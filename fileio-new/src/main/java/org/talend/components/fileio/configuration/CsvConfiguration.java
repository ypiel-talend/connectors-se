package org.talend.components.fileio.configuration;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.OptionsOrder;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@Documentation("Csv configuration properties.")
@OptionsOrder({ "recordDelimiter", "specificRecordDelimiter", "fieldDelimiter", "specificFieldDelimiter",
        "textEnclosureCharacter", "escapeCharacter", "encoding4CSV", "specificEncoding4CSV", "setHeaderLine4CSV",
        "headerLine4CSV" })
public class CsvConfiguration implements Serializable {

    @Option
    @Documentation("The record delimiter to split the file in records")
    private RecordDelimiterType recordDelimiter = RecordDelimiterType.LF;

    @Option
    @ActiveIf(target = "recordDelimiter", value = "OTHER")
    @Documentation("A custom delimiter if `recordDelimiter` is `OTHER`")
    private String specificRecordDelimiter = ";";

    @Option
    @Documentation("The field delimiter to split the records in columns")
    private FieldDelimiterType fieldDelimiter = FieldDelimiterType.SEMICOLON;

    @Option
    @ActiveIf(target = "fieldDelimiter", value = "OTHER")
    @Documentation("A custom delimiter if `fieldDelimiter` is `OTHER`")
    private String specificFieldDelimiter = ";";

    @Option
    @Documentation("Select a encoding type for CSV")
    private EncodingType encoding4CSV = EncodingType.UTF8;

    @Option
    @ActiveIf(target = "encoding4CSV", value = "OTHER")
    @Documentation("Set the custom encoding for CSV")
    private String specificEncoding4CSV;

    @Option
    @Documentation("enable the header setting for CSV")
    @DefaultValue("true")
    private boolean setHeaderLine4CSV;

    @Option
    @ActiveIf(target = "setHeaderLine4CSV", value = "true")
    @Documentation("set the header number for CSV")
    @DefaultValue("1")
    private int headerLine4CSV;

    @Option
    @Documentation("set the text enclosure character")
    private String textEnclosureCharacter;

    @Option
    @Documentation("set the escape character")
    private String escapeCharacter;

}
