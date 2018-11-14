package org.talend.components.fileio.common.csv;

import org.apache.commons.csv.CSVFormat;
import org.talend.components.fileio.configuration.CsvConfiguration;
import org.talend.components.fileio.configuration.FieldDelimiterType;

public class CsvFormatFactory {

    private CsvFormatFactory() {
    }

    public static CSVFormat createFormat(CsvConfiguration config) {
        CSVFormat csvFormat = CSVFormat.RFC4180;
        String fieldDelimiter;
        if (config.getFieldDelimiter() != FieldDelimiterType.OTHER) {
            fieldDelimiter = config.getFieldDelimiter().getDelimiter();
        } else {
            fieldDelimiter = config.getSpecificFieldDelimiter();
        }
        csvFormat = csvFormat.withDelimiter(fieldDelimiter.charAt(0));
        if (config.getTextEnclosureCharacter() != null) {
            csvFormat = csvFormat.withQuote(config.getTextEnclosureCharacter().charAt(0));
        } else {
            csvFormat = csvFormat.withQuote(null);
        }

        if (config.getEscapeCharacter() != null) {
            csvFormat = csvFormat.withEscape(config.getEscapeCharacter().charAt(0));
        }
        return csvFormat;
    }

}
