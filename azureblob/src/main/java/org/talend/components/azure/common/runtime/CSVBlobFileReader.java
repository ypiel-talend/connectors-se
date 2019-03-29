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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.talend.components.azure.common.csv.CSVFormatOptions;
import org.talend.components.azure.common.csv.FieldDelimiter;
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

    // TODO move fields in abstract class

    private RecordBuilderFactory recordBuilderFactory;

    private CloudStorageAccount connection;

    private AzureBlobConnectionServices connectionService;

    private FileRecordIterator recordIterator;

    private CSVFormatOptions configCSV;

    public CSVBlobFileReader(AzureBlobDataset config, RecordBuilderFactory recordBuilderFactory,
            AzureBlobConnectionServices connectionServices) throws URISyntaxException, StorageException {
        this.recordBuilderFactory = recordBuilderFactory;
        this.connectionService = connectionServices;
        this.connection = connectionService.createStorageAccount(config.getConnection()); // TODO no need of it?
        this.configCSV = config.getCsvOptions();

        CloudBlobClient blobClient = connection.createCloudBlobClient();
        CloudBlobContainer container = blobClient.getContainerReference(config.getContainerName());

        Iterable<ListBlobItem> blobItems = container.listBlobs(config.getDirectory(), true);
        // config.getCsvOptions()
        recordIterator = new FileRecordIterator(blobItems);
    }

    @Override
    public Record readRecord() {
        return recordIterator.next();
    }

    private class FileRecordIterator implements Iterator<Record> {

        private Iterator<ListBlobItem> blobItems;

        private Iterator<CSVRecord> recordIterator;

        private CloudBlob currentItem;

        private CSVFormat format;

        private List<String> columns;

        public FileRecordIterator(Iterable<ListBlobItem> blobItemsList) {
            this.blobItems = blobItemsList.iterator();

            takeFirstItem();
        }

        @Override
        public boolean hasNext() {
            throw new UnsupportedOperationException("Use next() method until return null");
        }

        @Override
        public Record next() {
            CSVRecord next = nextCSV();

            return next != null ? convertToRecord(next) : null;
        }

        private Record convertToRecord(CSVRecord next) {
            if (columns == null) {
                // TODO header
                columns = inferSchemaInfo(next, true);
            }

            Record.Builder recordBuilder = recordBuilderFactory.newRecordBuilder();
            for (int i = 0; i < next.size(); i++) {
                recordBuilder.withString(columns.get(i), next.get(i));
            }
            return recordBuilder.build(); // TODO stub
        }

        private CSVRecord nextCSV() {
            if (currentItem == null) {
                return null; // No items exists
            }

            if (recordIterator.hasNext()) {
                return recordIterator.next();
            } else if (blobItems.hasNext()) {
                currentItem = (CloudBlob) blobItems.next();
                readItem();
                return nextCSV(); // read record from next item
            } else {
                return null;
            }
        }

        private void takeFirstItem() {
            if (blobItems.hasNext()) {
                currentItem = (CloudBlob) blobItems.next();
                readItem();
            }
        }

        private void readItem() {
            if (format == null) {
                // TODO char
                String delimiterValue = configCSV.getFieldDelimiter() == FieldDelimiter.OTHER
                        ? configCSV.getCustomFieldDelimiter()
                        : configCSV.getFieldDelimiter().getDelimiterValue();
                format = createCSVFormat(delimiterValue.charAt(0), configCSV.getTextEnclosureCharacter(),
                        configCSV.getEscapeCharacter());
            }

            try (InputStream input = currentItem.openInputStream();
                    InputStreamReader inr = new InputStreamReader(input, StandardCharsets.UTF_8); // TODO encoding
                    org.apache.commons.csv.CSVParser parser = new CSVParser(inr, format)) {

                this.recordIterator = parser.getRecords().iterator();
            } catch (Exception e) {
                throw new RuntimeException(e); // TODO custom exception
            }
        }
    }

    // TODO move it
    private static CSVFormat createCSVFormat(char fieldDelimiter, String textEnclosure, String escapeChar) {
        // CSVFormat.RFC4180 use " as quote and no escape char and "," as field
        // delimiter and only quote if quote is set and necessary
        CSVFormat format = CSVFormat.RFC4180.withDelimiter(fieldDelimiter);

        Character textEnclosureCharacter = null;
        if (StringUtils.isNotEmpty(textEnclosure)) {
            textEnclosureCharacter = textEnclosure.charAt(0);
        }

        Character enclosureChar = null;
        if (escapeChar != null && !escapeChar.isEmpty()) {
            enclosureChar = escapeChar.charAt(0);
        }

        // the with method return a new object, so have to assign back
        if (textEnclosureCharacter != null) {
            format = format.withQuote(textEnclosureCharacter);
        } else {
            format = format.withQuote(null);
        }

        if (enclosureChar != null) {
            format = format.withEscape(enclosureChar);
        }
        return format;
    }

    // TODO move it
    private static List<String> inferSchemaInfo(CSVRecord singleHeaderRow, boolean useDefaultFieldName) {
        // ArrayList can Serializable, so can pass to the ExtractCsvRecord
        List<String> result = new ArrayList<>();
        Set<String> existNames = new HashSet<>();
        int index = 0;
        for (int i = 0; i < singleHeaderRow.size(); i++) {
            String fieldName = singleHeaderRow.get(i);
            if (useDefaultFieldName || fieldName == null || fieldName.isEmpty()) {
                fieldName = "field" + i;
            }

            String finalName = correct(fieldName, index++, existNames);
            existNames.add(finalName);

            result.add(finalName);
        }
        return result;
    }
}
