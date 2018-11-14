package org.talend.components.fileio.output;

import org.talend.sdk.component.api.record.Record;

public interface RecordFormatter<T> {

	T formatRecord(Record record);
	
}
