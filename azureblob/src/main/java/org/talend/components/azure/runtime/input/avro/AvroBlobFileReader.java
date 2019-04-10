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

package org.talend.components.azure.runtime.input.avro;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.talend.components.azure.common.exception.BlobRuntimeException;
import org.talend.components.azure.dataset.AzureBlobDataset;
import org.talend.components.azure.runtime.converters.AvroConverter;
import org.talend.components.azure.runtime.input.BlobFileReader;
import org.talend.components.azure.service.AzureBlobConnectionServices;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.ListBlobItem;

public class AvroBlobFileReader extends BlobFileReader {

    private AvroFileRecordIterator iterator;

    public AvroBlobFileReader(AzureBlobDataset config, RecordBuilderFactory recordBuilderFactory,
            AzureBlobConnectionServices connectionServices) throws URISyntaxException, StorageException {
        super(recordBuilderFactory);
        CloudStorageAccount connection = connectionServices.createStorageAccount(config.getConnection());
        CloudBlobClient blobClient = connectionServices.createCloudBlobClient(connection,
                AzureBlobConnectionServices.DEFAULT_RETRY_POLICY);
        CloudBlobContainer container = blobClient.getContainerReference(config.getContainerName());

        Iterable<ListBlobItem> blobItems = container.listBlobs(config.getDirectory(), true);
        iterator = new AvroFileRecordIterator(blobItems);
    }

    @Override
    public Record readRecord() {
        return iterator.next();
    }

    private class AvroFileRecordIterator extends ItemRecordIterator<GenericRecord> {

        private LinkedList<GenericRecord> recordList;

        private AvroConverter converter;

        public AvroFileRecordIterator(Iterable<ListBlobItem> blobItemsList) {
            super(blobItemsList);
        }

        @Override
        protected Record convertToRecord(GenericRecord next) {
            if (converter == null) {
                // todo not needed for datastreams
                AvroConverter.recordBuilderFactory = AvroBlobFileReader.this.getRecordBuilderFactory();
                converter = AvroConverter.of();
            }

            return converter.toRecord(next);
        }

        @Override
        protected void readItem() {
            try (InputStream input = getCurrentItem().openInputStream()) {

                DatumReader reader = new GenericDatumReader();
                DataFileStream<GenericRecord> dataFileStream = new DataFileStream<GenericRecord>(input, reader);
                System.out.println(dataFileStream.getBlockSize());
                dataFileStream.forEach(record -> recordList.add(record));
            } catch (Exception e) {
                throw new BlobRuntimeException(e);
            }
        }

        @Override
        protected boolean hasNextRecordTaken() {
            return recordList.size() > 0;
        }

        @Override
        protected GenericRecord takeNextRecord() {
            return recordList.poll();
        }

        @Override
        protected void initRecordContainer() {
            recordList = new LinkedList<>();
        }
    }
}
