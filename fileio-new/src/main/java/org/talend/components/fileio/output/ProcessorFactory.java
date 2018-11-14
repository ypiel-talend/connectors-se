package org.talend.components.fileio.output;

import org.talend.components.fileio.configuration.FileFormatConfiguration;
import org.talend.components.fileio.output.csv.CsvRecordProcessorFactory;

public class ProcessorFactory {

	public static RecordProcessor createProcessor(FileFormatConfiguration config, OutputStreamProvider streamProvider) {
		return createAbstractProcessorFactory(config).createProcessor(config, streamProvider);
	}
	
	private static AbstractRecordProcessorFactory createAbstractProcessorFactory(FileFormatConfiguration config) {
		switch(config.getFormat()) {
		case CSV:
			return new CsvRecordProcessorFactory();
		default:
			throw new IllegalArgumentException("Unknown format " + config.getFormat());
		}
	}
	
}
