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

package org.talend.components.azure.runtime.input.excel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.talend.components.azure.common.excel.ExcelFormat;
import org.talend.components.azure.common.excel.ExcelFormatOptions;
import org.talend.components.azure.common.exception.BlobRuntimeException;
import org.talend.components.azure.runtime.input.BlobFileReader;
import org.talend.components.azure.dataset.AzureBlobDataset;
import org.talend.components.azure.service.AzureBlobConnectionServices;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.ListBlobItem;

public class ExcelBlobFileReader extends BlobFileReader {

    private ExcelFormatOptions excelConfig;

    private ExcelRecordIterator recordIterator;

    public ExcelBlobFileReader(AzureBlobDataset config, RecordBuilderFactory recordBuilderFactory,
            AzureBlobConnectionServices connectionServices) throws URISyntaxException, StorageException {
        super(recordBuilderFactory);
        this.excelConfig = config.getExcelOptions();

        CloudStorageAccount connection = connectionServices.createStorageAccount(config.getConnection());
        CloudBlobClient blobClient = connectionServices.createCloudBlobClient(connection,
                AzureBlobConnectionServices.DEFAULT_RETRY_POLICY);
        CloudBlobContainer container = blobClient.getContainerReference(config.getContainerName());

        Iterable<ListBlobItem> blobItems = container.listBlobs(config.getDirectory(), true);
        recordIterator = new ExcelRecordIterator(blobItems);
    }

    @Override
    public Record readRecord() {
        return recordIterator.next();
    }

    private class ExcelRecordIterator extends BlobFileReader.ItemRecordIterator<Row> {

        private LinkedList<Row> rows;

        private List<String> columns;

        public ExcelRecordIterator(Iterable<ListBlobItem> blobItemsList) {
            super(blobItemsList);
        }

        @Override
        protected Record convertToRecord(Row next) {
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

        @Override
        protected void readItem() {
            try (InputStream input = getCurrentItem().openInputStream()) {
                Workbook wb;
                if (excelConfig.getExcelFormat() == ExcelFormat.EXCEL97) {
                    wb = new HSSFWorkbook(input);
                } else {
                    wb = new XSSFWorkbook(input);
                } // TODO HTML excel format??
                Sheet sheet = wb.getSheet(excelConfig.getSheetName());

                for (int i = 0;; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null)
                        break;
                    rows.add(row);
                }

            } catch (StorageException | IOException e) {
                throw new BlobRuntimeException(e);
            }
        }

        @Override
        protected boolean hasNextRecordTaken() {
            return rows.size() > 0;
        }

        @Override
        protected Row takeNextRecord() {
            return rows.poll();
        }

        @Override
        protected void initRecordContainer() {
            rows = new LinkedList<>();
        }
    }
}
