package org.talend.components.fileio.output.csv;

import java.io.IOException;

import org.talend.components.fileio.configuration.CsvConfiguration;
import org.talend.components.fileio.configuration.RecordDelimiterType;
import org.talend.components.fileio.output.DataWriter;
import org.talend.components.fileio.output.OutputStreamProvider;

public class CsvDataWriter implements DataWriter<String> {

	private final OutputStreamProvider streamProvider;
	
	private final String recordDelimiter;
	
	public CsvDataWriter(final OutputStreamProvider streamProvider, final CsvConfiguration configuration) {
		this.streamProvider = streamProvider;
		if(configuration.getRecordDelimiter() == RecordDelimiterType.OTHER) {
			recordDelimiter = configuration.getSpecificRecordDelimiter();
		} else {
			recordDelimiter = configuration.getRecordDelimiter().getDelimiter();
		}
	}
	
	@Override
	public void writeData(String data) throws IOException {
		streamProvider.getOutputStream().write(data.getBytes());
		streamProvider.getOutputStream().write(recordDelimiter.getBytes());
	}

}
