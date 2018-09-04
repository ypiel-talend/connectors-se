// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.fileio.hdfs;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.testing.ValidatesRunner;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.values.PCollection;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;
import org.talend.components.adapter.beam.transform.ConvertToIndexedRecord;
import org.talend.components.fileio.configuration.SimpleFileIOFormat;
import org.talend.components.test.MiniDfsResource;
import org.talend.components.test.SparkIntegrationTestResource;

/**
 * Unit tests for {@link SimpleFileIOOutputRuntime} using the Spark runner.
 */
public class SparkSimpleFileIOOutputRuntimeTestIT {

    /** Resource that provides a {@link Pipeline} configured for Spark. */
    @Rule
    public SparkIntegrationTestResource spark = SparkIntegrationTestResource.ofLocal();

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private static SimpleFileIOOutputConfig createSimpleFileIOOutputConfig() {
        SimpleFileIOOutputConfig config = new SimpleFileIOOutputConfig();
        SimpleFileIODataSet dataset = new SimpleFileIODataSet();
        SimpleFileIODataStore datastore = new SimpleFileIODataStore();
        dataset.setDatastore(datastore);
        config.setDataset(dataset);
        return config;
    }

    /**
     * Basic unit test using all default values (except for the path) on an in-memory DFS cluster.
     */
    @Category(ValidatesRunner.class)
    @Ignore("BEAM-1206")
    @Test
    public void testBasicDefaults() throws IOException {
        FileSystem fs = FileSystem.get(spark.createHadoopConfiguration());
        String fileSpec = fs.getUri().resolve(new Path(tmp.getRoot().toString(), "basic").toUri()).toString();

        // Configure the component.
        SimpleFileIOOutputConfig outputConfig = createSimpleFileIOOutputConfig();
        outputConfig.getDataset().setPath(fileSpec);
        outputConfig.getDataset().setFormat(SimpleFileIOFormat.AVRO);

        // Create the runtime.
        SimpleFileIOOutput runtime = new SimpleFileIOOutput(outputConfig);

        // Use the runtime in a Spark pipeline to test.
        final Pipeline p = spark.createPipeline();
        PCollection<IndexedRecord> input = p.apply( //
                Create.of(ConvertToIndexedRecord.convertToAvro(new String[] { "1", "one" }), //
                        ConvertToIndexedRecord.convertToAvro(new String[] { "2", "two" }))); //
        input.apply(runtime);

        // And run the test.
        p.run().waitUntilFinish();

        // Check the expected values.
        MiniDfsResource.assertReadFile(fs, fileSpec, "1;one", "2;two");
    }

    @Test
    public void testCsv_merge() throws IOException {
        FileSystem fs = FileSystem.get(spark.createHadoopConfiguration());
        String fileSpec = fs.getUri().resolve(new Path(tmp.getRoot().toString(), "output.csv").toUri()).toString();

        // Configure the component.
        SimpleFileIOOutputConfig outputConfig = createSimpleFileIOOutputConfig();
        outputConfig.getDataset().setPath(fileSpec);
        outputConfig.getDataset().setFormat(SimpleFileIOFormat.CSV);
        outputConfig.setMergeOutput(true);

        // Create the runtime.
        SimpleFileIOOutput runtime = new SimpleFileIOOutput(outputConfig);

        // Use the runtime in a Spark pipeline to test.
        final Pipeline p = spark.createPipeline();
        PCollection<IndexedRecord> input = p.apply( //
                Create.of(ConvertToIndexedRecord.convertToAvro(new String[] { "1", "one" }), //
                        ConvertToIndexedRecord.convertToAvro(new String[] { "2", "two" }))); //
        input.apply(runtime);

        // And run the test.
        p.run().waitUntilFinish();

        // Check the expected values.
        MiniDfsResource.assertReadFile(fs, fileSpec, "1;one", "2;two");
        MiniDfsResource.assertFileNumber(fs, fileSpec, 1);
    }

    @Test
    public void testAvro_merge() throws IOException {
        FileSystem fs = FileSystem.get(spark.createHadoopConfiguration());
        String fileSpec = fs.getUri().resolve(new Path(tmp.getRoot().toString(), "output.avro").toUri()).toString();

        // Configure the component.
        SimpleFileIOOutputConfig outputConfig = createSimpleFileIOOutputConfig();
        outputConfig.getDataset().setPath(fileSpec);
        outputConfig.getDataset().setFormat(SimpleFileIOFormat.AVRO);
        outputConfig.setMergeOutput(true);

        // Create the runtime.
        SimpleFileIOOutput runtime = new SimpleFileIOOutput(outputConfig);

        // Use the runtime in a Spark pipeline to test.
        final Pipeline p = spark.createPipeline();
        PCollection<IndexedRecord> input = p.apply( //
                Create.of(ConvertToIndexedRecord.convertToAvro(new String[] { "1", "one" }), //
                        ConvertToIndexedRecord.convertToAvro(new String[] { "2", "two" }))); //
        input.apply(runtime);

        // And run the test.
        p.run().waitUntilFinish();

        // Check the expected values.

        MiniDfsResource.assertReadAvroFile(fs, fileSpec,
                new HashSet<IndexedRecord>(Arrays.asList(ConvertToIndexedRecord.convertToAvro(new String[] { "1", "one" }), //
                        ConvertToIndexedRecord.convertToAvro(new String[] { "2", "two" }))),
                false);
        MiniDfsResource.assertFileNumber(fs, fileSpec, 1);
    }

    @Test
    public void testParquet_merge() throws IOException {
        FileSystem fs = FileSystem.get(spark.createHadoopConfiguration());
        String fileSpec = fs.getUri().resolve(new Path(tmp.getRoot().toString(), "output.parquet").toUri()).toString();

        // Configure the component.
        SimpleFileIOOutputConfig outputConfig = createSimpleFileIOOutputConfig();
        outputConfig.getDataset().setPath(fileSpec);
        outputConfig.getDataset().setFormat(SimpleFileIOFormat.PARQUET);
        outputConfig.setMergeOutput(true);

        // Create the runtime.
        SimpleFileIOOutput runtime = new SimpleFileIOOutput(outputConfig);

        // Use the runtime in a Spark pipeline to test.
        final Pipeline p = spark.createPipeline();
        PCollection<IndexedRecord> input = p.apply( //
                Create.of(ConvertToIndexedRecord.convertToAvro(new String[] { "1", "one" }), //
                        ConvertToIndexedRecord.convertToAvro(new String[] { "2", "two" }))); //
        input.apply(runtime);

        // And run the test.
        p.run().waitUntilFinish();

        // Check the expected values.
        MiniDfsResource.assertReadParquetFile(fs, fileSpec,
                new HashSet<IndexedRecord>(Arrays.asList(ConvertToIndexedRecord.convertToAvro(new String[] { "1", "one" }), //
                        ConvertToIndexedRecord.convertToAvro(new String[] { "2", "two" }))),
                false);
        MiniDfsResource.assertFileNumber(fs, fileSpec, 1);
    }

}
