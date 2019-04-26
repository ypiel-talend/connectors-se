/*
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
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

package org.talend.components.azure.output;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.talend.components.azure.BlobTestUtils;
import org.talend.components.azure.common.FileFormat;
import org.talend.components.azure.common.connection.AzureStorageConnectionAccount;
import org.talend.components.azure.common.csv.CSVFormatOptions;
import org.talend.components.azure.common.csv.RecordDelimiter;
import org.talend.components.azure.dataset.AzureBlobDataset;
import org.talend.components.azure.datastore.AzureCloudConnection;
import org.talend.components.azure.service.AzureBlobComponentServices;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.maven.MavenDecrypter;
import org.talend.sdk.component.maven.Server;
import org.talend.sdk.component.runtime.manager.chain.Job;

import com.microsoft.azure.storage.CloudStorageAccount;
import static org.talend.components.azure.source.CSVInputIT.COMPONENT;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

@WithComponents("org.talend.components.azure")
class CSVOutputIT {

    private String containerName;
    private BlobOutputConfiguration blobOutputProperties;
    private CloudStorageAccount storageAccount;
    @Service
    private AzureBlobComponentServices componentService;

    @BeforeEach
    public void init() throws Exception {
        containerName = "test-it-" + RandomStringUtils.randomAlphabetic(10).toLowerCase();
        Server account;
        final MavenDecrypter decrypter = new MavenDecrypter();

        AzureCloudConnection dataStore = new AzureCloudConnection();
        dataStore.setUseAzureSharedSignature(false);
        AzureStorageConnectionAccount accountConnection = new AzureStorageConnectionAccount();
        account = decrypter.find("azure.account");
        accountConnection.setAccountName(account.getUsername());
        accountConnection.setAccountKey(account.getPassword());

        dataStore.setAccountConnection(accountConnection);

        AzureBlobDataset dataset = new AzureBlobDataset();
        dataset.setConnection(dataStore);
        dataset.setFileFormat(FileFormat.CSV);

        CSVFormatOptions formatOptions = new CSVFormatOptions();
        formatOptions.setRecordDelimiter(RecordDelimiter.LF);
        dataset.setCsvOptions(formatOptions);
        dataset.setContainerName(containerName);
        blobOutputProperties = new BlobOutputConfiguration();
        blobOutputProperties.setDataset(dataset);

        storageAccount = componentService.createStorageAccount(blobOutputProperties.getDataset().getConnection());
        BlobTestUtils.createStorage(blobOutputProperties.getDataset().getContainerName(), storageAccount);
    }

    @Test
    public void selectAllInputPipelineTest() {
        blobOutputProperties.getDataset().setDirectory("testDir");
        blobOutputProperties.setBlobNameTemplate("testFile");

        blobOutputProperties.getDataset().getCsvOptions().setUseHeader(true);
        blobOutputProperties.getDataset().getCsvOptions().setHeader(1);

        Record testRecord = COMPONENT.findService(RecordBuilderFactory.class).newRecordBuilder()
                .withBoolean("booleanValue", true)
                .withLong("longValue", 0L).withInt("intValue", 1).withDouble("doubleValue", 2.0)
                .withDateTime("dateValue", Date.from(Instant.now())).build();

        List<Record> testRecords = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            testRecords.add(testRecord);
        }
        COMPONENT.setInputData(testRecords);

        String outputConfig = configurationByExample().forInstance(blobOutputProperties).configured().toQueryString();
        Job.components().component("inputFlow", "test://emitter")
                .component("outputComponent", "Azure://Output?" + outputConfig).connections().from("inputFlow")
                .to("outputComponent").build().run();
    }
}