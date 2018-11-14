package org.talend.components.fileio.output;

import java.io.IOException;

import org.talend.sdk.component.api.record.Record;

public class DefaultRecordProcessor<T> implements RecordProcessor {

	private final DataWriter<T> dataWriter;
	
	private final RecordFormatter<T> recordFormatter;
	
	public DefaultRecordProcessor(DataWriter<T> dataWriter, RecordFormatter<T> recordFormatter) {
		this.dataWriter = dataWriter;
		this.recordFormatter = recordFormatter;
	}
	
	@Override
	public void processRecord(Record record) throws IOException {
		if(record == null) {
			return;
		}
		dataWriter.writeData(recordFormatter.formatRecord(record));
	}

}
