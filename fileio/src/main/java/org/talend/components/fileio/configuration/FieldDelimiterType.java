package org.talend.components.fileio.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
