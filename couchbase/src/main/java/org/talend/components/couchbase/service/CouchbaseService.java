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

import com.couchbase.client.core.CouchbaseException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.couchbase.dataset.CouchbaseDataSet;
import org.talend.components.couchbase.datastore.CouchbaseDataStore;
import org.talend.components.couchbase.source.CouchbaseInput;
import org.talend.components.couchbase.source.CouchbaseInputConfiguration;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.api.service.schema.DiscoverSchema;

import java.util.Date;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import static org.talend.sdk.component.api.record.Schema.Type.*;

@Version(1)
@Slf4j
@Service
public class CouchbaseService {

    private static final transient Logger LOG = LoggerFactory.getLogger(CouchbaseService.class);

    private CouchbaseEnvironment environment;

    private Cluster cluster;

    @Service
    private CouchbaseDataStore couchBaseConnection;

    @Service
    private I18nMessage i18n;

    @Service
    RecordBuilderFactory builderFactory;

    public String[] resolveAddresses(String nodes) {
        String[] addresses = nodes.replaceAll(" ", "").split(",");
        for (int i = 0; i < addresses.length; i++) {
            LOG.info(i18n.bootstrapNodes(i, addresses[i]));
        }
        return addresses;
    }

    public Cluster openConnection(CouchbaseDataStore dataStore) {
        String bootStrapNodes = dataStore.getBootstrapNodes();
        String username = dataStore.getUsername();
        String password = dataStore.getPassword();
        int connectTimeout = dataStore.getConnectTimeout() * 1000; // convert to sec

        String[] urls = resolveAddresses(bootStrapNodes);

        try {
            environment = new DefaultCouchbaseEnvironment.Builder().connectTimeout(connectTimeout).build();
            cluster = CouchbaseCluster.create(environment, urls);
            cluster.authenticate(username, password);
            String clusterName = cluster.clusterManager().info().raw().get("name").toString();
            LOG.debug(i18n.connectedToCluster(clusterName));
        } catch (Exception e) {
            LOG.error(i18n.connectionKO());
            throw new CouchbaseException(e);
        }
        return cluster;
    }

    @HealthCheck("healthCheck")
    public HealthCheckStatus healthCheck(@Option("configuration.dataset.connection") final CouchbaseDataStore datastore) {
        try {
            openConnection(datastore);
            return new HealthCheckStatus(HealthCheckStatus.Status.OK, "Connection OK");
        } catch (Exception exception) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, exception.getMessage());
        } finally {
            closeConnection();
        }
    }

    @DiscoverSchema("discover")
    public Schema addColumns(@Option("dataSet") final CouchbaseDataSet dataSet) {
        CouchbaseInputConfiguration configuration = new CouchbaseInputConfiguration();
        configuration.setDataSet(dataSet);
        CouchbaseInput couchbaseInput = new CouchbaseInput(configuration, this, builderFactory, i18n);
        couchbaseInput.init();
        Record record = couchbaseInput.next();
        couchbaseInput.release();

        return record.getSchema();
    }

    public Schema defineSchema(JsonObject document, Set<String> columnsSet) {
        if (columnsSet.isEmpty()) {
            columnsSet.addAll(document.getNames());
        }
        final Schema.Builder schemaBuilder = builderFactory.newSchemaBuilder(RECORD);
        columnsSet.stream().forEach(name -> addField(schemaBuilder, name, getJsonValue(name, document)));
        return schemaBuilder.build();
    }

    public boolean isResultNeedWrapper(String query) {
        String selectPart = query.substring(0, query.indexOf('*') + 1);
        if (selectPart.trim().replaceAll(" ", "").toLowerCase().equals("select*")) {
            return true;
        }
        return false;
    }

    private void addField(final Schema.Builder schemaBuilder, final String name, Object value) {
        if (value == null) {
            LOG.warn(i18n.cannotGuessWhenDataIsNull());
            return;
        }
        final Schema.Entry.Builder entryBuilder = builderFactory.newEntryBuilder();
        Schema.Type type = getSchemaType(value);
        entryBuilder.withName(name).withNullable(true).withType(type);
        if (type == ARRAY) {
            List<?> listValue = ((JsonArray) value).toList();
            Object listObject = listValue.isEmpty() ? null : listValue.get(0);
            entryBuilder.withElementSchema(builderFactory.newSchemaBuilder(getSchemaType(listObject)).build());
        }
        schemaBuilder.withEntry(entryBuilder.build());
    }

    private Schema.Type getSchemaType(Object value) {
        if (value instanceof String || value instanceof JsonObject) {
            return STRING;
        } else if (value instanceof Boolean) {
            return BOOLEAN;
        } else if (value instanceof Date) {
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
        } else {
            return STRING;
        }
    }

    public Object getJsonValue(String jsonKey, JsonObject json) {
        return json.get(jsonKey);
    }

    public Bucket openBucket(Cluster cluster, String bucketName) {
        Bucket bucket;
        try {
            bucket = cluster.openBucket(bucketName);
        } catch (Exception e) {
            LOG.error(i18n.cannotOpenBucket());
            throw new CouchbaseException(e);
        }
        return bucket;
    }

    public void closeBucket(Bucket bucket) {
        if (bucket != null) {
            if (bucket.close()) {
                LOG.debug(i18n.bucketWasClosed(bucket.name()));
            } else {
                LOG.debug(i18n.cannotCloseBucket(bucket.name()));
            }
            bucket = null;
        }
    }

    public void closeConnection() {
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
}