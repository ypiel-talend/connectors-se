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
package org.talend.components.adlsgen2.runtime.output;

import javax.json.JsonBuilderFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.talend.components.adlsgen2.dataset.AdlsGen2DataSet;
import org.talend.components.adlsgen2.datastore.AdlsGen2Connection;
import org.talend.components.adlsgen2.output.OutputConfiguration;
import org.talend.components.adlsgen2.service.AdlsGen2Service;
import org.talend.components.common.connection.adls.AuthMethod;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.junit5.WithComponents;

@WithComponents("org.talend.components.adlsgen2")
class ParquetBlobWriterTest {

    @Service
    private RecordBuilderFactory factory;

    @Service
    private JsonBuilderFactory json;

    private AdlsGen2Service service = new AdlsGen2Service() {

        @Override
        public boolean blobExists(AdlsGen2DataSet dataSet, String blobName) {
            return false;
        }

        @Override
        public boolean pathCreate(final AdlsGen2DataSet dataSet) {
            final File f = this.getFile(dataSet);
            if (f.exists()) {
                f.delete();
            }
            try {
                return f.createNewFile();
            } catch (final IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }

        @Override
        public void pathUpdate(final AdlsGen2DataSet dataSet, byte[] content, long position) {
            final File file = this.getFile(dataSet);
            try (FileOutputStream out = new FileOutputStream(file)) {
                out.getChannel().position(position).write(ByteBuffer.wrap(content));
            }
            catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }

        @Override
        public void flushBlob(AdlsGen2DataSet dataSet, long position) {
        }

        private File getFile(final AdlsGen2DataSet dataSet) {
            final String blobPath = dataSet.getBlobPath();
            final String rootPath = Thread.currentThread().getContextClassLoader().getResource("./out").getPath();
            return new File(rootPath, blobPath);
        }
    };

    @Test
    void writeRecordTest() {
        final OutputConfiguration config = new OutputConfiguration();
        final AdlsGen2DataSet ds = new AdlsGen2DataSet();
        config.setDataSet(ds);
        final AdlsGen2Connection cnx = new AdlsGen2Connection();
        ds.setConnection(cnx);
        cnx.setAuthMethod(AuthMethod.SharedKey);
        cnx.setSharedKey(Base64.getEncoder()
                .encodeToString("accountname=jamesbond;accountkey=007".getBytes(StandardCharsets.UTF_8)));
        cnx.setAccountName("jamesbond");
        cnx.setTimeout(20);
        ds.setFilesystem("local");
        ds.setBlobPath("blob");

        final ParquetBlobWriter writer = new ParquetBlobWriter(config, factory, json, service);

        final Schema schema = this.buildSchema(this.factory);
        writer.newBatch();

        final Record record1 = this.buildRecord(factory, schema, 1);
        writer.writeRecord(record1);
        final Record record2 = this.buildRecord(factory, schema, 2);
        writer.writeRecord(record2);
        final Record record3 = this.buildRecord(factory, schema, 3);
        writer.writeRecord(record3);
        writer.flush();
    }

    private Record buildRecord(final RecordBuilderFactory factory,
            final Schema schema,
            int index) {
        final Schema.Entry field2 = schema.getEntry("field2");
        final Schema elementSchema = field2.getElementSchema();
        final List<Record> records = IntStream.rangeClosed(0, index)
                .mapToObj((int i) -> factory.newRecordBuilder(elementSchema)
                        .withString("inner", "inner" + (i + 1))
                        .build())
                .collect(Collectors.toList());

        return factory.newRecordBuilder(schema)
                .withArray(field2, records)
                .withString("field1", "value_" + index)
                .build();
    }

    private Schema buildSchema(final RecordBuilderFactory factory) {
        final Schema.Entry field1 = factory.newEntryBuilder()
                .withName("field1")
                .withType(Schema.Type.STRING)
                .withNullable(true)
                .build();

        final Schema.Entry inner = factory.newEntryBuilder()
                .withName("inner")
                .withType(Schema.Type.STRING)
                .withNullable(true)
                .build();

        final Schema subSchema = factory.newSchemaBuilder(Schema.Type.RECORD)
                .withEntry(inner)
                .build();

        final Schema.Entry field2 = factory.newEntryBuilder()
                .withName("field2")
                .withType(Schema.Type.ARRAY)
                .withElementSchema(subSchema)
                .withNullable(true)
                .build();

        return factory.newSchemaBuilder(Schema.Type.RECORD)
                .withEntry(field1)
                .withEntry(field2)
                .build();
    }
}