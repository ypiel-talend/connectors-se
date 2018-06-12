package org.talend.components.localio.fixed;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;
import static org.talend.sdk.component.runtime.manager.ComponentManager.ComponentType.MAPPER;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.talend.daikon.avro.GenericDataRecordHelper;
import org.talend.sdk.component.junit.ComponentsHandler;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;

@WithComponents("org.talend.components.localio")
class FixedFlowInputTest {

    private static Schema refSchema;

    private static IndexedRecord refRecord;

    @Injected
    private ComponentsHandler handler;

    @BeforeAll
    static void init() {
        Object[] inputAsObject1 = new Object[] { "rootdata",
                new Object[] { "subdata", new Object[] { "subsubdata1", 28, 42L }, "subdata2" } };
        refSchema = GenericDataRecordHelper.createSchemaFromObject("MyRecord", inputAsObject1);
        refRecord = GenericDataRecordHelper.createRecord(inputAsObject1);
    }

    @Test
    void input() throws IOException {
        final FixedFlowInputConfiguration configuration = new FixedFlowInputConfiguration();
        configuration.setNbRows(2);
        configuration.setSchema(refSchema.toString());
        configuration.setValues(generateInputJSON(refSchema, refRecord));
        final Map<String, String> asConfig = configurationByExample().forInstance(configuration).configured().toMap();
        final Pipeline pipeline = Pipeline.create();
        final PTransform<PBegin, PCollection<IndexedRecord>> input = handler.asManager()
                .createComponent("LocalIO", "FixedFlowInput", MAPPER, 1, asConfig)
                .map(e -> (PTransform<PBegin, PCollection<IndexedRecord>>) e)
                .orElseThrow(() -> new IllegalArgumentException("No component for fixed flow input"));
        PAssert.that(pipeline.apply(input)).satisfies(it -> {
            final List<IndexedRecord> records = StreamSupport.stream(it.spliterator(), false).collect(toList());
            assertEquals(2, records.size());
            records.forEach(record -> assertEquals(record.toString(), refRecord.toString()));
            return null;
        });
        pipeline.run().waitUntilFinish();
    }

    private static String generateInputJSON(final Schema inputSchema, final IndexedRecord inputIndexedRecord) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DatumWriter<IndexedRecord> writer = new GenericDatumWriter<IndexedRecord>(inputSchema);
        final JsonEncoder encoder = EncoderFactory.get().jsonEncoder(inputSchema, baos, false);
        writer.write(inputIndexedRecord, encoder);
        encoder.flush();
        baos.flush();
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }
}
