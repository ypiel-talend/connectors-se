package org.talend.components.fileio.s3;

import static org.talend.sdk.component.api.component.Icon.IconType.FILE_S3_O;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.talend.components.fileio.runtime.SimpleFileIOAvroRegistry;
import org.talend.components.fileio.runtime.SimpleRecordFormatAvroIO;
import org.talend.components.fileio.runtime.SimpleRecordFormatBase;
import org.talend.components.fileio.runtime.SimpleRecordFormatCsvIO;
import org.talend.components.fileio.runtime.SimpleRecordFormatExcelIO;
import org.talend.components.fileio.runtime.SimpleRecordFormatParquetIO;
import org.talend.components.fileio.runtime.ugi.UgiDoAs;
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
        // have to register the type converter firstly here now, not a good place in my view
        SimpleFileIOAvroRegistry.get();
    }

    private final S3DataSet configuration;

    public S3Input(@Option("configuration") final S3DataSet dataSet) {
        this.configuration = dataSet;
    }

    @Override
    public PCollection<IndexedRecord> expand(final PBegin in) {
        // The UGI does not control security for S3.
        UgiDoAs doAs = UgiDoAs.ofNone();
        String path = S3Service.getUriPath(configuration);
        boolean overwrite = false; // overwrite is ignored for reads.
        int limit = configuration.getLimit();
        boolean mergeOutput = false; // mergeOutput is ignored for reads.

        SimpleRecordFormatBase rf = null;
        switch (configuration.getFormat()) {

        case AVRO:
            rf = new SimpleRecordFormatAvroIO(doAs, path, overwrite, limit, mergeOutput);
            break;

        case CSV:
            rf = new SimpleRecordFormatCsvIO(doAs, path, limit, configuration.getRecordDelimiterValue(),
                    configuration.getFieldDelimiterValue(), configuration.getEncodingValue(), configuration.getHeaderLineValue(),
                    configuration.getTextEnclosureCharacter(), configuration.getEscapeCharacter());
            break;

        case PARQUET:
            rf = new SimpleRecordFormatParquetIO(doAs, path, overwrite, limit, mergeOutput);
            break;

        case EXCEL:
            rf = new SimpleRecordFormatExcelIO(doAs, path, overwrite, limit, mergeOutput, configuration.getEncodingValue(),
                    configuration.getSheet(), configuration.getHeaderLineValue(), configuration.getFooterLineValue(),
                    configuration.getExcelFormat());
            break;
        }

        if (rf == null) {
            throw new RuntimeException("To be implemented: " + configuration.getFormat());
        }

        S3Service.setS3Configuration(rf.getExtraHadoopConfiguration(), configuration);
        return rf.read(in);
    }
}
