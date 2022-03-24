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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.talend.components.azure.output.BlobOutputConfiguration;
import org.talend.components.azure.service.AzureBlobComponentServices;
import org.talend.components.common.Constants;
import org.talend.components.common.service.azureblob.AzureComponentServices;
import org.talend.components.common.stream.output.avro.RecordToAvro;
import org.talend.sdk.component.api.record.Record;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlob;

public class AvroBlobFileWriter extends BlobFileWriter {

    private BlobOutputConfiguration config;

    private RecordToAvro converter;

    public AvroBlobFileWriter(BlobOutputConfiguration config, AzureBlobComponentServices connectionServices)
            throws Exception {
        super(config, connectionServices);
        this.config = config;
        converter = new RecordToAvro(Constants.AZURE_BLOB_NAMESPACE);
    }

    @Override
    public void generateFile(String directoryName) throws URISyntaxException, StorageException {
        String fileName = directoryName + config.getBlobNameTemplate() + UUID.randomUUID() + ".avro";
        CloudBlob blob = getContainer().getBlockBlobReference(fileName);
        while (blob.exists(null, null, AzureComponentServices.getTalendOperationContext())) {
            fileName = directoryName + config.getBlobNameTemplate() + UUID.randomUUID() + ".avro";
            blob = getContainer().getBlockBlobReference(fileName);
        }

        setCurrentItem(blob);
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
    public void flush() throws StorageException, IOException {
        if (getBatch().isEmpty()) {
            return;
        }

        byte[] batchBytes = convertBatchToBytes();
        getCurrentItem().uploadFromByteArray(batchBytes, 0, batchBytes.length);
        getBatch().clear();
    }

    private byte[] convertBatchToBytes() throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>();
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter);
        dataFileWriter.create(converter.fromRecordSchema(getSchema()), byteBuffer);
        for (Record record : getBatch()) {
            dataFileWriter.append(converter.fromRecord(record));
        }
        dataFileWriter.flush();
        return byteBuffer.toByteArray();
    }
}
