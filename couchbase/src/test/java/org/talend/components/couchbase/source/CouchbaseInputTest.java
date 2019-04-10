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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.talend.components.couchbase.CouchbaseContainerTest;
import org.talend.components.couchbase.dataset.CouchbaseDataSet;
import org.talend.components.couchbase.datastore.CouchbaseDataStore;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.manager.chain.Job;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

@WithComponents("org.talend.components.couchbase")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Testing of CouchbaseInput component")
public class CouchbaseInputTest extends CouchbaseContainerTest {

    @Injected
    private BaseComponentsHandler componentsHandler;

    @Service
    private RecordBuilderFactory recordBuilderFactory;

    private List<Record> records;

    private List<String> listTestData;

    private static final ZonedDateTime ZONED_DATE_TIME = ZonedDateTime.of(2018, 10, 30, 10, 30, 59, 0, ZoneId.of("UTC"));

    private void insertTestDataToDB() {
        CouchbaseEnvironment environment = new DefaultCouchbaseEnvironment.Builder().connectTimeout(20000L).build();
        Cluster cluster = null;
        Bucket bucket = null;

        try {
            cluster = CouchbaseCluster.create(environment, COUCHBASE_CONTAINER.getContainerIpAddress());
            bucket = cluster.openBucket(BUCKET_NAME, BUCKET_PASSWORD);

            bucket.bucketManager().flush();

            JsonObject json = JsonObject.create().put("t_string", "RRRR1").put("t_int", 11111).put("t_long", 1_000_000_000_000L)
                    // .put("t_bytes", "test1".getBytes())
                    .put("t_float", 1000.0f).put("t_double", 1.5).put("t_boolean", false)
                    .put("t_datetime", ZONED_DATE_TIME.toString()).put("t_array", JsonArray.from("one", "two", "three"));

            bucket.insert(JsonDocument.create("RRRR1", json));

            JsonObject json2 = JsonObject.create().put("t_string", "RRRR2").put("t_int", 22222).put("t_long", 2_000_000_000_000L)
                    // .put("t_bytes", "test2".getBytes())
                    .put("t_float", 2000.0f).put("t_double", 2.5).put("t_boolean", true)
                    .put("t_datetime", ZONED_DATE_TIME.toString()).put("t_array", JsonArray.from("one", "two", "three"));

            bucket.insert(JsonDocument.create("RRRR2", json2));
        } finally {
            if (bucket != null) {
                bucket.close();
            }
            if (cluster != null) {
                cluster.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Check size of input data")
    void totalNumbersOfRecordsTest() {
        insertTestDataToDB();

        final String inputConfig = configurationByExample().forInstance(getInputConfiguration()).configured().toQueryString();
        Job.components().component("Couchbase_Input", "Couchbase://Input?" + inputConfig)
                .component("collector", "test://collector").connections().from("Couchbase_Input").to("collector").build().run();

        final List<Record> res = componentsHandler.getCollectedData(Record.class);
        assertEquals(2, res.size());
    }

    @Test
    @DisplayName("Check input data")
    void couchbaseInputDataTest() {
        insertTestDataToDB();

        final String inputConfig = configurationByExample().forInstance(getInputConfiguration()).configured().toQueryString();
        Job.components().component("Couchbase_Input", "Couchbase://Input?" + inputConfig)
                .component("collector", "test://collector").connections().from("Couchbase_Input").to("collector").build().run();

        final List<Record> res = componentsHandler.getCollectedData(Record.class);
        // check first record
        assertEquals("RRRR1", res.get(0).getString("t_string"));
        assertEquals(11111, res.get(0).getInt("t_int"));
        assertEquals(1_000_000_000_000L, res.get(0).getLong("t_long"));
        // assertEquals("test1".getBytes(), res.get(0).getString("t_bytes"));
        assertEquals(1000.0F, res.get(0).getFloat("t_float"));
        assertEquals(1.5, res.get(0).getDouble("t_double"));
        assertEquals(false, res.get(0).getBoolean("t_boolean"));
        assertEquals(ZONED_DATE_TIME, res.get(0).getDateTime("t_datetime"));
        assertEquals(listTestData, res.get(0).getArray(List.class, "t_array"));

        assertEquals("RRRR2", res.get(1).getString("t_string"));
        assertEquals(22222, res.get(1).getInt("t_int"));
        assertEquals(2_000_000_000_000L, res.get(1).getLong("t_long"));
        // assertEquals("test2".getBytes(), res.get(1).getString("t_bytes"));
        assertEquals(2000.0F, res.get(1).getFloat("t_float"));
        assertEquals(2.5, res.get(1).getDouble("t_double"));
        assertEquals(true, res.get(1).getBoolean("t_boolean"));
        assertEquals(listTestData, res.get(1).getArray(List.class, "t_array"));
    }

    private CouchbaseInputConfiguration getInputConfiguration() {
        CouchbaseDataStore couchbaseDataStore = new CouchbaseDataStore();
        couchbaseDataStore.setBootstrapNodes(COUCHBASE_CONTAINER.getContainerIpAddress());
        couchbaseDataStore.setBucket(BUCKET_NAME);
        couchbaseDataStore.setPassword(BUCKET_PASSWORD);

        CouchbaseDataSet couchbaseDataSet = new CouchbaseDataSet();
        couchbaseDataSet.setDatastore(couchbaseDataStore);

        CouchbaseInputConfiguration configuration = new CouchbaseInputConfiguration();
        return configuration.setDataSet(couchbaseDataSet);
    }
}
