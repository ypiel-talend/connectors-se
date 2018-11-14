package org.talend.components.fileio.output.csv;

import org.talend.components.fileio.configuration.CsvConfiguration;
import org.talend.components.fileio.configuration.FileFormatConfiguration;
import org.talend.components.fileio.output.AbstractRecordProcessorFactory;
import org.talend.components.fileio.output.DataWriter;
import org.talend.components.fileio.output.DefaultRecordProcessor;
import org.talend.components.fileio.output.OutputStreamProvider;
import org.talend.components.fileio.output.RecordFormatter;
import org.talend.components.fileio.output.RecordProcessor;

public class CsvRecordProcessorFactory implements AbstractRecordProcessorFactory {

	private DataWriter<String> createDataWriter(OutputStreamProvider streamProvider, CsvConfiguration configuration) {
		return new CsvDataWriter(streamProvider, configuration);
	}
	
	private RecordFormatter<String> createRecordFormatter(CsvConfiguration configuration) {
		return new CsvRecordFormatter(configuration);
	}
	
	@Override
	public RecordProcessor createProcessor(FileFormatConfiguration config, OutputStreamProvider streamProvider) {
		return new DefaultRecordProcessor<>(createDataWriter(streamProvider, config.getCsvConfiguration()), 
				createRecordFormatter(config.getCsvConfiguration()));
	}

}
