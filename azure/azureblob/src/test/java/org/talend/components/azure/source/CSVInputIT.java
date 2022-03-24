/*
 * Copyright (C) 2006-2022 Talend Inc. - www.talend.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.talend.components.azure.source;

import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.talend.components.azure.BaseIT;
import org.talend.components.azure.BlobTestUtils;
import org.talend.components.azure.common.FileFormat;
import org.talend.components.azure.dataset.AzureBlobDataset;
import org.talend.components.common.formats.csv.CSVFieldDelimiter;
import org.talend.components.common.formats.csv.CSVFormatOptions;
import org.talend.components.common.formats.csv.CSVRecordDelimiter;
import org.talend.sdk.component.api.exception.ComponentException;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.manager.chain.Job;

@WithComponents("org.talend.components.azure")
public class CSVInputIT extends BaseIT {

    private static BlobInputProperties blobInputProperties;

    @BeforeEach
    void initDataset() {
        AzureBlobDataset dataset = new AzureBlobDataset();
        dataset.setConnection(dataStore);
        dataset.setFileFormat(FileFormat.CSV);

        CSVFormatOptions formatOptions = new CSVFormatOptions();
        formatOptions.setRecordDelimiter(CSVRecordDelimiter.LF);
        dataset.setCsvOptions(formatOptions);
        dataset.setContainerName(containerName);
        blobInputProperties = new BlobInputProperties();
        blobInputProperties.setDataset(dataset);
    }

    @Test
    void selectAllInputPipelineTest() throws Exception {
        final int recordSize = 10;
        List<String> columns = Arrays.asList(new String[] { "a", "b", "c" });
        blobInputProperties.getDataset().setDirectory("someDir");
        BlobTestUtils
                .createAndPopulateFileInStorage(storageAccount, blobInputProperties.getDataset(), columns, recordSize);

        String inputConfig = configurationByExample().forInstance(blobInputProperties).configured().toQueryString();
        Job
                .components()
                .component("azureInput", "Azure://Input?" + inputConfig)
                .component("collector", "test://collector")
                .connections()
                .from("azureInput")
                .to("collector")
                .build()
                .run();
        List<Record> records = componentsHandler.getCollectedData(Record.class);
        Record firstRecord = records.get(0);

        Assertions.assertEquals(recordSize, records.size(), "Records amount is different");
        Assertions
                .assertEquals(columns.size(), firstRecord.getSchema().getEntries().size(),
                        "Columns number is different");
    }

    @Test
    void selectFromNotExistingDirectory() {
        blobInputProperties.getDataset().setDirectory("notExistingDir");
        String inputConfig = configurationByExample().forInstance(blobInputProperties).configured().toQueryString();
        Job.ExecutorBuilder job = Job
                .components()
                .component("azureInput", "Azure://Input?" + inputConfig)
                .component("collector", "test://collector")
                .connections()
                .from("azureInput")
                .to("collector")
                .build();
        Assertions
                .assertThrows(ComponentException.class, job::run,
                        "Can't start reading blob items: Specified directory doesn't exist");
    }

    @Test
    void selectFromNotExistingContainer() {
        blobInputProperties.getDataset().setContainerName("notexistingcontainer");
        blobInputProperties.getDataset().setDirectory("notExistingDir");
        String inputConfig = configurationByExample().forInstance(blobInputProperties).configured().toQueryString();
        Job.ExecutorBuilder job = Job
                .components()
                .component("azureInput", "Azure://Input?" + inputConfig)
                .component("collector", "test://collector")
                .connections()
                .from("azureInput")
                .to("collector")
                .build();
        Assertions
                .assertThrows(ComponentException.class, job::run,
                        "Can't start reading blob items: Specified container doesn't exist");
    }

    @Test
    void invalidContainerNameInDataSet() {
        blobInputProperties.getDataset().setContainerName("inVaLiDcoNtAinErName");
        blobInputProperties.getDataset().setDirectory("notExistingDir");
        String inputConfig = configurationByExample().forInstance(blobInputProperties).configured().toQueryString();
        Job.ExecutorBuilder job = Job
                .components()
                .component("azureInput", "Azure://Input?" + inputConfig)
                .component("collector", "test://collector")
                .connections()
                .from("azureInput")
                .to("collector")
                .build();
        Assertions
                .assertThrows(ComponentException.class, job::run,
                        "Can't start reading blob items: Container name is not valid");
    }

    @Test
    void testInputMultipleFiles() throws Exception {
        final int recordSize = 10 + 5;

        List<String> columns = Arrays.asList(new String[] { "a", "b", "c" });
        blobInputProperties.getDataset().setDirectory("someDir");
        BlobTestUtils.createAndPopulateFileInStorage(storageAccount, blobInputProperties.getDataset(), columns, 10);
        BlobTestUtils.createAndPopulateFileInStorage(storageAccount, blobInputProperties.getDataset(), columns, 5);

        String inputConfig = configurationByExample().forInstance(blobInputProperties).configured().toQueryString();
        Job
                .components()
                .component("azureInput", "Azure://Input?" + inputConfig)
                .component("collector", "test://collector")
                .connections()
                .from("azureInput")
                .to("collector")
                .build()
                .run();
        List<Record> records = componentsHandler.getCollectedData(Record.class);

        Assertions.assertEquals(recordSize, records.size(), "Records amount is different");
    }

    @Test
    void testHeaderIsGreaterThanFileContent() throws Exception {
        final int recordSize = 0;
        List<String> columns = Arrays.asList(new String[] { "a", "b", "c" });
        blobInputProperties.getDataset().setDirectory("someDir");
        BlobTestUtils.createAndPopulateFileInStorage(storageAccount, blobInputProperties.getDataset(), columns, 1);
        blobInputProperties.getDataset().getCsvOptions().setUseHeader(true);
        blobInputProperties.getDataset().getCsvOptions().setHeader(5);

        String inputConfig = configurationByExample().forInstance(blobInputProperties).configured().toQueryString();
        Job
                .components()
                .component("azureInput", "Azure://Input?" + inputConfig)
                .component("collector", "test://collector")
                .connections()
                .from("azureInput")
                .to("collector")
                .build()
                .run();
        List<Record> records = componentsHandler.getCollectedData(Record.class);

        Assertions.assertEquals(recordSize, records.size(), "Records amount is different");
    }

    @Test
    void testCorrectHeader() throws Exception {
        final int recordSize = 5 - 1;
        List<String> columns = Arrays.asList(new String[] { "a", "b", "c" });
        blobInputProperties.getDataset().setDirectory("someDir");
        BlobTestUtils.createAndPopulateFileInStorage(storageAccount, blobInputProperties.getDataset(), columns, 5);
        blobInputProperties.getDataset().getCsvOptions().setUseHeader(true);
        blobInputProperties.getDataset().getCsvOptions().setHeader(1);

        String inputConfig = configurationByExample().forInstance(blobInputProperties).configured().toQueryString();
        Job
                .components()
                .component("azureInput", "Azure://Input?" + inputConfig)
                .component("collector", "test://collector")
                .connections()
                .from("azureInput")
                .to("collector")
                .build()
                .run();
        List<Record> records = componentsHandler.getCollectedData(Record.class);

        Assertions.assertEquals(recordSize, records.size(), "Records amount is different");
    }

    @Test
    void testReadFileFromRootDir() throws Exception {
        final int recordSize = 5;
        List<String> columns = Arrays.asList(new String[] { "a", "b", "c" });
        blobInputProperties.getDataset().setDirectory(null);

        BlobTestUtils.createAndPopulateFileInStorage(storageAccount, blobInputProperties.getDataset(), columns, 5);

        String inputConfig = configurationByExample().forInstance(blobInputProperties).configured().toQueryString();
        Job
                .components()
                .component("azureInput", "Azure://Input?" + inputConfig)
                .component("collector", "test://collector")
                .connections()
                .from("azureInput")
                .to("collector")
                .build()
                .run();
        List<Record> records = componentsHandler.getCollectedData(Record.class);

        Assertions.assertEquals(recordSize, records.size(), "Records amount is different");
    }

    @Test
    void testReadCSVWithShortRecord() throws Exception {
        final int recordSize = 3;
        final int columnSize = 3;
        final int shortColumnSize = 2;
        blobInputProperties.getDataset().getCsvOptions().setFieldDelimiter(CSVFieldDelimiter.SEMICOLON);
        blobInputProperties.getDataset().setDirectory("csv");

        BlobTestUtils
                .uploadTestFile(storageAccount, blobInputProperties,
                        "csv/csvWithShortRecord.csv", "csvWithShortRecord.csv");

        String inputConfig = configurationByExample().forInstance(blobInputProperties).configured().toQueryString();
        Job
                .components()
                .component("azureInput", "Azure://Input?" + inputConfig)
                .component("collector", "test://collector")
                .connections()
                .from("azureInput")
                .to("collector")
                .build()
                .run();
        List<Record> records = componentsHandler.getCollectedData(Record.class);

        Assertions.assertEquals(recordSize, records.size(), "Records amount is different");
        Assertions
                .assertEquals(columnSize, records.get(0).getSchema().getEntries().size(),
                        "Columns number is different");
        Assertions
                .assertNull(records.get(2).getString("field2"),
                        "Value for last column in the short row should be null");

    }
}
