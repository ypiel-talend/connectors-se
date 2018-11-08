package org.talend.components.fileio.input;

import java.io.IOException;

import org.talend.sdk.component.api.record.Record;

public interface RecordReader {

    Record nextRecord() throws IOException;

}
