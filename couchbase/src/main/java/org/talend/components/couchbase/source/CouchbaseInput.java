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

package org.talend.components.couchbase.source;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.couchbase.service.CouchbaseService;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.talend.sdk.component.api.record.Schema.Type.*;

@Version(1)
@Slf4j
@Documentation("This component reads data from Couchbase.")
public class CouchbaseInput implements Serializable {

    private transient static final Logger LOG = LoggerFactory.getLogger(CouchbaseInput.class);

    private final CouchbaseInputConfiguration configuration;

    private final CouchbaseService service;

    private Cluster cluster;

    private Bucket bucket;

    private final RecordBuilderFactory builderFactory;

    private transient Schema schema;

    private List<Record> recordList;

    public CouchbaseInput(@Option("configuration") final CouchbaseInputConfiguration configuration,
            final CouchbaseService service, final RecordBuilderFactory builderFactory) {
        this.configuration = configuration;
        this.service = service;
        this.builderFactory = builderFactory;
    }

    @PostConstruct
    public void init() {

        String bootStrapNodes = configuration.getDataSet().getDatastore().getBootstrapNodes();
        String bucketName = configuration.getDataSet().getDatastore().getBucket();
        String password = configuration.getDataSet().getDatastore().getPassword();

        CouchbaseEnvironment environment = new DefaultCouchbaseEnvironment.Builder().connectTimeout(20000L).build();
        this.cluster = CouchbaseCluster.create(environment, bootStrapNodes);
        bucket = cluster.openBucket(bucketName, password);
        bucket.bucketManager().createN1qlPrimaryIndex(true, false);

        N1qlQueryResult n1qlQueryResult = bucket.query(N1qlQuery
                .simple("SELECT META(" + bucketName + ").id FROM " + bucketName + " ORDER BY META(" + bucketName + ").id"));
        recordList = n1qlQueryResult.allRows().stream().map(index -> index.value().get("id")).map(Object::toString)
                .map(index -> bucket.get(index)).map(this::createRecord).collect(Collectors.toList());
    }

    @Producer
    public Record next() {
        // this is the method allowing you to go through the dataset associated
        // to the component configuration
        //
        // return null means the dataset has no more data to go through
        // you can use the builderFactory to create a new Record.
        return recordList.isEmpty() ? null : recordList.remove(0);
    }

    @PreDestroy
    public void release() {
        bucket.close();
        cluster.disconnect();

    }

    private Record createRecord(final JsonDocument jsonDocument) {
        JsonObject jsonObject = jsonDocument.content();
        Set<String> labelNames = jsonObject.getNames();

        if (schema == null) {
            final Schema.Builder schemaBuilder = builderFactory.newSchemaBuilder(RECORD);
            labelNames.stream().forEach(name -> addField(schemaBuilder, jsonObject.get(name), name));
            schema = schemaBuilder.build();
        }

        // final Record.Builder recordBuilder = builderFactory.newRecordBuilder(schema);
        final Record.Builder recordBuilder = builderFactory.newRecordBuilder();

        labelNames.stream().forEach(name -> addColumn(recordBuilder, jsonObject.get(name), name));
        return recordBuilder.build();
    }

    private void addField(Schema.Builder schemaBuilder, Object value, String name) {
        final Schema.Entry.Builder entryBuilder = builderFactory.newEntryBuilder();
        entryBuilder.withName(name).withNullable(true);

        // todo: decide how define type if value is null
        if (value == null) {
            LOG.warn("Can't guess data type if value null. Column with null value will be excluded");
        } else if (value instanceof Integer) {
            schemaBuilder.withEntry(entryBuilder.withType(INT).build());
        } else if (value instanceof Long || value instanceof BigInteger) {
            schemaBuilder.withEntry(entryBuilder.withType(LONG).build());
        } else if (value instanceof Byte[]) {
            schemaBuilder.withEntry(entryBuilder.withType(BYTES).build());
        } else if (value instanceof Double) {
            schemaBuilder.withEntry(entryBuilder.withType(DOUBLE).build());
        } else if (value instanceof String || value instanceof JsonObject) {
            schemaBuilder.withEntry(entryBuilder.withType(STRING).build());
        } else if (value instanceof Boolean) {
            schemaBuilder.withEntry(entryBuilder.withType(BOOLEAN).build());
        } else if (value instanceof JsonArray) {
            schemaBuilder.withEntry(entryBuilder.withType(ARRAY).build());
        } else if (value instanceof Date) {
            schemaBuilder.withEntry(entryBuilder.withType(DATETIME).build());
        } else {
            throw new IllegalArgumentException("Unknown Class type " + value.getClass().getSimpleName());
        }
    }

    private void addColumn(Record.Builder recordBuilder, Object value, String name) {
        final Schema.Entry.Builder entryBuilder = builderFactory.newEntryBuilder();
        entryBuilder.withName(name);

        try {
            if (value == null) {
                LOG.warn("Can't guess data type if value null. Column with null value will be excluded");
            } else if (value instanceof Integer) {
                recordBuilder.withInt(entryBuilder.withType(INT).build(), (Integer) value);
            } else if (value instanceof Long || value instanceof BigInteger) {
                recordBuilder.withLong(entryBuilder.withType(LONG).build(), (Long) value);
            } else if (value instanceof Byte[]) {
                recordBuilder.withBytes(entryBuilder.withType(BYTES).build(), (byte[]) value);
            } else if (value instanceof Double) {
                recordBuilder.withDouble(entryBuilder.withType(DOUBLE).build(), (Double) value);
            } else if (value instanceof String || value instanceof JsonObject) {
                recordBuilder.withString(entryBuilder.withType(STRING).build(), (String) value);
            } else if (value instanceof Boolean) {
                recordBuilder.withBoolean(entryBuilder.withType(BOOLEAN).build(), (Boolean) value);
            } else if (value instanceof JsonArray) {
                recordBuilder.withArray(entryBuilder.withType(ARRAY).build(), (List) value);
            } else if (value instanceof Date) {
                recordBuilder.withDateTime(entryBuilder.withType(DATETIME).build(), (ZonedDateTime) value);
            } else {
                LOG.error("Unknown Class type " + value.getClass().getSimpleName());
                throw new IllegalArgumentException("Unknown Class type " + value.getClass().getSimpleName());
            }
        } catch (ClassCastException e) {
            LOG.error("Field " + name + " with value " + value + " can't be converted", e);
        }
    }
}