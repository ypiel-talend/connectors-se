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

package org.talend.components.azure.runtime.output;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.talend.components.azure.common.Encoding;
import org.talend.components.azure.common.csv.CSVFormatOptions;
import org.talend.components.azure.common.exception.BlobRuntimeException;
import org.talend.components.azure.common.service.AzureComponentServices;
import org.talend.components.azure.output.BlobOutputConfiguration;
import org.talend.components.azure.runtime.converters.CSVConverter;
import org.talend.components.azure.service.AzureBlobComponentServices;
import org.talend.components.azure.service.FormatUtils;
import org.talend.sdk.component.api.record.Record;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudAppendBlob;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CSVBlobFileWriter extends BlobFileWriter {

    private BlobOutputConfiguration config;

    private final CSVFormatOptions configCSV;

    private boolean fileIsEmpty = true;

    public CSVBlobFileWriter(BlobOutputConfiguration config, AzureBlobComponentServices connectionServices) throws Exception {
        super(config, connectionServices);
        this.config = config;
        this.configCSV = config.getDataset().getCsvOptions();

    }

    @Override
    public void generateFile() throws URISyntaxException, StorageException {
        CloudAppendBlob currentItem = getContainer()
                .getAppendBlobReference(config.getDataset().getDirectory() + "/" + config.getBlobNameTemplate() + ".csv");

        if (currentItem.exists()) {
            log.warn("File {} existed, will be recreated", currentItem.getName());
        }

        currentItem.createOrReplace();
        setCurrentItem(currentItem);
    }

    @Override
    public void newBatch() {
        super.newBatch();

        if (getCurrentItem() == null) {
            try {
                generateFile();
            } catch (Exception e) {
                throw new BlobRuntimeException(e);
            }
        }
    }

    // TODO move common implementation to the abstract class
    @Override
    public void flush() throws IOException, StorageException {
        if (getBatch().isEmpty()) {
            return;
        }

        String content = convertBatchToString();

         if (fileIsEmpty && configCSV.isUseHeader() && configCSV.getHeader() > 0) {
            appendHeader();
        }

        byte[] contentBytes = content.getBytes(FormatUtils.getUsedEncodingValue(config.getDataset()));
        ((CloudAppendBlob) getCurrentItem()).appendFromByteArray(contentBytes, 0, contentBytes.length, null, null,
                AzureComponentServices.getTalendOperationContext());

        fileIsEmpty = false;

        getBatch().clear();
    }

    private void appendHeader() throws IOException, StorageException {
        if (getSchema() == null || getSchema().getEntries().size() == 0)
            return;
        StringBuilder headerBuilder = new StringBuilder();
        for (int i = 0; i < configCSV.getHeader() - 1; i++) {
            headerBuilder.append("//header line").append(CSVConverter.getRecordDelimiterValue(configCSV));
        }

        headerBuilder.append(getSchema().getEntries().get(0).getName());
        for (int i = 1; i < getSchema().getEntries().size(); i++) {
            headerBuilder.append(CSVConverter.getFieldDelimiterValue(configCSV))
                    .append(getSchema().getEntries().get(i).getName());
        }
        ((CloudAppendBlob) getCurrentItem())
                .appendText(headerBuilder.toString() + CSVConverter.getRecordDelimiterValue(configCSV));
        fileIsEmpty = false;
    }

    private String convertBatchToString() throws IOException {
        StringWriter stringWriter = new StringWriter();
        Iterator<Record> recordIterator = getBatch().iterator();
        CSVFormat format = CSVConverter.of(configCSV.isUseHeader()).createCSVFormat(
                CSVConverter.getFieldDelimiterValue(configCSV),
                CSVConverter.getRecordDelimiterValue(configCSV),
                configCSV.getTextEnclosureCharacter(),
                configCSV.getEscapeCharacter());

        CSVPrinter printer = new CSVPrinter(stringWriter, format);


        while (recordIterator.hasNext()) {
            printer.printRecord(convertRecordToArray(recordIterator.next()));
        }

        printer.flush();
        printer.close();

        return stringWriter.toString();
    }

    private Object[] convertRecordToArray(Record record) {
        Object[] array = new Object[record.getSchema().getEntries().size()];
        for (int i = 0; i < getSchema().getEntries().size(); i++) {
            array[i] = record.get(Object.class, getSchema().getEntries().get(i).getName());
        }

        return array;
    }

    @Override
    public void complete() {
        // NOOP
    }
}
