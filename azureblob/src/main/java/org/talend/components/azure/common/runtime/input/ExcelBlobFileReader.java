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

package org.talend.components.azure.common.runtime.input;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.talend.components.azure.common.excel.ExcelFormatOptions;
import org.talend.components.azure.common.exception.BlobRuntimeException;
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

public class ExcelBlobFileReader extends BlobFileReader {

    private ExcelFormatOptions excelConfig;

    private FileRecordIterator recordIterator;

    public ExcelBlobFileReader(AzureBlobDataset config, RecordBuilderFactory recordBuilderFactory,
            AzureBlobConnectionServices connectionServices) throws URISyntaxException, StorageException {
        super(recordBuilderFactory);
        this.excelConfig = config.getExcelOptions();

        CloudStorageAccount connection = connectionServices.createStorageAccount(config.getConnection());
        CloudBlobClient blobClient = connectionServices.createCloudBlobClient(connection,
                AzureBlobConnectionServices.DEFAULT_RETRY_POLICY);
        CloudBlobContainer container = blobClient.getContainerReference(config.getContainerName());

        Iterable<ListBlobItem> blobItems = container.listBlobs(config.getDirectory(), true);
        recordIterator = new FileRecordIterator(blobItems);
    }

    @Override
    public Record readRecord() {
        return recordIterator.next();
    }

    private class FileRecordIterator implements Iterator<Record> {

        private Iterator<ListBlobItem> blobItems;

        private Queue<Row> rows;

        private CloudBlob currentItem;

        private List<String> columns;

        public FileRecordIterator(Iterable<ListBlobItem> blobItemsList) {
            this.blobItems = blobItemsList.iterator();
            this.rows = new LinkedBlockingQueue<>();
            takeFirstItem();
        }

        @Override
        public boolean hasNext() {
            throw new UnsupportedOperationException("Use next() method until return null");
        }

        @Override
        public Record next() {
            Row next = nextRow();

            return next != null ? convertToRecord(next) : null;
        }

        private Record convertToRecord(Row next) {
            if (columns == null) {
                // TODO header
                columns = inferSchemaInfo(next, false);
            }

            Record.Builder recordBuilder = getRecordBuilderFactory().newRecordBuilder();
            for (int i = 0;; i++) {
                Cell cell = next.getCell(i);
                if (cell == null)
                    break;
                if (cell.getCellType() != CellType.STRING) {
                    cell.setCellType(CellType.STRING); // TODO should we do that or other way exist?
                }
                recordBuilder.withString(columns.get(i), cell.getStringCellValue());
            }
            return recordBuilder.build();
        }

        private List<String> inferSchemaInfo(Row next, boolean isHeader) {
            List<String> columns = new ArrayList<>();
            for (int i = 0;; i++) {
                Cell cell = next.getCell(i);
                if (cell == null)
                    break;

                String columnName;
                if (isHeader) {
                    columnName = cell.getStringCellValue();
                } else {
                    columnName = "field" + i;
                }

                columns.add(columnName);
            }
            return columns;
        }

        private Row nextRow() {
            if (currentItem == null) {
                return null; // No items exists
            }

            if (rows.size() > 0) {
                return rows.poll();
            } else if (blobItems.hasNext()) {
                currentItem = (CloudBlob) blobItems.next();
                readItem();
                return nextRow(); // read record from next item
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
            try (InputStream input = currentItem.openInputStream()) {

                Workbook wb = new HSSFWorkbook(input); // TODO excel format
                Sheet sheet = wb.getSheet(excelConfig.getSheetName());

                for (int i = 0;; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null)
                        break;
                    rows.add(row);
                }

            } catch (Exception e) {
                throw new BlobRuntimeException(e);
            }
        }
    }
}
