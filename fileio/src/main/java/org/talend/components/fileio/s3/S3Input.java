package org.talend.components.fileio.s3;

import static org.talend.sdk.component.api.component.Icon.IconType.FILE_S3_O;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.talend.components.simplefileio.runtime.s3.S3InputRuntime;
import org.talend.components.simplefileio.runtime.s3.S3OutputRuntime;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.Processor;

@Version
@Icon(FILE_S3_O)
@Processor(name = "S3Input")
@Documentation("This component reads data from S3.")
public class S3Input extends PTransform<PBegin, PCollection<IndexedRecord>> {

    private final S3DataSet configuration;

    private final S3ConfigurationService service;

    public S3Input(@Option("configuration") final S3DataSet dataSet, final S3ConfigurationService service) {
        this.configuration = dataSet;
        this.service = service;
    }

    @Override
    public PCollection<IndexedRecord> expand(final PBegin input) {
        final S3InputRuntime runtime = new S3InputRuntime();
        runtime.initialize(null, service.toInputConfiguration(configuration));
        return runtime.expand(input);
    }
}
