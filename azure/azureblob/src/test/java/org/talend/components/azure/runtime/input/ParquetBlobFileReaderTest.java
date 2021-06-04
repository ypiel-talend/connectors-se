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
package org.talend.components.azure.runtime.input;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Iterator;

import org.apache.hadoop.fs.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.talend.components.azure.BlobTestUtils;
import org.talend.components.azure.common.FileFormat;
import org.talend.components.azure.dataset.AzureBlobDataset;
import org.talend.components.azure.datastore.AzureCloudConnection;
import org.talend.components.azure.service.AzureBlobComponentServices;
import org.talend.components.azure.service.MessageService;
import org.talend.components.azure.source.BlobInputProperties;
import org.talend.components.azure.source.BlobSource;
import org.talend.components.common.connection.azureblob.AzureAuthType;
import org.talend.components.common.connection.azureblob.AzureStorageConnectionAccount;
import org.talend.components.common.service.azureblob.AzureComponentServices;
import org.talend.sdk.component.api.exception.ComponentException;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.injector.Injector;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.container.Container;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.manager.ComponentManager;
import org.talend.sdk.component.runtime.record.RecordBuilderFactoryImpl;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.StorageUri;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;

@WithComponents("org.talend.components.azure")
class ParquetBlobFileReaderTest {

    @Injected
    private BaseComponentsHandler componentsHandler;

    @Service
    private AzureBlobComponentServices services;

    @Service
    private MessageService i18n;

    @Test
    void testADAuthNotSupportedForParquet() throws Exception {
        MessageService messageService = Mockito.mock(MessageService.class);
        Mockito.when(messageService.authTypeNotSupportedForParquet()).thenReturn("Some error message");
        Mockito.when(messageService.cantStartReadBlobItems(anyString()))
                .thenReturn("Parent error message for " + "Some error message");

        AzureCloudConnection connection = new AzureCloudConnection();
        AzureStorageConnectionAccount accountConnection = new AzureStorageConnectionAccount();
        accountConnection.setAuthType(AzureAuthType.ACTIVE_DIRECTORY_CLIENT_CREDENTIAL);
        connection.setAccountConnection(accountConnection);

        AzureBlobDataset dataset = new AzureBlobDataset();
        dataset.setConnection(connection);
        dataset.setFileFormat(FileFormat.PARQUET);

        BlobInputProperties inputProperties = new BlobInputProperties();
        inputProperties.setDataset(dataset);

        AzureBlobComponentServices componentServices = Mockito.mock(AzureBlobComponentServices.class);
        AzureComponentServices azureConnectionServices = Mockito.mock(AzureComponentServices.class);
        CloudBlobClient blobClientMock = Mockito.mock(CloudBlobClient.class);
        CloudBlobContainer containerReferenceMock = Mockito.mock(CloudBlobContainer.class);
        Mockito.when(containerReferenceMock.exists()).thenReturn(true);
        Mockito.when(containerReferenceMock.listBlobs(any(), anyBoolean(), any(), any(), any()))
                .thenReturn(
                        Collections.singletonList(Mockito.mock(ListBlobItem.class)));

        Mockito.when(blobClientMock.getContainerReference(any())).thenReturn(containerReferenceMock);

        Mockito.when(azureConnectionServices.createCloudBlobClient(any(), any())).thenReturn(blobClientMock);

        Mockito.when(componentServices.getConnectionService()).thenReturn(azureConnectionServices);
        BlobSource reader = new BlobSource(inputProperties, componentServices, null, messageService);

        ComponentException exception = Assertions.assertThrows(ComponentException.class, reader::init);
        Assertions.assertTrue(exception.getMessage().contains(messageService.authTypeNotSupportedForParquet()),
                "Not expected exception message appeared");
    }

    @Test
    void readRecordTest() throws URISyntaxException, StorageException {
        final AzureComponentServices service = new AzureComponentServices();
        BlobTestUtils.inject(this.componentsHandler.asManager(), AzureComponentServices.class, service);
        BlobTestUtils.inject(this.componentsHandler.asManager(), AzureBlobComponentServices.class, this.services);
        BlobTestUtils.inject(this.componentsHandler.asManager(), MessageService.class, this.i18n);
        final AzureBlobDataset ds = new AzureBlobDataset();
        ds.setConnection(new AzureCloudConnection());
        ds.setContainerName("container");

        final AzureStorageConnectionAccount account = new AzureStorageConnectionAccount();
        ds.getConnection().setAccountConnection(account);
        ds.getConnection().setUseAzureSharedSignature(false);
        account.setAccountName("jamesbond");
        account.setAccountKey(new String(Base64.getEncoder().encode("007".getBytes(StandardCharsets.UTF_8))));

        final RecordBuilderFactory factory = new RecordBuilderFactoryImpl("test");

        final ParquetBlobFileReader reader = new ParquetBlobFileReader(ds, factory, this.services, i18n) {
            @Override
            protected Iterator<Record> initItemRecordIterator(Iterable<ListBlobItem> blobItems) {
                final ParquetRecordIteratorMock mock = new ParquetRecordIteratorMock(blobItems,
                        this.getRecordBuilderFactory(),
                        this.getConfig(),
                        this.getMessageService(),
                        "testParquet6Records.parquet");
                mock.initialize();
                return mock;
            }

            @Override
            public CloudBlobContainer checkBlobContainer(AzureBlobDataset config, CloudBlobClient blobClient) throws URISyntaxException, StorageException {
                return blobClient.getContainerReference(config.getContainerName());
            }

            @Override
            protected Iterable<ListBlobItem> findBlobs(final CloudBlobContainer container) {

                try {
                    CloudBlockBlob item = new CloudBlockBlob(URI.create("https://container/xxx.zz"));
                    return Collections.singletonList(item);
                }
                catch (final StorageException ex) {
                    throw new RuntimeException("" + ex.getMessage(), ex);
                }
            }
        };
        reader.initialize();
        final Record record1 = reader.readRecord();
        Assertions.assertNotNull(record1);
        final Record record2 = reader.readRecord();
        Assertions.assertNotNull(record2);
        final Record record3 = reader.readRecord();
        Assertions.assertNotNull(record3);
        final Record record4 = reader.readRecord();
        Assertions.assertNotNull(record4);
        final Record record5 = reader.readRecord();
        Assertions.assertNotNull(record5);
        final Record record6 = reader.readRecord();
        Assertions.assertNotNull(record6);
        final Record record7 = reader.readRecord();
        Assertions.assertNull(record7);
    }

    static class ParquetRecordIteratorMock extends ParquetBlobFileReader.ParquetRecordIterator {

        private final String testFile;

        public ParquetRecordIteratorMock(final Iterable<ListBlobItem> blobItemsList,
                                         final RecordBuilderFactory recordBuilderFactory,
                                         final AzureBlobDataset dataset,
                                         final MessageService messageService,
                                         final String testFile) {
            super(blobItemsList, recordBuilderFactory, dataset, messageService);
            this.testFile = testFile;
        }

        @Override
        protected String extractPath() {
            final URL resource = Thread.currentThread().getContextClassLoader().getResource("./parquet/" + this.testFile);
            return resource.getPath();
        }
    }
}