package org.talend.components.fileio.input.csv;

import java.io.IOException;

import org.talend.components.fileio.configuration.CsvConfiguration;
import org.talend.components.fileio.configuration.FileFormatConfiguration;
import org.talend.components.fileio.input.AbstractRecordReaderFactory;
import org.talend.components.fileio.input.DefaultRecordReader;
import org.talend.components.fileio.input.InputStreamProvider;
import org.talend.components.fileio.input.RecordParser;
import org.talend.components.fileio.input.RecordReader;
import org.talend.components.fileio.input.StreamReader;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

public class CsvRecordReaderFactory implements AbstractRecordReaderFactory {

    public RecordParser<String> createRecordParser(CsvConfiguration config, RecordBuilderFactory factory) {
        return new CsvRecordParser(config, factory);
    }

    public StreamReader<String> createStreamReader(InputStreamProvider streamProvider, CsvConfiguration configuration) {
        return new CsvStreamReader(streamProvider, configuration);
    }

    @Override
    public RecordReader createRecordReader(FileFormatConfiguration configuration, InputStreamProvider streamProvider,
            RecordBuilderFactory builderFactory) throws IOException {
        CsvConfiguration csvConfiguration = configuration.getCsvConfiguration();
        int headerLine = 0;
        if (csvConfiguration.isSetHeaderLine4CSV() && csvConfiguration.getHeaderLine4CSV() > 0) {
            headerLine = csvConfiguration.getHeaderLine4CSV();
        }
        return new DefaultRecordReader<>(createRecordParser(csvConfiguration, builderFactory),
                createStreamReader(streamProvider, csvConfiguration), headerLine);
    }

}
