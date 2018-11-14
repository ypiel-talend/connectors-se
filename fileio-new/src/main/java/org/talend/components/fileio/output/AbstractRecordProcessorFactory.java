package org.talend.components.fileio.output;

import org.talend.components.fileio.configuration.FileFormatConfiguration;

public interface AbstractRecordProcessorFactory {

	RecordProcessor createProcessor(FileFormatConfiguration config, OutputStreamProvider streamProvider);
	
}
