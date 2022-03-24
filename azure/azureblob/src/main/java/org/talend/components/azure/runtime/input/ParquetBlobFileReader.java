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

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.io.InputFile;
import org.talend.components.common.connection.azureblob.AzureAuthType;
import org.talend.components.common.connection.azureblob.Protocol;
import org.talend.components.azure.dataset.AzureBlobDataset;
import org.talend.components.common.converters.ParquetConverter;
import org.talend.components.azure.service.AzureBlobComponentServices;
import org.talend.components.azure.service.MessageService;
import org.talend.components.azure.service.RegionUtils;
import org.talend.sdk.component.api.exception.ComponentException;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.ListBlobItem;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParquetBlobFileReader extends BlobFileReader {

    public ParquetBlobFileReader(AzureBlobDataset config, RecordBuilderFactory recordBuilderFactory,
            AzureBlobComponentServices connectionServices, MessageService messageService)
            throws URISyntaxException, StorageException {
        super(config, recordBuilderFactory, connectionServices, messageService);
    }

    @Override
    protected ItemRecordIterator initItemRecordIterator(Iterable<ListBlobItem> blobItems) {
        return new ParquetRecordIterator(blobItems, getRecordBuilderFactory());
    }

    private class ParquetRecordIterator extends ItemRecordIterator<GenericRecord> {

        private final static String AZURE_FILESYSTEM_PROPERTY_KEY = "fs.azure";

        private final static String AZURE_FILESYSTEM_PROPERTY_VALUE =
                "org.apache.hadoop.fs.azure.NativeAzureFileSystem";

        private ParquetConverter converter;

        private Configuration hadoopConfig;

        private String accountName;

        private String endpointSuffix;

        private ParquetReader<GenericRecord> reader;

        private GenericRecord currentRecord;

        private ParquetRecordIterator(Iterable<ListBlobItem> blobItemsList, RecordBuilderFactory recordBuilderFactory) {
            super(blobItemsList, recordBuilderFactory);
            initConfig();
            takeFirstItem();
        }

        private void initConfig() {
            hadoopConfig = new Configuration();
            hadoopConfig.set(AZURE_FILESYSTEM_PROPERTY_KEY, AZURE_FILESYSTEM_PROPERTY_VALUE);
            if (getConfig().getConnection().isUseAzureSharedSignature()) {
                RegionUtils ru = new RegionUtils(getConfig().getConnection().getSignatureConnection());
                accountName = ru.getAccountName4SignatureAuth();
                endpointSuffix = ru.getEndpointSuffix4SignatureAuth();
                String sasKey = RegionUtils
                        .getSasKey4SignatureAuth(getConfig().getContainerName(), accountName, endpointSuffix);
                String token = ru.getToken4SignatureAuth();
                hadoopConfig.set(sasKey, token);
            } else if (getConfig().getConnection().getAccountConnection().getAuthType() == AzureAuthType.BASIC) {
                accountName = getConfig().getConnection().getAccountConnection().getAccountName();
                endpointSuffix = getConfig().getConnection().getEndpointSuffix();
                String accountCredKey = RegionUtils.getAccountCredKey4AccountAuth(accountName, endpointSuffix);
                hadoopConfig.set(accountCredKey, getConfig().getConnection().getAccountConnection().getAccountKey());
            } else {
                /*
                 * Azure Active Directory auth doesn't supported by hadoop-azure lib for now.
                 * We do not support it in the component either
                 * https://issues.apache.org/jira/browse/HADOOP-17973
                 */
                throw new ComponentException(getMessageService().authTypeNotSupportedForParquet());
            }
        }

        @Override
        protected Record convertToRecord(GenericRecord next) {
            if (converter == null) {
                converter = ParquetConverter.of(getRecordBuilderFactory());
            }

            return converter.toRecord(next);
        }

        @Override
        protected void readItem() {
            closePreviousInputStream();

            boolean isHttpsConnectionUsed = getConfig().getConnection().isUseAzureSharedSignature()
                    || getConfig().getConnection().getAccountConnection().getProtocol().equals(Protocol.HTTPS);
            String blobURI = RegionUtils
                    .getBlobURI(isHttpsConnectionUsed, getConfig().getContainerName(), accountName,
                            endpointSuffix, getCurrentItem().getName());
            try {
                InputFile file = HadoopInputFile.fromPath(new org.apache.hadoop.fs.Path(blobURI), hadoopConfig);
                reader = AvroParquetReader.<GenericRecord> builder(file).build();
                currentRecord = reader.read();
            } catch (IOException e) {
                log.error("Can't read item", e);
            }
        }

        @Override
        protected boolean hasNextRecordTaken() {
            return currentRecord != null;
        }

        @Override
        protected GenericRecord takeNextRecord() {
            GenericRecord currentRecord = this.currentRecord;
            try {
                // read next line for next method call
                this.currentRecord = reader.read();
            } catch (IOException e) {
                log.error("Can't read record from file " + getCurrentItem().getName(), e);
            }

            return currentRecord;
        }

        @Override
        protected void complete() {
            closePreviousInputStream();
        }

        private void closePreviousInputStream() {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.warn("Can't close stream", e);
                }
            }
        }
    }

}
