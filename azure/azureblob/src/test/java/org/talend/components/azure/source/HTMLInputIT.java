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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.talend.components.azure.BaseIT;
import org.talend.components.azure.BlobTestUtils;
import org.talend.components.azure.service.MessageService;
import org.talend.components.common.formats.Encoding;
import org.talend.components.azure.common.FileFormat;
import org.talend.components.common.formats.excel.ExcelFormat;
import org.talend.components.common.formats.excel.ExcelFormatOptions;
import org.talend.components.azure.dataset.AzureBlobDataset;
import org.talend.sdk.component.api.exception.ComponentException;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.manager.chain.Job;

import com.microsoft.azure.storage.StorageException;
import lombok.SneakyThrows;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

@WithComponents("org.talend.components.azure")
class HTMLInputIT extends BaseIT {

    private static BlobInputProperties blobInputProperties;

    @Service
    private MessageService messageService;

    @BeforeEach
    void initDataset() {
        AzureBlobDataset dataset = new AzureBlobDataset();
        dataset.setConnection(dataStore);
        dataset.setFileFormat(FileFormat.EXCEL);
        ExcelFormatOptions excelFormatOptions = new ExcelFormatOptions();
        excelFormatOptions.setExcelFormat(ExcelFormat.HTML);
        excelFormatOptions.setEncoding(Encoding.UTF8);
        dataset.setExcelOptions(excelFormatOptions);

        dataset.setContainerName(containerName);
        dataset.setDirectory("excelHTML");
        blobInputProperties = new BlobInputProperties();
        blobInputProperties.setDataset(dataset);
    }

    @Test
    void testInput1File1Row() throws StorageException, IOException, URISyntaxException {
        final int recordSize = 1;
        final int columnSize = 2;

        BlobTestUtils
                .uploadTestFile(storageAccount, blobInputProperties, "excelHTML/TestExcelHTML1Row.html",
                        "TestExcelHTML1Row.html");

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
        Record firstRecord = records.get(0);
        Assertions
                .assertEquals(columnSize, firstRecord.getSchema().getEntries().size(), "Record's schema is different");
        Assertions.assertEquals("a1", firstRecord.getString("field0"));
        Assertions.assertEquals("b1", firstRecord.getString("field1"));
    }

    @Test
    void testInput1FileMultipleRows() throws StorageException, IOException, URISyntaxException {
        final int recordSize = 5;
        final int columnSize = 2;

        BlobTestUtils
                .uploadTestFile(storageAccount, blobInputProperties, "excelHTML/TestExcelHTML5Rows.html",
                        "TestExcelHTML5Rows.html");

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
        for (int i = 0; i < recordSize; i++) {
            Record record = records.get(i);
            Assertions.assertEquals(columnSize, record.getSchema().getEntries().size(), "Record's schema is different");
            Assertions.assertEquals("a" + (i + 1), record.getString("field0"));
            Assertions.assertEquals("b" + (i + 1), record.getString("field1"));
        }
    }

    @Test
    void testInputMultipleFiles() throws StorageException, IOException, URISyntaxException {
        final int recordSize = 1 + 5;
        BlobTestUtils
                .uploadTestFile(storageAccount, blobInputProperties, "excelHTML/TestExcelHTML1Row.html",
                        "TestExcelHTML1Row.html");
        BlobTestUtils
                .uploadTestFile(storageAccount, blobInputProperties, "excelHTML/TestExcelHTML5Rows.html",
                        "TestExcelHTML5Rows.html");

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
    void testInputHTMLExportedFromSalesforce() throws StorageException, IOException, URISyntaxException {
        final int recordSize = 6;
        final int columnSize = 7;
        BlobTestUtils
                .uploadTestFile(storageAccount, blobInputProperties, "excelHTML/realSalesforceExportedHTML.html",
                        "realSalesforceExportedHTML.html");

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
        Record firstRecord = records.get(0);
        Assertions
                .assertEquals(columnSize, firstRecord.getSchema().getEntries().size(),
                        "Record's column amount is different");
        Assertions.assertEquals("compqa talend", firstRecord.getString("Account_Owner"));
        Assertions.assertEquals("CLoud_salesforce", firstRecord.getString("Account_Name"));
        Assertions.assertTrue(firstRecord.getString("Type").isEmpty(), "Type should be empty");
        Assertions.assertTrue(firstRecord.getString("Rating").isEmpty(), "Rating should be empty");
        Assertions.assertTrue(firstRecord.getString("Last_Activity").isEmpty(), "Last_Activity should be empty");
        Assertions.assertEquals("2/3/2020", firstRecord.getString("Last_Modified_Date"));
        Assertions
                .assertTrue(firstRecord.getString("Billing_State_Province").isEmpty(),
                        "Billing_State_Province should be empty");
    }

    @SneakyThrows
    @Test
    void testCustomEncoding() {
        int expectedNumberOfRecords = 26;
        String specialSymbolStrings = "テスト";

        blobInputProperties.getDataset().getExcelOptions().setEncoding(Encoding.OTHER);
        blobInputProperties.getDataset().getExcelOptions().setCustomEncoding("SJIS");
        BlobTestUtils
                .uploadTestFile(storageAccount, blobInputProperties, "excelHTML/report_encodingshif.html",
                        "report_encodingshif.html");

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

        Assertions.assertEquals(expectedNumberOfRecords, records.size());
        Assertions.assertEquals(specialSymbolStrings, records.get(0).getString("Subject"));
    }

    @SneakyThrows
    @Test
    void testReadCsvAsHTMLException() {
        BlobTestUtils
                .uploadTestFile(storageAccount, blobInputProperties, "avro/testAvro1Record.avro",
                        "testAvro1Record.html");

        String inputConfig = configurationByExample().forInstance(blobInputProperties).configured().toQueryString();
        ComponentException expectedException = Assertions.assertThrows(ComponentException.class, () -> Job.components()
                .component("azureInput", "Azure://Input?" + inputConfig)
                .component("collector", "test://collector")
                .connections()
                .from("azureInput")
                .to("collector")
                .build()
                .run());

        Assertions.assertTrue(expectedException.getMessage().contains(messageService.fileIsNotValidExcelHTML()));
    }
}
