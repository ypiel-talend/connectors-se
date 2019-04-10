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

package org.talend.components.couchbase.output;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.talend.components.couchbase.CouchbaseContainerTest;
import org.talend.components.couchbase.dataset.CouchbaseDataSet;
import org.talend.components.couchbase.datastore.CouchbaseDataStore;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.manager.chain.Job;
import org.talend.sdk.component.runtime.record.SchemaImpl;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

@WithComponents("org.talend.components.couchbase")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Testing of CouchbaseOutput component")
public class CouchbaseOutputTest extends CouchbaseContainerTest {

    @Injected
    private BaseComponentsHandler componentsHandler;

    @Service
    private RecordBuilderFactory recordBuilderFactory;

    private List<Record> records;

    private static final ZonedDateTime ZONED_DATE_TIME = ZonedDateTime.of(2018, 10, 30, 10, 30, 59, 0, ZoneId.of("UTC"));

    private List<JsonDocument> retrieveDataFromDatabase() {
        CouchbaseEnvironment environment = new DefaultCouchbaseEnvironment.Builder().connectTimeout(20000L).build();
        Cluster cluster = null;
        Bucket bucket = null;
        List<JsonDocument> resultList = new ArrayList<>();

        try {
            cluster = CouchbaseCluster.create(environment, COUCHBASE_CONTAINER.getContainerIpAddress());
            bucket = cluster.openBucket(BUCKET_NAME, BUCKET_PASSWORD);

            resultList.add(bucket.get("RRRR1"));
            resultList.add(bucket.get("RRRR2"));

        } finally {
            if (bucket != null) {
                bucket.close();
            }
            if (cluster != null) {
                cluster.disconnect();
            }
        }
        return resultList;
    }

    private void prepareRecords() {
        final Schema.Entry.Builder entryBuilder = recordBuilderFactory.newEntryBuilder();
        List<String> listTestData = new ArrayList<>();
        listTestData.add("one");
        listTestData.add("two");
        listTestData.add("three");

        records = new ArrayList<>();

        SchemaImpl schema = new SchemaImpl();
        schema.setType(Schema.Type.STRING);

        Record record1 = recordBuilderFactory.newRecordBuilder()
                .withString(entryBuilder.withName("t_string").withType(Schema.Type.STRING).build(), "RRRR1")
                .withInt(entryBuilder.withName("t_int").withType(Schema.Type.INT).build(), 11111)
                .withLong(entryBuilder.withName("t_long").withType(Schema.Type.LONG).build(), 1_000_000_000_000L)
                // .withBytes(entryBuilder.withName("t_bytes").withType(Schema.Type.BYTES).build(), "test1".getBytes())
                .withFloat(entryBuilder.withName("t_float").withType(Schema.Type.FLOAT).build(), 1000.0f)
                .withDouble(entryBuilder.withName("t_double").withType(Schema.Type.DOUBLE).build(), 1.5)
                .withBoolean(entryBuilder.withName("t_boolean").withType(Schema.Type.BOOLEAN).build(), false)
                .withDateTime(entryBuilder.withName("t_datetime").withType(Schema.Type.DATETIME).build(), ZONED_DATE_TIME)
                .withArray(entryBuilder.withName("t_array").withType(Schema.Type.ARRAY).withElementSchema(schema).build(),
                        listTestData)
                .build();
        Record record2 = recordBuilderFactory.newRecordBuilder()
                .withString(entryBuilder.withName("t_string").withType(Schema.Type.STRING).build(), "RRRR2")
                .withInt(entryBuilder.withName("t_int").withType(Schema.Type.INT).build(), 22222)
                .withLong(entryBuilder.withName("t_long").withType(Schema.Type.LONG).build(), 2_000_000_000_000L)
                // .withBytes(entryBuilder.withName("t_bytes").withType(Schema.Type.BYTES).build(), "test2".getBytes())
                .withFloat(entryBuilder.withName("t_float").withType(Schema.Type.FLOAT).build(), 2000.0f)
                .withDouble(entryBuilder.withName("t_double").withType(Schema.Type.DOUBLE).build(), 2.5)
                .withBoolean(entryBuilder.withName("t_boolean").withType(Schema.Type.BOOLEAN).build(), true)
                .withDateTime(entryBuilder.withName("t_datetime").withType(Schema.Type.DATETIME).build(), ZONED_DATE_TIME)
                .withArray(entryBuilder.withName("t_array").withType(Schema.Type.ARRAY).withElementSchema(schema).build(),
                        listTestData)
                .build();
        records.add(record1);
        records.add(record2);
        componentsHandler.setInputData(records);
    }

