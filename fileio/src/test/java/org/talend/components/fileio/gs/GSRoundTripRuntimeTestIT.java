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

package org.talend.components.fileio.gs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.FileSystems;
import org.apache.beam.sdk.io.fs.MoveOptions;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.values.PCollection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.components.adapter.beam.gcp.GcpServiceAccountOptions;
import org.talend.components.adapter.beam.gcp.ServiceAccountCredentialFactory;
import org.talend.components.adapter.beam.transform.ConvertToIndexedRecord;
import org.talend.components.fileio.hdfs.SimpleFileIODataSet;
import org.talend.components.fileio.hdfs.SimpleFileIODataStore;
import org.talend.components.fileio.hdfs.SimpleFileIOInput;
import org.talend.components.fileio.hdfs.SimpleFileIOOutput;
import org.talend.components.fileio.hdfs.SimpleFileIOOutputConfig;
import org.talend.components.fileio.runtime.SimpleFileIOAvroRegistry;

public class GSRoundTripRuntimeTestIT {

    static {
        SimpleFileIOAvroRegistry.get();
    }
    
    private static SimpleFileIOOutputConfig createSimpleFileIOOutputConfig() {
        SimpleFileIOOutputConfig config = new SimpleFileIOOutputConfig();
        SimpleFileIODataSet dataset = new SimpleFileIODataSet();
        SimpleFileIODataStore datastore = new SimpleFileIODataStore();
        dataset.setDatastore(datastore);
        config.setDataset(dataset);
        return config;
    }
    
    private static SimpleFileIODataSet createSimpleFileIODataSet() {
        SimpleFileIODataSet dataset = new SimpleFileIODataSet();
        SimpleFileIODataStore datastore = new SimpleFileIODataStore();
        dataset.setDatastore(datastore);
        return dataset;
    }

    PipelineOptions pipelineOptions = PipelineOptionsFactory.create();

    // Need to create Pipeline by PipelineOptions, then it will registry GcsFileSystem, else TextIO will failed
    // It will be fixed on beam-compiler side
    final Pipeline writeP = Pipeline.create(pipelineOptions);

    final Pipeline readP = Pipeline.create(pipelineOptions);

    String gsPath = System.getProperty("bigquery.gcp.temp.folder") + "/" + UUID.randomUUID() + "/";

    @Before
    public void prepare() {

        // DataflowPipelineOptions dataflowPO = pipelineOptions.as(DataflowPipelineOptions.class);
        // dataflowPO.setRunner(DataflowRunner.class);

        GcpServiceAccountOptions gcpOptions = pipelineOptions.as(GcpServiceAccountOptions.class);
        gcpOptions.setProject(System.getProperty("bigquery.project"));
        gcpOptions.setCredentialFactoryClass(ServiceAccountCredentialFactory.class);
        gcpOptions.setServiceAccountFile(System.getProperty("bigquery.service.account.file"));
    }

    @After
    public void clean() throws IOException {
        //TODO the directory is not really be removed, need a way to fix it
        FileSystems.delete(Arrays.asList(FileSystems.matchNewResource(gsPath, true)),
                MoveOptions.StandardMoveOptions.IGNORE_MISSING_FILES);
    }

    @Test
    public void testCsv() {
        List<IndexedRecord> expected = new ArrayList<>();
        expected.add(ConvertToIndexedRecord.convertToAvro(new String[] { "1", "one" }));
        expected.add(ConvertToIndexedRecord.convertToAvro(new String[] { "2", "two" }));

        SimpleFileIOOutputConfig outputConfig = createSimpleFileIOOutputConfig();
        outputConfig.getDataset().setPath(gsPath);

        SimpleFileIOOutput outputRuntime = new SimpleFileIOOutput(outputConfig);
        
        PCollection<IndexedRecord> input = writeP.apply(Create.of(expected));
        input.apply(outputRuntime);
        writeP.run(pipelineOptions).waitUntilFinish();

        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setPath(gsPath + "*");

        SimpleFileIOInput inputRuntime = new SimpleFileIOInput(dataset);
        
        PCollection<IndexedRecord> readRecords = readP.apply(inputRuntime);
        PAssert.that(readRecords).containsInAnyOrder(expected);
        readP.run(pipelineOptions).waitUntilFinish();

    }

}
