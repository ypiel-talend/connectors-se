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

package org.talend.components.couchbase.service;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import org.talend.components.couchbase.datastore.CouchbaseDataStore;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import java.util.Date;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import static org.talend.sdk.component.api.record.Schema.Type.*;
import static org.talend.sdk.component.api.record.Schema.Type.STRING;

@Version(1)
@Slf4j
@Service
public class CouchbaseService {

    private CouchbaseEnvironment environment;

    private Cluster cluster;

    private Bucket bucket;

    @Service
    private CouchbaseDataStore couchBaseConnection;

    @Service
    private I18nMessage i18n;

    @Service
    private RecordBuilderFactory builderFactory;

    public String[] resolveAddresses(String nodes) {
        String[] addresses = nodes.replaceAll(" ", "").split(",");
        for (int i = 0; i < addresses.length; i++) {
            log.info(i18n.bootstrapNodes(i, addresses[i]));
        }
        return addresses;
    }

    public Bucket openConnection(CouchbaseDataStore dataStore) throws Exception {
        String bootStrapNodes = dataStore.getBootstrapNodes();
        String bucketName = dataStore.getBucket();
        String password = dataStore.getPassword();
        int connectTimeout = dataStore.getConnectTimeout() * 1000; // convert to sec

        String[] urls = resolveAddresses(bootStrapNodes);

        this.environment = new DefaultCouchbaseEnvironment.Builder().connectTimeout(connectTimeout).build();
        this.cluster = CouchbaseCluster.create(environment, urls);
        this.bucket = this.cluster.openBucket(bucketName, password);
        return bucket;
    }

    @HealthCheck("healthCheck")
    public HealthCheckStatus healthCheck(@Option("configuration.dataset.connection") final CouchbaseDataStore datastore) {
        try {
            bucket = openConnection(datastore);
            return new HealthCheckStatus(HealthCheckStatus.Status.OK, "Connection OK");
        } catch (Exception exception) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, exception.getMessage());
        } finally {
            closeConnection();
        }
    }

    public void closeConnection() {
        if (bucket != null) {
            if (bucket.close()) {
                log.debug(i18n.bucketWasClosed(bucket.name()));
            } else {
                log.debug(i18n.cannotCloseBucket(bucket.name()));
            }
            bucket = null;
        }
        if (cluster != null) {
            if (cluster.disconnect()) {
                log.debug(i18n.clusterWasClosed());
            } else {
                log.debug(i18n.cannotCloseCluster());
            }
            cluster = null;
        }
        if (environment != null) {
            if (environment.shutdown()) {
                log.debug(i18n.couchbaseEnvWasClosed());
            } else {
                log.debug(i18n.cannotCloseCouchbaseEnv());
            }
            environment = null;
        }
    }

    public Schema getSchema(JsonObject jsonObject) {
        Schema.Builder schemaBuilder = builderFactory.newSchemaBuilder(RECORD);

        Set<String> jsonKeys = jsonObject.getNames();

        for (String key : jsonKeys) {
            // receive value from JSON
            Object value = jsonObject.get(key);

            // With this value we can define type
            Schema.Type type = defineValueType(value);

            // We can add to schema builder entry
            Schema.Entry.Builder entryBuilder = builderFactory.newEntryBuilder();
            entryBuilder.withNullable(true).withName(key).withType(type);

            if (type == RECORD) {
                entryBuilder.withElementSchema(getSchema((JsonObject) value));
            } else if (type == ARRAY) {
                entryBuilder.withElementSchema(defineSchemaForArray((JsonArray) value));
            }
            Schema.Entry currentEntry = entryBuilder.build();
            schemaBuilder.withEntry(currentEntry);
        }
        return schemaBuilder.build();
    }

    private Schema defineSchemaForArray(JsonArray jsonArray) {
        Object firstValueInArray = jsonArray.get(0);
        Schema.Builder schemaBuilder = builderFactory.newSchemaBuilder(RECORD);
        if (firstValueInArray == null) {
            throw new IllegalArgumentException("First value of Array is null. Can't define type of values in array");
        }
        Schema.Type type = defineValueType(firstValueInArray);
        schemaBuilder.withType(type);
        if (type == RECORD) {
            schemaBuilder.withEntry(
                    builderFactory.newEntryBuilder().withElementSchema(getSchema((JsonObject) firstValueInArray)).build());
        } else if (type == ARRAY) {
            schemaBuilder.withEntry(builderFactory.newEntryBuilder()
                    .withElementSchema(defineSchemaForArray((JsonArray) firstValueInArray)).build());
        }
        return schemaBuilder.withType(type).build();
    }

    private Schema.Type defineValueType(Object value) {
        if (value instanceof String) {
            return STRING;
        } else if (value instanceof Boolean) {
            return BOOLEAN;
        } else if (value instanceof Date) { // TODO: Not sure if Date can come from Couchbase
            return DATETIME;
        } else if (value instanceof Double) {
            return DOUBLE;
        } else if (value instanceof Integer) {
            return INT;
        } else if (value instanceof Long) {
            return LONG;
        } else if (value instanceof Byte[]) {
            throw new IllegalArgumentException("BYTES is unsupported");
        } else if (value instanceof JsonArray) {
            return ARRAY;
        } else if (value instanceof JsonObject) {
            return RECORD;
        } else if (value instanceof Float) {
            return FLOAT;
        } else {
            return STRING;
        }
    }
}