    @Test
    @DisplayName("Check amount of total records from retrieved data")
    void sizeOfRetrievedCouchbaseInsertTest() {
        prepareRecords();
        final String outputConfig = configurationByExample().forInstance(getOutputConfiguration()).configured().toQueryString();

        Job.components().component("Couchbase_Output", "Couchbase://Output?" + outputConfig)
                .component("emitter", "test://emitter").connections().from("emitter").to("Couchbase_Output").build().run(); //
        assertEquals(2, retrieveDataFromDatabase().size());
    }

    @Test
    @DisplayName("Check fields from retrieved data")
    void checkDataCouchbaseInsertTest() {
        prepareRecords();
        final String outputConfig = configurationByExample().forInstance(getOutputConfiguration()).configured().toQueryString();

        Job.components().component("Couchbase_Output", "Couchbase://Output?" + outputConfig)
                .component("emitter", "test://emitter").connections().from("emitter").to("Couchbase_Output").build().run();

        List<JsonDocument> resultList = retrieveDataFromDatabase();

        List<String> listTestData = new ArrayList<>();
        listTestData.add("one");
        listTestData.add("two");
        listTestData.add("three");

        assertEquals("RRRR1", resultList.get(0).content().getString("t_string"));
        assertEquals(new Integer(11111), resultList.get(0).content().getInt("t_int"));
        assertEquals(new Long(1_000_000_000_000L), resultList.get(0).content().getLong("t_long"));
        // assertEquals(new Byte[], resultList.get(0).content().getString("t_bytes"));
        assertEquals(new Double(1000.0f), resultList.get(0).content().getDouble("t_float"));
        assertEquals(new Double(1.5), resultList.get(0).content().getDouble("t_double"));
        assertEquals(new Boolean(false), resultList.get(0).content().getBoolean("t_boolean"));
        assertEquals(ZONED_DATE_TIME.toString(), resultList.get(0).content().getString("t_datetime"));
        assertArrayEquals(listTestData.toArray(), resultList.get(0).content().getArray("t_array").toList().toArray());

        assertEquals("RRRR2", resultList.get(1).content().getString("t_string"));
        assertEquals(new Integer(22222), resultList.get(1).content().getInt("t_int"));
        assertEquals(new Long(2_000_000_000_000L), resultList.get(1).content().getLong("t_long"));
        // assertEquals(new Byte[], resultList.get(0).content().getString("t_bytes"));
        assertEquals(new Double(2000.0f), resultList.get(1).content().getDouble("t_float"));
        assertEquals(new Double(2.5), resultList.get(1).content().getDouble("t_double"));
        assertEquals(new Boolean(true), resultList.get(1).content().getBoolean("t_boolean"));
        assertEquals(ZONED_DATE_TIME.toString(), resultList.get(1).content().getString("t_datetime"));
        assertArrayEquals(listTestData.toArray(), resultList.get(1).content().getArray("t_array").toList().toArray());
    }

    private CouchbaseOutputConfiguration getOutputConfiguration() {
        CouchbaseDataStore couchbaseDataStore = new CouchbaseDataStore();
        couchbaseDataStore.setBootstrapNodes(COUCHBASE_CONTAINER.getContainerIpAddress());
        couchbaseDataStore.setBucket(BUCKET_NAME);
        couchbaseDataStore.setPassword(BUCKET_PASSWORD);

        CouchbaseDataSet couchbaseDataSet = new CouchbaseDataSet();
        couchbaseDataSet.setDatastore(couchbaseDataStore);

        CouchbaseOutputConfiguration configuration = new CouchbaseOutputConfiguration();
        configuration.setIdFieldName("t_string");
        configuration.setDataSet(couchbaseDataSet);
        return configuration;
    }
}