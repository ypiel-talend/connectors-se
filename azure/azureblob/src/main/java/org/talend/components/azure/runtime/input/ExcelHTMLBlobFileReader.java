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

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Iterator;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.ParseError;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.talend.components.azure.dataset.AzureBlobDataset;
import org.talend.components.azure.service.AzureBlobComponentServices;
import org.talend.components.azure.service.MessageService;
import org.talend.components.common.stream.input.excel.HTMLToRecord;
import org.talend.sdk.component.api.exception.ComponentException;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.ListBlobItem;

public class ExcelHTMLBlobFileReader extends BlobFileReader {

    private HTMLToRecord converter;

    private Schema columns;

    public ExcelHTMLBlobFileReader(AzureBlobDataset config, RecordBuilderFactory recordBuilderFactory,
            AzureBlobComponentServices connectionServices, MessageService messageService)
            throws URISyntaxException, StorageException {
        super(config, recordBuilderFactory, connectionServices, messageService);
    }

    @Override
    protected ItemRecordIterator initItemRecordIterator(Iterable<ListBlobItem> blobItems) {
        return new HTMLRecordIterator(blobItems, getRecordBuilderFactory());
    }

    private class HTMLRecordIterator extends ItemRecordIterator<Element> {

        private Iterator<Element> rowIterator;

        private HTMLRecordIterator(Iterable<ListBlobItem> blobItemsList, RecordBuilderFactory recordBuilderFactory) {
            super(blobItemsList, recordBuilderFactory);
            takeFirstItem();
        }

        @Override
        protected void readItem() {
            try (InputStream input = getCurrentItem().openInputStream()) {
                Document document =
                        Jsoup.parse(input, getConfig().getExcelOptions()
                                .effectiveHTMLFileEncoding(getMessageService()::encodingNotSupported), "");

                Element body = document.body();
                Elements rows = body.getElementsByTag("tr");
                if (rows.isEmpty()) {
                    throw new ComponentException(getMessageService().fileIsNotValidExcelHTML());
                }
                rowIterator = rows.iterator();
                if (rows.first().getElementsByTag("th").size() > 0) {
                    // infer schema of html header row and ignore result
                    convertToRecord(rowIterator.next());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected boolean hasNextRecordTaken() {
            return rowIterator.hasNext();
        }

        @Override
        protected Element takeNextRecord() {
            return rowIterator.next();
        }

        @Override
        protected Record convertToRecord(Element row) {
            if (converter == null) {
                converter = new HTMLToRecord(getRecordBuilderFactory());
            }

            if (columns == null) {
                columns = converter.inferSchema(row);
            }
            return converter.toRecord(columns, row);
        }

        @Override
        protected void complete() {
            // NOOP
        }
    }
}
