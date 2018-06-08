package org.talend.components.localio.fixed;

import static org.talend.sdk.component.api.component.Icon.IconType.FILE_O;

import java.io.Serializable;

import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.OptionsOrder;
import org.talend.sdk.component.api.configuration.ui.widget.Code;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Version
@Icon(FILE_O)
@DataSet("FixedDataSet")
@OptionsOrder({ "format", "recordDelimiter", "specificRecordDelimiter", "fieldDelimiter", "specificFieldDelimiter", "schema",
        "csvSchema", "values" })
public class FixedDataSet implements Serializable {

    @Option
    private FixedDatastore datastore;

    @Option
    @Required
    @Documentation("The content format (CSV, JSON, AVRO).")
    private RecordFormat format = RecordFormat.CSV;

    @Option
    @ActiveIf(target = "./format", value = "CSV")
    @Documentation("How to split records in the file stream.")
    private RecordDelimiterType recordDelimiter = RecordDelimiterType.LF;

    @Option
    @ActiveIf(target = "./format", value = "CSV")
    @ActiveIf(target = "./recordDelimiter", value = "OTHER")
    @Documentation("Overrides recordDelimiter with a custom value.")
    private String specificRecordDelimiter = RecordDelimiterType.LF.getDelimiter();

    @Option
    @ActiveIf(target = "./format", value = "CSV")
    @Documentation("How to split columns in a record.")
    private FieldDelimiterType fieldDelimiter = FieldDelimiterType.SEMICOLON;

    @Option
    @ActiveIf(target = "./format", value = "CSV")
    @ActiveIf(target = "./fieldDelimiter", value = "OTHER")
    @Documentation("Custom field delimiter, replaces fieldDelimiter.")
    private String specificFieldDelimiter = FieldDelimiterType.SEMICOLON.getDelimiter();

    @Option
    @ActiveIf(target = "./format", value = "AVRO")
    @Documentation("If format is AVRO the Avro schema.")
    private String schema;

    @Option
    @Code("json")
    @ActiveIf(target = "./format", value = "CSV")
    @Documentation("If format is CSV the csv schema.")
    private String csvSchema;

    @Option
    @Code("json")
    @Documentation("The values.")
    private String values;

    public String getRecordDelimiter() {
        if (RecordDelimiterType.OTHER == recordDelimiter) {
            return specificRecordDelimiter;
        }
        return recordDelimiter.getDelimiter();
    }

    public String getFieldDelimiter() {
        if (FieldDelimiterType.OTHER == fieldDelimiter) {
            return specificFieldDelimiter;
        }
        return fieldDelimiter.getDelimiter();
    }

    public enum RecordFormat {
        CSV,
        JSON,
        AVRO
    }

    @Getter
    @AllArgsConstructor
    public enum RecordDelimiterType {
        LF("\n"),
        CR("\r"),
        CRLF("\r\n"),
        OTHER("Other");

        private final String delimiter;
    }

    @Getter
    @AllArgsConstructor
    public enum FieldDelimiterType {
        SEMICOLON(";"),
        COMMA(","),
        TABULATION("\t"),
        SPACE(" "),
        OTHER("Other");

        private final String delimiter;
    }
}
