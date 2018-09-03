package org.talend.components.fileio.hdfs;

import static org.talend.sdk.component.api.component.Icon.IconType.FILE_HDFS_O;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.PrivilegedExceptionAction;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.fileio.runtime.SimpleFileIOErrorCode;
import org.talend.components.fileio.runtime.SimpleRecordFormat;
import org.talend.components.fileio.runtime.SimpleRecordFormatAvroIO;
import org.talend.components.fileio.runtime.SimpleRecordFormatCsvIO;
import org.talend.components.fileio.runtime.SimpleRecordFormatParquetIO;
import org.talend.components.fileio.runtime.ugi.UgiDoAs;
import org.talend.components.fileio.runtime.ugi.UgiExceptionHandler;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.Processor;

@Version
@Icon(FILE_HDFS_O)
@Processor(name = "SimpleFileIOOutput")
@Documentation("This component writes data to HDFS.")
public class SimpleFileIOOutput extends PTransform<PCollection<IndexedRecord>, PDone> {

    private final SimpleFileIOOutputConfig configuration;

    public SimpleFileIOOutput(@Option("configuration") final SimpleFileIOOutputConfig configuration) {
        this.configuration = configuration;
    }

    @Override
    public PDone expand(final PCollection<IndexedRecord> in) {
        // Controls the access security on the cluster.
        UgiDoAs doAs = SimpleFileIOService.getReadWriteUgiDoAs(configuration.getDataset(), UgiExceptionHandler.AccessType.Write);
        String path = configuration.getDataset().getPath();
        boolean overwrite = configuration.isOverwrite();
        int limit = -1; // limit is ignored for sinks
        boolean mergeOutput = configuration.isMergeOutput();

        SimpleRecordFormat rf = null;
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

        try {
            return rf.write(in);
        } catch (IllegalStateException rte) {
            // Unable to overwrite exceptions are handled here.
            if (rte.getMessage().startsWith("Output path") && rte.getMessage().endsWith("already exists")) {
                throw SimpleFileIOErrorCode.createOutputAlreadyExistsException(rte, path);
            } else {
                throw rte;
            }
        }
    }

    // TODO copy it from the tcompv0 output runtime, what is used for? need to recheck it when runtime platform is ready
    public void runAtDriver() {
        if (configuration.isOverwrite()) {
            UgiDoAs doAs = SimpleFileIOService.getReadWriteUgiDoAs(configuration.getDataset(),
                    UgiExceptionHandler.AccessType.Write);
            try {
                doAs.doAs(new PrivilegedExceptionAction<Void>() {

                    @Override
                    public Void run() throws IOException, URISyntaxException {
                        Path p = new Path(configuration.getDataset().getPath());
                        FileSystem fs = p.getFileSystem(new Configuration());
                        if (fs.exists(p)) {
                            boolean deleted = fs.delete(p, true);
                            if (!deleted)
                                throw SimpleFileIOErrorCode.createOutputNotAuthorized(null, null,
                                        configuration.getDataset().getPath());
                        }
                        return null;
                    }
                });
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw TalendRuntimeException.createUnexpectedException(e);
            }
        }
    }
}
