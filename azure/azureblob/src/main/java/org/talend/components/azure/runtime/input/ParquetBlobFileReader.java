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

import com.microsoft.azure.storage.blob.ListBlobItem;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.io.InputFile;
import org.talend.components.azure.dataset.AzureBlobDataset;
import org.talend.components.azure.datastore.AzureCloudConnection;
import org.talend.components.azure.service.AzureBlobComponentServices;
import org.talend.components.azure.service.MessageService;
import org.talend.components.azure.service.RegionUtils;
import org.talend.components.common.connection.azureblob.AzureAuthType;
import org.talend.components.common.connection.azureblob.Protocol;
import org.talend.components.common.stream.input.parquet.ParquetRecordReader;
import org.talend.sdk.component.api.exception.ComponentException;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParquetBlobFileReader extends BlobFileReader {

    public ParquetBlobFileReader(AzureBlobDataset config, RecordBuilderFactory recordBuilderFactory,
            AzureBlobComponentServices connectionServices, MessageService messageService) {
        super(config, recordBuilderFactory, connectionServices, messageService);
    }

    @Override
    protected Iterator<Record> initItemRecordIterator(Iterable<ListBlobItem> blobItems) {
        final ParquetRecordIterator parquetIterator = new ParquetRecordIterator(blobItems, getRecordBuilderFactory(), this.getConfig(), this.getMessageService());
        parquetIterator.initialize();
        return parquetIterator;
    }

    protected static class ParquetRecordIterator extends ItemRecordIterator<Record> {

        private final static String AZURE_FILESYSTEM_PROPERTY_KEY = "fs.azure";

        private final static String AZURE_FILESYSTEM_PROPERTY_VALUE =
                "org.apache.hadoop.fs.azure.NativeAzureFileSystem";

        private Configuration hadoopConfig;

        private String accountName;

        private String endpointSuffix;

        private Iterator<Record> currentRecords;

        private final AzureBlobDataset dataset;

        private final MessageService messageService;

        protected ParquetRecordIterator(final Iterable<ListBlobItem> blobItemsList,
                                      final RecordBuilderFactory recordBuilderFactory,
                                      final AzureBlobDataset dataset,
                                      final MessageService messageService) {
            super(blobItemsList, recordBuilderFactory);
            this.messageService = messageService;
            this.dataset = dataset;
        }

        public void initialize() {
            initConfig(dataset);
            takeFirstItem();
        }

        private void initConfig(final AzureBlobDataset dataset) {
            hadoopConfig = new Configuration();
            hadoopConfig.set(AZURE_FILESYSTEM_PROPERTY_KEY, AZURE_FILESYSTEM_PROPERTY_VALUE);
            final AzureCloudConnection connection = dataset.getConnection();
            if (connection.isUseAzureSharedSignature()) {
                RegionUtils ru = new RegionUtils(connection.getSignatureConnection());
                accountName = ru.getAccountName4SignatureAuth();
                endpointSuffix = ru.getEndpointSuffix4SignatureAuth();
                String sasKey = RegionUtils
                        .getSasKey4SignatureAuth(dataset.getContainerName(), accountName, endpointSuffix);
                String token = ru.getToken4SignatureAuth();
                hadoopConfig.set(sasKey, token);
            } else if (connection.getAccountConnection().getAuthType() == AzureAuthType.BASIC) {
                accountName = connection.getAccountConnection().getAccountName();
                endpointSuffix = connection.getEndpointSuffix();
                String accountCredKey = RegionUtils.getAccountCredKey4AccountAuth(accountName, endpointSuffix);
                hadoopConfig.set(accountCredKey, connection.getAccountConnection().getAccountKey());
            } else {
                /*
                 * Azure Active Directory auth doesn't supported by hadoop-azure lib for now.
                 * We do not support it in the component either
                 * https://issues.apache.org/jira/browse/HADOOP-17973
                 */
                throw new ComponentException(this.messageService.authTypeNotSupportedForParquet());
            }
        }

        @Override
        protected Record convertToRecord(Record next) {
            return next;
        }

        @Override
        protected void readItem() {
            final String blobURI = this.extractPath();
            try {
                final InputFile file = HadoopInputFile.fromPath(new org.apache.hadoop.fs.Path(blobURI), hadoopConfig);

                this.currentRecords = new ParquetRecordReader(this.getRecordBuilderFactory()).read(file);
            } catch (IOException e) {
                log.error("Can't read item", e);
            }
        }

        protected String extractPath() {
            boolean isHttpsConnectionUsed = this.dataset.getConnection().isUseAzureSharedSignature()
                    || this.dataset.getConnection().getAccountConnection().getProtocol().equals(Protocol.HTTPS);
            return RegionUtils
                    .getBlobURI(isHttpsConnectionUsed, this.dataset.getContainerName(), accountName,
                            endpointSuffix, getCurrentItem().getName());
        }

        @Override
        protected boolean hasNextRecordTaken() {
            return this.currentRecords != null && this.currentRecords.hasNext();
        }

        @Override
        protected Record takeNextRecord() {
            return this.currentRecords.next();
        }

        @Override
        protected void complete() {
        }
    }

}
