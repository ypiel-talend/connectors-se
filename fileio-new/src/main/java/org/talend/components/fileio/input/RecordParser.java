package org.talend.components.fileio.input;

import java.io.IOException;

import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;

public interface RecordParser<T> {

    public static final String FIELD_NAME_PREFIX = "field";

    Schema createSchema(T inputRow, boolean header) throws IOException;

    Record createRecord(T inputRow, Schema schema) throws IOException;

}
