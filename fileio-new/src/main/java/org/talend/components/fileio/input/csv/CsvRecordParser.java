package org.talend.components.fileio.input.csv;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.talend.components.fileio.configuration.CsvConfiguration;
import org.talend.components.fileio.configuration.EncodingType;
import org.talend.components.fileio.input.RecordParser;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.record.Schema.Entry;
import org.talend.sdk.component.api.record.Schema.Type;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

public class CsvRecordParser implements RecordParser<String> {

    private final CSVFormat csvFormat;

    private final String encoding;

    private final RecordBuilderFactory factory;

    public CsvRecordParser(final CsvConfiguration config, final RecordBuilderFactory factory) {
        this.csvFormat = CsvFormatFactory.createFormat(config);
        this.factory = factory;
        if (EncodingType.OTHER.equals(config.getEncoding4CSV())) {
            encoding = config.getSpecificEncoding4CSV();
        } else {
            encoding = config.getEncoding4CSV().getEncoding();
        }
    }

    @Override
    public Schema createSchema(String inputRow, boolean header) throws IOException {
        String rowValue = new String(inputRow.getBytes(), encoding);
        Schema.Builder schemaBuilder = factory.newSchemaBuilder(Type.RECORD);
        for (CSVRecord record : csvFormat.parse(new StringReader(rowValue))) {
            for (int i = 0; i < record.size(); i++) {
                String fieldName = record.get(i);
                if (!header) {
                    fieldName = FIELD_NAME_PREFIX + i;
                }
                schemaBuilder.withEntry(
                        factory.newEntryBuilder().withName(fieldName).withNullable(true).withType(Type.STRING).build());
            }
            break;
        }
        return schemaBuilder.build();
    }

    @Override
    public Record createRecord(String inputRow, Schema schema) throws IOException {
        String rowValue = new String(inputRow.getBytes(), encoding);
        Record.Builder recordBuilder = factory.newRecordBuilder();
        List<Entry> schemaEntries = schema.getEntries();
        for (CSVRecord record : csvFormat.parse(new StringReader(rowValue))) {
            for (int i = 0; i < record.size(); i++) {
                recordBuilder.withString(schemaEntries.get(i), record.get(i));
            }
        }
        return recordBuilder.build();
    }

}
