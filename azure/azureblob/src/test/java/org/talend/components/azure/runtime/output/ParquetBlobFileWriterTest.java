package org.talend.components.azure.runtime.output;


import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.talend.components.azure.BlobTestUtils;
import org.talend.components.azure.dataset.AzureBlobDataset;
import org.talend.components.azure.datastore.AzureCloudConnection;
import org.talend.components.azure.output.BlobOutputConfiguration;
import org.talend.components.azure.service.AzureBlobComponentServices;
import org.talend.components.azure.service.MessageService;
import org.talend.components.common.connection.azureblob.AzureStorageConnectionAccount;
import org.talend.components.common.service.azureblob.AzureComponentServices;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.record.RecordBuilderFactoryImpl;

@WithComponents("org.talend.components.azure")
class ParquetBlobFileWriterTest {

    @Injected
    private BaseComponentsHandler componentsHandler;

    @Service
    private AzureBlobComponentServices services;

    @Service
    private MessageService i18n;

    @Test
    void writeRecordTest() throws Exception {
        final URL outRep = Thread.currentThread().getContextClassLoader().getResource("parquet/out");
        final AzureComponentServices service = new AzureComponentServices();
        BlobTestUtils.inject(this.componentsHandler.asManager(), AzureComponentServices.class, service);
        BlobTestUtils.inject(this.componentsHandler.asManager(), AzureBlobComponentServices.class, this.services);
        BlobTestUtils.inject(this.componentsHandler.asManager(), MessageService.class, this.i18n);

        final BlobOutputConfiguration config = new BlobOutputConfiguration();
        final AzureBlobDataset ds = new AzureBlobDataset();
        config.setDataset(ds);
        final AzureCloudConnection cnx = new AzureCloudConnection();
        ds.setConnection(cnx);
        ds.setContainerName("container");
        final AzureStorageConnectionAccount account = new AzureStorageConnectionAccount();
        cnx.setAccountConnection(account);
        cnx.setUseAzureSharedSignature(false);
        account.setAccountName("jamesbond");
        account.setAccountKey(new String(Base64.getEncoder().encode("007".getBytes(StandardCharsets.UTF_8))));

        final RecordBuilderFactory factory = new RecordBuilderFactoryImpl("test");
        final Schema schema = this.buildSchema(factory);

        final ParquetBlobFileWriter writer = new ParquetBlobFileWriter(config, this.services) {
            @Override
            public void generateFile(String directoryName) throws URISyntaxException, StorageException {
                CloudBlockBlob item = new CloudBlockBlob(URI.create("https://container/xxx.zz"));
                this.setCurrentItem(item);
            }

            @Override
            protected OutputStream currentOutputStream() throws StorageException {
                final File fic = new File(outRep.getPath(), "writeRecordTest.parquet");
                if (fic.exists()) {
                    fic.delete();
                }
                try {
                    fic.createNewFile();
                    return new FileOutputStream(fic);
                }
                catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
            }
        };
        writer.newBatch();

        final Record record1 = this.buildRecord(factory, schema, 1);
        writer.writeRecord(record1);
        final Record record2 = this.buildRecord(factory, schema, 2);
        writer.writeRecord(record2);
        final Record record3 = this.buildRecord(factory, schema, 3);
        writer.writeRecord(record3);
        writer.flush();

        final File fic = new File(outRep.getPath(), "writeRecordTest.parquet");
        Assertions.assertTrue(fic.exists());
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