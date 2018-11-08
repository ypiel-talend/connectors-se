package org.talend.components.fileio.input;

import java.io.IOException;

import org.talend.components.fileio.configuration.FileFormatConfiguration;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

public interface AbstractRecordReaderFactory {

    public RecordReader createRecordReader(FileFormatConfiguration configuration, InputStreamProvider streamProvider,
            RecordBuilderFactory builderFactory) throws IOException;

}
