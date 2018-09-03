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
package org.talend.components.fileio.hdfs;

import static org.talend.components.test.RecordSetUtil.writeRandomCsvFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.values.PCollection;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.adapter.beam.transform.ConvertToIndexedRecord;
import org.talend.components.fileio.configuration.SimpleFileIOFormat;
import org.talend.components.test.BeamDirectTestResource;
import org.talend.components.test.MiniDfsResource;

/**
 * Unit tests for {@link SimpleFileIOInputRuntime}.
 */
public class SimpleFileIOInputRuntimeTest {

    @Rule
    public MiniDfsResource mini = new MiniDfsResource();

    @Rule
    public BeamDirectTestResource beam = BeamDirectTestResource.of();

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFileIOInput.class);

    private static SimpleFileIODataSet createSimpleFileIODataSet() {
        SimpleFileIODataSet dataset = new SimpleFileIODataSet();
        SimpleFileIODataStore datastore = new SimpleFileIODataStore();
        dataset.setDatastore(datastore);
        return dataset;
    }

    /**
     * Basic unit test using all default values (except for the path) on an in-memory DFS cluster.
     */
    @Test
    public void testBasicDefaults() throws IOException, URISyntaxException {
        String inputFile = writeRandomCsvFile(mini.getFs(), "/user/test/input.csv", 0, 0, 10, 10, 6, ";", "\n");
        String fileSpec = mini.getFs().getUri().resolve("/user/test/input.csv").toString();

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setFormat(SimpleFileIOFormat.CSV);
        dataset.setPath(fileSpec);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        // Use the runtime in a direct pipeline to test.
        final Pipeline p = beam.createPipeline();
        PCollection<IndexedRecord> readLines = p.apply(runtime);

        // Check the expected values.
        List<IndexedRecord> expected = new ArrayList<>();
        for (String record : inputFile.split("\n")) {
            expected.add(ConvertToIndexedRecord.convertToAvro(record.split(";")));
        }
        PAssert.that(readLines).containsInAnyOrder(expected);

        // And run the test.
        p.run().waitUntilFinish();
    }

}
