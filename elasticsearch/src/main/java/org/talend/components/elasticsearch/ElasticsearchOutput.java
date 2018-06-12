package org.talend.components.elasticsearch;

import static org.talend.sdk.component.api.component.Icon.IconType.ELASTIC;

import java.io.StringWriter;
import java.util.stream.Stream;

import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;

import org.apache.beam.sdk.io.elasticsearch.ElasticsearchIO;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.meta.Documentation;

@Version
@Icon(ELASTIC)
@PartitionMapper(name = "ElasticsearchOutput")
@Documentation("This component writes data to Elasticsearch.")
public class ElasticsearchOutput extends PTransform<PCollection<JsonObject>, PDone> {

    private final ElasticsearchDataSet configuration;

    private final JsonWriterFactory writerFactory;

    public ElasticsearchOutput(@Option("configuration") final ElasticsearchDataSet configuration,
            final JsonWriterFactory writerFactory) {
        this.configuration = configuration;
        this.writerFactory = writerFactory;
    }

    @Override
    public PDone expand(final PCollection<JsonObject> input) {
        return input.apply("JsonToString", ParDo.of(new DoFn<JsonObject, String>() {

            @ProcessElement
            public void processElement(final ProcessContext c) {
                final StringWriter output = new StringWriter();
                try (final JsonWriter writer = writerFactory.createWriter(output)) {
                    writer.writeObject(c.element());
                }
                c.output(output.toString());
            }
        })).apply(configure());
    }

    private ElasticsearchIO.Write configure() {
        return ElasticsearchIO.write()
                .withConnectionConfiguration(ElasticsearchIO.ConnectionConfiguration.create(
                        Stream.of(configuration.getDatastore().getNodes().split(",")).map(String::trim).toArray(String[]::new),
                        configuration.getIndex(), configuration.getType()));
    }
}
