package org.talend.components.fileio.s3;

import static org.talend.sdk.component.api.component.Icon.IconType.FILE_S3_O;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.talend.components.simplefileio.runtime.SimpleFileIOAvroRegistry;
import org.talend.components.simplefileio.runtime.SimpleRecordFormatAvroIO;
import org.talend.components.simplefileio.runtime.SimpleRecordFormatBase;
import org.talend.components.simplefileio.runtime.SimpleRecordFormatCsvIO;
import org.talend.components.simplefileio.runtime.SimpleRecordFormatParquetIO;
import org.talend.components.simplefileio.runtime.s3.S3Connection;
import org.talend.components.simplefileio.runtime.ugi.UgiDoAs;
import org.talend.components.simplefileio.s3.input.S3InputProperties;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.meta.Documentation;

@Version
@Icon(FILE_S3_O)
@PartitionMapper(name = "S3Input")
@Documentation("This component reads data from S3.")
public class S3Input extends PTransform<PBegin, PCollection<IndexedRecord>> {

    static {
        // Ensure that the singleton for the SimpleFileIOAvroRegistry is created.
        SimpleFileIOAvroRegistry.get();
    }

    private final S3DataSet configuration;

    private final S3ConfigurationService service;

    public S3Input(@Option("configuration") final S3DataSet dataSet, final S3ConfigurationService service) {
        this.configuration = dataSet;
        this.service = service;
    }

    @Override
    public PCollection<IndexedRecord> expand(final PBegin input) {
        // The UGI does not control security for S3.
        final S3InputProperties properties = service.toInputConfiguration(configuration);
        UgiDoAs doAs = UgiDoAs.ofNone();
        String path = S3Connection.getUriPath(properties.getDatasetProperties());
        boolean overwrite = false; // overwrite is ignored for reads.
        int limit = properties.limit.getValue();
        boolean mergeOutput = false; // mergeOutput is ignored for reads.
        SimpleRecordFormatBase rf = null;
        switch (properties.getDatasetProperties().format.getValue()) {
        case AVRO:
            rf = new SimpleRecordFormatAvroIO(doAs, path, overwrite, limit, mergeOutput);
            break;
        case CSV:
            rf = new SimpleRecordFormatCsvIO(doAs, path, overwrite, limit, properties.getDatasetProperties().getRecordDelimiter(),
                    properties.getDatasetProperties().getFieldDelimiter(), mergeOutput);
            break;
        case PARQUET:
            rf = new SimpleRecordFormatParquetIO(doAs, path, overwrite, limit, mergeOutput);
            break;
        }

        if (rf == null) {
            throw new RuntimeException("To be implemented: " + properties.getDatasetProperties().format.getValue());
        }

        S3Connection.setS3Configuration(rf.getExtraHadoopConfiguration(), properties.getDatasetProperties());
        // Support jvm proxy
        final String host = System.getProperty("https.proxyHost", System.getProperty("http.proxyHost"));
        final int port = Integer.getInteger("https.proxyPort", Integer.getInteger("http.proxyPort", 0));
        final String user = System.getProperty("https.proxyUser", System.getProperty("http.proxyUser"));
        final String password = System.getProperty("https.proxyUser", System.getProperty("http.proxyUser"));
        if (host != null && port > 0) {
            rf.getExtraHadoopConfiguration().set("fs.s3a.proxy.host", host);
            rf.getExtraHadoopConfiguration().set("fs.s3a.proxy.port", String.valueOf(port));
            if (user != null && password != null) {
                rf.getExtraHadoopConfiguration().set("fs.s3a.proxy.user", user);
                rf.getExtraHadoopConfiguration().set("fs.s3a.proxy.password", password);
            }
        }
        return rf.read(input);
    }
}
