// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.components.fileio.s3;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.values.PCollection;
import org.apache.hadoop.fs.FileSystem;
import org.junit.Rule;
import org.junit.Test;
import org.talend.components.adapter.beam.transform.ConvertToIndexedRecord;
import org.talend.components.fileio.configuration.FieldDelimiterType;
import org.talend.components.fileio.configuration.RecordDelimiterType;
import org.talend.components.fileio.configuration.SimpleFileIOFormat;
import org.talend.components.test.MiniDfsResource;
import org.talend.components.test.SparkIntegrationTestResource;

public class S3OutputRuntimeTestIT {

    /** Set up credentials for integration tests. */
    @Rule
    public S3TestResource s3 = S3TestResource.of();

    @Rule
    public SparkIntegrationTestResource spark = SparkIntegrationTestResource.ofLocal();

    @Test
    public void testCsv_merge() throws IOException {
        S3DataSet dataset = s3.createS3DataSet();
        dataset.setFormat(SimpleFileIOFormat.CSV);
        dataset.setRecordDelimiter(RecordDelimiterType.LF);
        dataset.setFieldDelimiter(FieldDelimiterType.SEMICOLON);

        S3OutputConfig outputConfig = new S3OutputConfig();
        outputConfig.setDataset(dataset);
        outputConfig.setMergeOutput(true);

        // Create the runtime.
        S3Output runtime = new S3Output(outputConfig);

        // Use the runtime in a Spark pipeline to test.
        final Pipeline p = spark.createPipeline();
        PCollection<IndexedRecord> input = p.apply( //
                Create.of(ConvertToIndexedRecord.convertToAvro(new String[] { "1", "one" }), //
                        ConvertToIndexedRecord.convertToAvro(new String[] { "2", "two" }))); //
        input.apply(runtime);

        // And run the test.
        p.run().waitUntilFinish();

        FileSystem s3FileSystem = S3Service.createFileSystem(dataset);
        MiniDfsResource.assertReadFile(s3FileSystem, s3.getS3APath(dataset), "1;one", "2;two");
        MiniDfsResource.assertFileNumber(s3FileSystem, s3.getS3APath(dataset), 1);

    }

    @Test
    public void testAvro_merge() throws IOException {
        S3DataSet dataset = s3.createS3DataSet();
        dataset.setFormat(SimpleFileIOFormat.AVRO);

        S3OutputConfig outputConfig = new S3OutputConfig();
        outputConfig.setDataset(dataset);
        outputConfig.setMergeOutput(true);

        // Create the runtime.
        S3Output runtime = new S3Output(outputConfig);

        // Use the runtime in a Spark pipeline to test.
        final Pipeline p = spark.createPipeline();
        PCollection<IndexedRecord> input = p.apply( //
                Create.of(ConvertToIndexedRecord.convertToAvro(new String[] { "1", "one" }), //
                        ConvertToIndexedRecord.convertToAvro(new String[] { "2", "two" }))); //
        input.apply(runtime);

        // And run the test.
        p.run().waitUntilFinish();

        FileSystem s3FileSystem = S3Service.createFileSystem(dataset);
        MiniDfsResource.assertReadAvroFile(s3FileSystem, s3.getS3APath(dataset),
                new HashSet<IndexedRecord>(Arrays.asList(ConvertToIndexedRecord.convertToAvro(new String[] { "1", "one" }), //
                        ConvertToIndexedRecord.convertToAvro(new String[] { "2", "two" }))),
                false);
        MiniDfsResource.assertFileNumber(s3FileSystem, s3.getS3APath(dataset), 1);

    }

    @Test
    public void testParquet_merge() throws IOException {
        S3DataSet dataset = s3.createS3DataSet();
        dataset.setFormat(SimpleFileIOFormat.PARQUET);

        S3OutputConfig outputConfig = new S3OutputConfig();
        outputConfig.setDataset(dataset);
        outputConfig.setMergeOutput(true);

        // Create the runtime.
        S3Output runtime = new S3Output(outputConfig);

        // Use the runtime in a Spark pipeline to test.
        final Pipeline p = spark.createPipeline();
        PCollection<IndexedRecord> input = p.apply( //
                Create.of(ConvertToIndexedRecord.convertToAvro(new String[] { "1", "one" }), //
                        ConvertToIndexedRecord.convertToAvro(new String[] { "2", "two" }))); //
        input.apply(runtime);

        // And run the test.
        p.run().waitUntilFinish();

        FileSystem s3FileSystem = S3Service.createFileSystem(dataset);
        MiniDfsResource.assertReadParquetFile(s3FileSystem, s3.getS3APath(dataset),
                new HashSet<IndexedRecord>(Arrays.asList(ConvertToIndexedRecord.convertToAvro(new String[] { "1", "one" }), //
                        ConvertToIndexedRecord.convertToAvro(new String[] { "2", "two" }))),
                false);
        MiniDfsResource.assertFileNumber(s3FileSystem, s3.getS3APath(dataset), 1);

    }

}
