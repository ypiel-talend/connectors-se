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
package org.talend.components.azure.runtime.output;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;

import org.apache.hadoop.fs.Path;
import org.apache.parquet.hadoop.ParquetFileWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.talend.components.azure.output.BlobOutputConfiguration;
import org.talend.components.azure.service.AzureBlobComponentServices;
import org.talend.components.common.service.azureblob.AzureComponentServices;
import org.talend.components.common.stream.output.parquet.TCKParquetWriterBuilder;
import org.talend.sdk.component.api.record.Record;

public class ParquetBlobFileWriter extends BlobFileWriter {

    private BlobOutputConfiguration config;

    public ParquetBlobFileWriter(BlobOutputConfiguration config, AzureBlobComponentServices connectionServices)
            throws Exception {
        super(config, connectionServices);
        this.config = config;
    }

    @Override
    public void newBatch() {
        super.newBatch();

        try {
            generateFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void generateFile(String directoryName) throws URISyntaxException, StorageException {
        String fileName = directoryName + config.getBlobNameTemplate() + System.currentTimeMillis() + ".parquet";

        CloudBlob blob = getContainer().getBlockBlobReference(fileName);
        while (blob.exists(null, null, AzureComponentServices.getTalendOperationContext())) {
            fileName = directoryName + config.getBlobNameTemplate() + System.currentTimeMillis() + ".parquet";
            blob = getContainer().getBlockBlobReference(fileName);
        }

        setCurrentItem(blob);
    }

    @Override
    public void flush() {
        if (getBatch().isEmpty()) {
            return;
        }

        File tempFilePath = null;
        try {
            tempFilePath = File.createTempFile("tempFile", ".parquet");
            Path tempFile = new org.apache.hadoop.fs.Path(tempFilePath.getPath());
            final TCKParquetWriterBuilder builder = new TCKParquetWriterBuilder(tempFile);
            builder.withSchema(this.getSchema()).withWriteMode(ParquetFileWriter.Mode.OVERWRITE);
            try (final ParquetWriter<Record> writer = builder.build()) {
                for (Record r : getBatch()) {
                    writer.write(r);
                }
            }

            try (OutputStream blobOutputStream = this.currentOutputStream()) {
                Files.copy(tempFilePath.toPath(), blobOutputStream);
            }
        } catch (IOException | StorageException e) {
            throw new RuntimeException(e);
        } finally {
            getBatch().clear();
            if (tempFilePath != null) {
                tempFilePath.delete();
            }
        }
    }

    protected OutputStream currentOutputStream() throws StorageException {
        return ((CloudBlockBlob) getCurrentItem()).openOutputStream();
    }
}
