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
package org.talend.components.adlsgen2.runtime.input;

import com.azure.storage.file.datalake.DataLakeFileClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.talend.components.adlsgen2.dataset.AdlsGen2DataSet;
import org.talend.components.adlsgen2.datastore.AdlsGen2Connection;
import org.talend.components.adlsgen2.input.InputConfiguration;
import org.talend.components.adlsgen2.service.AdlsGen2Service;
import org.talend.components.adlsgen2.service.BlobInformations;
import org.talend.components.common.connection.adls.AuthMethod;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.junit5.WithComponents;

@WithComponents("org.talend.components.adlsgen2")
class ParquetBlobReaderTest {

    @Service
    private RecordBuilderFactory factory;

    @Test
    void readRecordTest() {
        final AdlsGen2Service service = new AdlsGen2Service() {

            @Override
            public List<BlobInformations> getBlobs(AdlsGen2DataSet dataSet) {
                final BlobInformations info1 = new BlobInformations();
                info1.setDirectory("dir1");
                info1.setFileName("file1.parquet");
                final BlobInformations info2 = new BlobInformations();
                info2.setDirectory("dir2");
                info2.setFileName("file2.parquet");
                return Arrays.asList(info1, info2);
            }

            @Override
            protected void readDatalakeFile(final DataLakeFileClient blobFileClient, final OutputStream out) {
                final String filePath = blobFileClient.getFilePath();

                final byte[] buffer = new byte[1024];
                try (final InputStream resource =
                        Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath)) {
                    int size = resource.read(buffer);
                    while (size > 0) {
                        out.write(buffer, 0, size);
                        size = resource.read(buffer);
                    }
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
            }
        };

        final InputConfiguration config = new InputConfiguration();
        final AdlsGen2DataSet ds = new AdlsGen2DataSet();
        config.setDataSet(ds);
        final AdlsGen2Connection cnx = new AdlsGen2Connection();
        ds.setConnection(cnx);
        cnx.setAuthMethod(AuthMethod.SharedKey);
        cnx.setSharedKey(Base64.getEncoder()
                .encodeToString("accountname=jamesbond;accountkey=007".getBytes(StandardCharsets.UTF_8)));
        cnx.setAccountName("jamesbond");
        ds.setFilesystem("local");
        ds.setBlobPath("blob");

        final ParquetBlobReader reader = new ParquetBlobReader(config, this.factory, service);
        reader.initialize();
        for (int i = 0; i < 12; i++) {
            final Record record = reader.readRecord();
            Assertions.assertNotNull(record);
        }
        final Record record = reader.readRecord();
        Assertions.assertNull(record);
    }

}