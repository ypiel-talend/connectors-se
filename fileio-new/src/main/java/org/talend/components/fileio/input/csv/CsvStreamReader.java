package org.talend.components.fileio.input.csv;

import java.util.Scanner;

import org.talend.components.fileio.configuration.CsvConfiguration;
import org.talend.components.fileio.configuration.RecordDelimiterType;
import org.talend.components.fileio.input.InputStreamProvider;
import org.talend.components.fileio.input.StreamReader;

public class CsvStreamReader implements StreamReader<String> {

    private final InputStreamProvider streamProvider;

    private final CsvConfiguration configuration;

    private Scanner scanner;

    public CsvStreamReader(final InputStreamProvider streamProvider, final CsvConfiguration configuration) {
        this.streamProvider = streamProvider;
        this.configuration = configuration;
        init();
    }

    private void init() {
        String rowDelimiter;
        if (RecordDelimiterType.OTHER.equals(configuration.getRecordDelimiter())) {
            rowDelimiter = configuration.getSpecificRecordDelimiter();
        } else {
            rowDelimiter = configuration.getRecordDelimiter().getDelimiter();
        }
        this.scanner = new Scanner(streamProvider.getInputStream());
        scanner.useDelimiter(rowDelimiter);
    }

    @Override
    public String nextData() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return null;
    }

}
