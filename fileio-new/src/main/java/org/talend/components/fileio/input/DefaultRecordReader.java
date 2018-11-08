package org.talend.components.fileio.input;

import java.io.IOException;

import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;

public class DefaultRecordReader<T> implements RecordReader {

    private final RecordParser<T> parser;

    private final StreamReader<T> streamReader;

    private Schema schema;

    private final int headerLine;

    public DefaultRecordReader(final RecordParser<T> parser, final StreamReader<T> streamReader, final int headerLine)
            throws IOException {
        this.parser = parser;
        this.streamReader = streamReader;
        this.headerLine = headerLine;
        init();
    }

    protected void init() throws IOException {
        int lineNumber = 0;
        T row = null;
        while (lineNumber < headerLine) {
            row = streamReader.nextData();
            lineNumber++;
        }
        if (row != null) {
            this.schema = parser.createSchema(streamReader.nextData(), true);
        }
    }

    @Override
    public Record nextRecord() throws IOException {
        T row = streamReader.nextData();
        if (row == null) {
            return null;
        }
        if (schema == null) {
            schema = parser.createSchema(row, false);
        }
        return parser.createRecord(row, schema);
    }

}
