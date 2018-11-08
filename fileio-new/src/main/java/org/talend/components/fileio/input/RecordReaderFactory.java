package org.talend.components.fileio.input;

import java.io.IOException;

import org.talend.components.fileio.configuration.FileFormatConfiguration;
import org.talend.components.fileio.input.csv.CsvRecordReaderFactory;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

public class RecordReaderFactory {

    public static RecordReader createRecordReader(FileFormatConfiguration fileFormatConfig, InputStreamProvider provider,
            RecordBuilderFactory factory) throws IOException {
        return createRecordReaderFactory(fileFormatConfig).createRecordReader(fileFormatConfig, provider, factory);
    }

    public static AbstractRecordReaderFactory createRecordReaderFactory(FileFormatConfiguration fileFormatConfig) {
        switch (fileFormatConfig.getFormat()) {
        case CSV:
            return new CsvRecordReaderFactory();
        default:
            throw new UnsupportedOperationException("Unknown format: " + fileFormatConfig.getFormat());
        }
    }

}
