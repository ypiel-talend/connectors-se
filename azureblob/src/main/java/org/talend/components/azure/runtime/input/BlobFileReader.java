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

package org.talend.components.azure.runtime.input;

import java.util.Iterator;

import org.talend.components.azure.runtime.input.excel.ExcelBlobFileReader;
import org.talend.components.azure.dataset.AzureBlobDataset;
import org.talend.components.azure.service.AzureBlobConnectionServices;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

public abstract class BlobFileReader {

    private RecordBuilderFactory recordBuilderFactory;

    public BlobFileReader(RecordBuilderFactory recordBuilderFactory) {
        this.recordBuilderFactory = recordBuilderFactory;
    }

    public abstract Record readRecord();

    protected RecordBuilderFactory getRecordBuilderFactory() {
        return recordBuilderFactory;
    }

    public static class BlobFileReaderFactory {

        public static BlobFileReader getReader(AzureBlobDataset config, RecordBuilderFactory recordBuilderFactory,
                AzureBlobConnectionServices connectionServices) throws Exception {
            switch (config.getFileFormat()) {
            case CSV:
                return new CSVBlobFileReader(config, recordBuilderFactory, connectionServices);
            case AVRO:
                return new AvroBlobFileReader(recordBuilderFactory);
            case EXCEL:
                return new ExcelBlobFileReader(config, recordBuilderFactory, connectionServices);
            case PARQUET:
                return new ParquetBlobFileReader(recordBuilderFactory);
            default:
                throw new IllegalArgumentException("Unsupported file format"); // shouldn't be here
            }
        }
    }

    protected abstract class ItemRecordIterator<T> implements Iterator<Record> {

        private Iterator<ListBlobItem> blobItems;

        private CloudBlob currentItem;

        protected ItemRecordIterator(Iterable<ListBlobItem> blobItemsList) {
            this.blobItems = blobItemsList.iterator();
            initRecordContainer();
            takeFirstItem();
        }

        @Override
        public boolean hasNext() {
            throw new UnsupportedOperationException("Use next() method until return null");
        }

        @Override
        public Record next() {
            T next = readNextItemRecord();

            return next != null ? convertToRecord(next) : null;
        }

        protected CloudBlob getCurrentItem() {
            return currentItem;
        }

        T readNextItemRecord() {
            if (currentItem == null) {
                return null; // No items exists
            }

            if (hasNextRecordTaken()) {
                return takeNextRecord();
            } else if (blobItems.hasNext()) {
                currentItem = (CloudBlob) blobItems.next();
                readItem();
                return readNextItemRecord(); // read record from next item
            } else {
                return null;
            }
        }

        protected abstract void initRecordContainer();

        protected abstract T takeNextRecord();

        protected abstract boolean hasNextRecordTaken();

        protected abstract Record convertToRecord(T next);

        protected abstract void readItem();

        private void takeFirstItem() {
            if (blobItems.hasNext()) {
                currentItem = (CloudBlob) blobItems.next();
                readItem();
            }
        }
    }
}
