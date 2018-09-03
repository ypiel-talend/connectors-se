package org.talend.components.fileio.s3;

import static org.talend.sdk.component.api.component.Icon.IconType.FILE_S3_O;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.talend.components.fileio.runtime.ExtraHadoopConfiguration;
import org.talend.components.fileio.runtime.SimpleFileIOErrorCode;
import org.talend.components.fileio.runtime.SimpleRecordFormatAvroIO;
import org.talend.components.fileio.runtime.SimpleRecordFormatBase;
import org.talend.components.fileio.runtime.SimpleRecordFormatCsvIO;
import org.talend.components.fileio.runtime.SimpleRecordFormatParquetIO;
import org.talend.components.fileio.runtime.ugi.UgiDoAs;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.Processor;

@Version
@Icon(FILE_S3_O)
@Processor(name = "S3Output")
@Documentation("This component writes data to S3.")
public class S3Output extends PTransform<PCollection<IndexedRecord>, PDone> {

    private final S3OutputConfig configuration;

    public S3Output(@Option("configuration") final S3OutputConfig dataSet) {
        this.configuration = dataSet;
    }

    @Override
    public PDone expand(final PCollection<IndexedRecord> in) {
        // The UGI does not control security for S3.
        UgiDoAs doAs = UgiDoAs.ofNone();
        String path = S3Service.getUriPath(configuration.getDataset());
        boolean overwrite = configuration.isOverwrite();
        int limit = -1; // limit is ignored for sinks
        boolean mergeOutput = configuration.isMergeOutput();

        SimpleRecordFormatBase rf = null;
        switch (configuration.getDataset().getFormat()) {

        case AVRO:
            rf = new SimpleRecordFormatAvroIO(doAs, path, overwrite, limit, mergeOutput);
            break;

        case CSV:
            rf = new SimpleRecordFormatCsvIO(doAs, path, overwrite, limit, configuration.getDataset().getRecordDelimiterValue(),
                    configuration.getDataset().getFieldDelimiterValue(), mergeOutput);
            break;

        case PARQUET:
            rf = new SimpleRecordFormatParquetIO(doAs, path, overwrite, limit, mergeOutput);
            break;
        }

        if (rf == null) {
            throw new RuntimeException("To be implemented: " + configuration.getDataset().getFormat());
        }

        S3Service.setS3Configuration(rf.getExtraHadoopConfiguration(), configuration.getDataset());
        return rf.write(in);
    }

    // TODO copy it from the tcompv0 output runtime, what is used for? need to recheck it when runtime platform is ready
    public void runAtDriver() {
        if (configuration.isOverwrite()) {
            try {
                Path p = new Path(S3Service.getUriPath(configuration.getDataset()));

                // Add the AWS configuration to the Hadoop filesystem.
                ExtraHadoopConfiguration awsConf = new ExtraHadoopConfiguration();
                S3Service.setS3Configuration(awsConf, configuration.getDataset());
                Configuration hadoopConf = new Configuration();
                awsConf.addTo(hadoopConf);
                FileSystem fs = p.getFileSystem(hadoopConf);

                if (fs.exists(p)) {
                    boolean deleted = fs.delete(p, true);
                    if (!deleted)
                        throw SimpleFileIOErrorCode.createOutputNotAuthorized(null, null, p.toString());
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw TalendRuntimeException.createUnexpectedException(e);
            }
        }
    }
}
