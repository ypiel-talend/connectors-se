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

package org.talend.components.azure.source;

import java.util.List;

import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.talend.components.azure.common.FileFormat;
import org.talend.components.azure.common.connection.AzureStorageConnectionAccount;
import org.talend.components.azure.common.csv.CSVFormatOptions;
import org.talend.components.azure.common.csv.RecordDelimiter;
import org.talend.components.azure.common.service.AzureComponentServices;
import org.talend.components.azure.dataset.AzureBlobDataset;
import org.talend.components.azure.datastore.AzureCloudConnection;
import org.talend.components.azure.service.AzureBlobComponentServices;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.maven.MavenDecrypter;
import org.talend.sdk.component.maven.Server;
import org.talend.sdk.component.runtime.manager.chain.Job;

import com.microsoft.azure.storage.CloudStorageAccount;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

@WithComponents("org.talend.components.azure")
public class CSVInputIT {
    @Service
    private AzureBlobComponentServices componentService;


    @ClassRule
    public static final SimpleComponentRule COMPONENT = new SimpleComponentRule("org.talend.components.azure");

    private static BlobInputProperties blobInputProperties;

    private CloudStorageAccount storageAccount;

    @BeforeEach
    public void init() throws Exception {
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
        dataset.setContainerName("limcontainer");
        dataset.setDirectory("csv0");
        blobInputProperties = new BlobInputProperties();
        blobInputProperties.setDataset(dataset);

        storageAccount = componentService.createStorageAccount(blobInputProperties.getDataset().getConnection());
    }

    @Test
    public void selectAllInputPipelineTest() {
        String inputConfig = configurationByExample().forInstance(blobInputProperties).configured().toQueryString();
        Job.components().component("azureInput", "Azure://Input?" + inputConfig)
                .component("collector", "test://collector").connections().from("azureInput").to("collector").build().run();

        List<Record> records = COMPONENT.getCollectedData(Record.class);

        System.out.println(records.size());
    }

}
