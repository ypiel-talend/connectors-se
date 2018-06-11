package org.talend.components.fileio.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecordDelimiterType {
    LF("\n"),
    CR("\r"),
    CRLF("\r\n"),
    OTHER("Other");

    private final String delimiter;
}
