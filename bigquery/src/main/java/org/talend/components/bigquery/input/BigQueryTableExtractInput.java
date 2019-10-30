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
package org.talend.components.bigquery.input;

import com.google.api.gax.paging.Page;
import com.google.cloud.ReadChannel;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.talend.components.bigquery.avro.AvroConfiguration;
import org.talend.components.bigquery.avro.AvroConverter;
import org.talend.components.bigquery.datastore.BigQueryConnection;
import org.talend.components.bigquery.service.BigQueryService;
import org.talend.components.bigquery.service.I18nMessage;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.channels.Channels;
import java.util.Iterator;

@Slf4j
public class BigQueryTableExtractInput implements Serializable {

    protected final BigQueryConnection connection;

    protected final BigQueryService service;

    protected final I18nMessage i18n;

    protected final RecordBuilderFactory builderFactory;

    protected final String bucket;

    protected final String gsBlob;

    private transient Storage storage;

    private transient Iterator<Blob> blobsIterator;

    private transient Blob blob;

    private transient DataFileStream<GenericRecord> dataStream;

    private transient boolean loaded = false;

    private transient AvroConverter converter;

    private transient DatumReader<GenericRecord> datumReader;

    public BigQueryTableExtractInput(BigQueryTableExtractInputConfig configuration, final BigQueryService service,
            final I18nMessage i18n, final RecordBuilderFactory builderFactory, final String gsBlob) {
        this.bucket = configuration.getGsBucket();
        this.connection = configuration.getDataStore();
        this.service = service;
        this.i18n = i18n;
        this.builderFactory = builderFactory;
        this.gsBlob = gsBlob;
        System.out.println(gsBlob);
    }

    @PostConstruct
    public void init() {

    }

    @Producer
    public Record next() {
        if (!loaded) {
            try {
                BigQuery bigQuery = BigQueryService.createClient(connection);
                StorageOptions storageOptions = StorageOptions.newBuilder().setCredentials(bigQuery.getOptions().getCredentials())
                        .build();
                storage = new StorageOptions.DefaultStorageFactory().create(storageOptions);
                blob = storage.get(bucket, gsBlob);
                log.info("Working with blob {}.", blob);

                datumReader = new GenericDatumReader<>();
                converter = AvroConverter.of(builderFactory, new AvroConfiguration());

                ReadChannel rc = blob.reader();
                InputStream in = Channels.newInputStream(rc);
                try {
                    dataStream = new DataFileStream<GenericRecord>(in, datumReader);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                loaded = true;
            }
        }

        Record record = null;

        if (blob == null || (dataStream != null && !dataStream.hasNext())) {

            if (dataStream != null) {
                try {
                    dataStream.close();
                } catch (Exception e) {
                    log.warn("Cannot close stream", e);
                }
            }

            if (blobsIterator != null && blobsIterator.hasNext()) {
                blob = blobsIterator.next();
            } else {
                log.info("{} job done", Thread.currentThread().getName());
                return null;
            }
            ReadChannel rc = blob.reader();
            InputStream in = Channels.newInputStream(rc);
            try {
                dataStream = new DataFileStream<GenericRecord>(in, datumReader);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        if (dataStream != null && dataStream.hasNext()) {
            GenericRecord rec = dataStream.next();
            record = converter.toRecord(rec);
        }

        return record;
    }

    @PreDestroy
    public void release() {
        if (blob != null) {
            blob.delete();
        }
    }

    private Iterator<Blob> getBlobs() {
        String prefix = gsBlob.substring(0, gsBlob.lastIndexOf('_') + 1);
        prefix = prefix.substring(prefix.indexOf(bucket) + bucket.length() + 1);
        log.info("Getting blobs with prefix {}", prefix);
        Page<Blob> blobs = storage.list(bucket, Storage.BlobListOption.prefix(prefix));
        return blobs.iterateAll().iterator();
    }

}
