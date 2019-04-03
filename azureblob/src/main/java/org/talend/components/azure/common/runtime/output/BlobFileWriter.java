/*
 * Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

package org.talend.components.azure.common.runtime.output;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.talend.components.azure.output.BlobOutputConfiguration;
import org.talend.components.azure.service.AzureBlobConnectionServices;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudAppendBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;

public abstract class BlobFileWriter {

    private CloudStorageAccount connection;

    private List<Record> batch;

    private Schema schema;

    private CloudAppendBlob blobItem;

    private final CloudBlobContainer container;

    private final BlobOutputConfiguration configuration;

    public BlobFileWriter(BlobOutputConfiguration config, AzureBlobConnectionServices connectionServices) throws Exception {
        this.connection = connectionServices.createStorageAccount(config.getDataset().getConnection());
        this.configuration = config;
        CloudBlobClient blobClient = connectionServices.createCloudBlobClient(connection,
                AzureBlobConnectionServices.DEFAULT_RETRY_POLICY);
        container = blobClient.getContainerReference(config.getDataset().getContainerName());
    }

    public void newBatch() {
        batch = new LinkedList<>();
    }

    public void generateFile() throws URISyntaxException, StorageException {
        blobItem = container
                .getAppendBlobReference(configuration.getDataset().getDirectory() + "/" + configuration.getBlobName()); // TODO
                                                                                                                        // check
                                                                                                                        // separator
                                                                                                                        // is
                                                                                                                        // always
                                                                                                                        // "/"
        // TODO not replace if append
        blobItem.createOrReplace();
    }

    public void writeRecord(Record record) {
        if (schema == null) {
            schema = record.getSchema();
        }

        batch.add(record);
    }

    protected List<Record> getBatch() {
        return batch;
    }

    protected CloudAppendBlob getBlobItem() {
        return blobItem;
    }

    protected Schema getSchema() {
        return schema;
    }

    public abstract void flush() throws IOException, StorageException;

    // TODO move it
    public static class BlobFileWriterFactory {

        public static BlobFileWriter getWriter(BlobOutputConfiguration config, AzureBlobConnectionServices connectionServices)
                throws Exception {
            switch (config.getDataset().getFileFormat()) {
            case CSV:
                return new CSVBlobFileWriter(config, connectionServices);
            case AVRO:
                return new AvroBlobFileWriter(config, connectionServices);
            case EXCEL:
                return new ExcelBlobFileWriter(config, connectionServices);
            case PARQUET:
                return new ParquetBlobFileWriter(config, connectionServices);
            default:
                throw new IllegalArgumentException("Unsupported file format"); // shouldn't be here
            }
        }
    }
}
