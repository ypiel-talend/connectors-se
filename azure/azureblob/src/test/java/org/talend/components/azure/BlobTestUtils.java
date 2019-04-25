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

package org.talend.components.azure;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.avro.data.RecordBuilder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.talend.components.azure.common.csv.CSVFormatOptions;
import org.talend.components.azure.dataset.AzureBlobDataset;
import org.talend.components.azure.runtime.converters.CSVConverter;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

public class BlobTestUtils {

    @Service
    private static RecordBuilderFactory recordBuilderFactory;

    public static void createStorage(String storageName, CloudStorageAccount connectionAccount) throws URISyntaxException, StorageException {
        CloudBlobClient blobConnection = connectionAccount.createCloudBlobClient();
        blobConnection.getContainerReference(storageName).createIfNotExists();
    }

    public static void createAndPopulateFileInStorage(CloudStorageAccount connectionAccount, AzureBlobDataset fileOptions, List<String> recordSchema, int recordsSize) throws Exception {
        String fileName = "file" + RandomStringUtils.randomAlphabetic(5);
        CloudBlockBlob file = connectionAccount.createCloudBlobClient().getContainerReference(fileOptions.getContainerName())
                .getBlockBlobReference(fileOptions.getDirectory() + "/" + fileName);
        byte[] content = null;
                switch (fileOptions.getFileFormat()) {
                    case CSV:
                        content = createCSVFileContent(recordsSize,
                                recordSchema,
                                fileOptions.getCsvOptions());
                        break;
                    case EXCEL:
                    case AVRO:
                    case PARQUET:
                }
                try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content)) {
                    file.upload(inputStream, content.length);
                }
    }

    private static byte[] createCSVFileContent(int recordsSize, List<String> columns, CSVFormatOptions formatOptions) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream));
        CSVFormat format = CSVConverter.of(formatOptions.isUseHeader()).createCSVFormat(
                CSVConverter.getFieldDelimiterValue(formatOptions),
                CSVConverter.getRecordDelimiterValue(formatOptions),
                formatOptions.getTextEnclosureCharacter(),
                formatOptions.getEscapeCharacter());

        CSVPrinter printer = new CSVPrinter(writer, format);

        for (int i = 0; i < recordsSize; i++) {
            List<String> values = new ArrayList<>();
            columns.forEach(column -> values.add(RandomStringUtils.randomAlphabetic(5)));

            printer.printRecord(values.toArray());
        }
        printer.flush();
        printer.close();
        return byteArrayOutputStream.toByteArray();
    }

    public static List<Record> readFilesFromDirectory() {
        return null;
    }

    public static void deleteStorage(String storageName, CloudStorageAccount connectionAccount) throws URISyntaxException, StorageException {
        CloudBlobClient blobConnection = connectionAccount.createCloudBlobClient();
        blobConnection.getContainerReference(storageName).deleteIfExists();
    }
}
