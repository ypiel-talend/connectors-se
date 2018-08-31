package org.talend.components.fileio.hdfs;

import static org.talend.sdk.component.api.component.Icon.IconType.FILE_HDFS_O;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.talend.components.fileio.runtime.SimpleRecordFormat;
import org.talend.components.fileio.runtime.SimpleRecordFormatAvroIO;
import org.talend.components.fileio.runtime.SimpleRecordFormatCsvIO;
import org.talend.components.fileio.runtime.SimpleRecordFormatExcelIO;
import org.talend.components.fileio.runtime.SimpleRecordFormatParquetIO;
import org.talend.components.fileio.runtime.ugi.UgiDoAs;
import org.talend.components.fileio.runtime.ugi.UgiExceptionHandler;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.meta.Documentation;

@Version
@Icon(FILE_HDFS_O)
@PartitionMapper(name = "SimpleFileIOInput")
@Documentation("This component reads data from HDFS.")
public class SimpleFileIOInput extends PTransform<PBegin, PCollection<IndexedRecord>> {

    private final SimpleFileIODataSet configuration;

    public SimpleFileIOInput(@Option("configuration") final SimpleFileIODataSet configuration) {
        this.configuration = configuration;
    }

    @Override
    public PCollection<IndexedRecord> expand(final PBegin in) {
        // Controls the access security on the cluster.
        UgiDoAs doAs = SimpleFileIOService.getReadWriteUgiDoAs(configuration, UgiExceptionHandler.AccessType.Read);
        String path = configuration.getPath();
        boolean overwrite = false; // overwrite is ignored for reads.
        int limit = configuration.getLimit();
        boolean mergeOutput = false; // mergeOutput is ignored for reads.

        SimpleRecordFormat rf = null;
        switch (configuration.getFormat()) {

        case AVRO:
            rf = new SimpleRecordFormatAvroIO(doAs, path, overwrite, limit, mergeOutput);
            break;

        case CSV:
            rf = new SimpleRecordFormatCsvIO(doAs, path, limit, configuration.getRecordDelimiter().getDelimiter(),
                    configuration.getFieldDelimiter().getDelimiter(), configuration.getEncoding4CSV().getEncoding(),
                    configuration.getHeaderLine4CSV(), configuration.getTextEnclosureCharacter(),
                    configuration.getEscapeCharacter());
            break;

        case PARQUET:
            rf = new SimpleRecordFormatParquetIO(doAs, path, overwrite, limit, mergeOutput);
            break;

        case EXCEL:
            rf = new SimpleRecordFormatExcelIO(doAs, path, overwrite, limit, mergeOutput,
                    configuration.getEncoding4EXCEL().getEncoding(), configuration.getSheet(),
                    configuration.getHeaderLine4EXCEL(), configuration.getFooterLine4EXCEL(), configuration.getExcelFormat());
            break;
        }

        if (rf == null) {
            throw new RuntimeException("To be implemented: " + configuration.getFormat());
        }

        return rf.read(in);
    }
}
