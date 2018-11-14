package org.talend.components.fileio.output;

import java.io.IOException;

import org.talend.sdk.component.api.record.Record;

public interface RecordProcessor {

	public void processRecord(Record record) throws IOException;
	
}
