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

package org.talend.components.azure.common.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.talend.components.azure.dataset.AzureBlobDataset;
import org.talend.components.azure.service.AzureBlobConnectionServices;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.ListBlobItem;

public class CSVBlobFileReader extends BlobFileReader {

    // TODO move field in abstract class

    private RecordBuilderFactory recordBuilderFactory;

    private CloudStorageAccount connection;

    private AzureBlobConnectionServices connectionService;

    private FileRecordIterator recordIterator;

    public CSVBlobFileReader(AzureBlobDataset config, RecordBuilderFactory recordBuilderFactory,
            AzureBlobConnectionServices connectionServices) throws URISyntaxException, StorageException {
        this.recordBuilderFactory = recordBuilderFactory;
        this.connectionService = connectionServices;
        this.connection = connectionService.createStorageAccount(config.getConnection()); // TODO no need of it?

        CloudBlobClient blobClient = connection.createCloudBlobClient();
        CloudBlobContainer container = blobClient.getContainerReference(config.getContainerName());

        Iterable<ListBlobItem> blobItems = container.listBlobs(config.getDirectory(), true);
        recordIterator = new FileRecordIterator(blobItems);
        for (ListBlobItem item : blobItems) {
            System.out.println(((CloudBlob) item).getName());
        }

    }

    @Override
    public Record readRecord() {
        return readNextLine();
    }

    private Record readNextLine() {
        CSVRecord next = recordIterator.next();

        if (next == null)
            return null;

        return recordBuilderFactory.newRecordBuilder().withString("s", next.toString()).build(); // TODO stub
    }

    private class FileRecordIterator implements Iterator<CSVRecord> {

        private Iterator<ListBlobItem> blobItems;

        private Iterator<CSVRecord> recordIterator;

        private CloudBlob currentItem;

        public FileRecordIterator(Iterable<ListBlobItem> blobItemsList) {
            this.blobItems = blobItemsList.iterator();
        }

        @Override
        public boolean hasNext() {
            throw new UnsupportedOperationException("Use next() method until return not null");
        }

        @Override
        public CSVRecord next() {
            if (currentItem == null) {
                if (blobItems.hasNext()) {
                    currentItem = (CloudBlob) blobItems.next();
                    readFile();
                } else {
                    return null;
                }
            } // TODO move to constructor

            if (recordIterator.hasNext()) {
                return recordIterator.next();
            } else if (blobItems.hasNext()) {
                currentItem = (CloudBlob) blobItems.next();
                readFile();
                return next(); // recursion
            } else {
                return null;
            }
        }

        private void readFile() {
            try (InputStream input = currentItem.openInputStream();
                    InputStreamReader inr = new InputStreamReader(input, StandardCharsets.UTF_8);
                    org.apache.commons.csv.CSVParser parser = new CSVParser(inr, CSVFormat.DEFAULT)) { // TODO custom encoding

                this.recordIterator = parser.getRecords().iterator();
            } catch (Exception e) {
                throw new RuntimeException(e); // TODO custom exception
            }
        }
    }
}
