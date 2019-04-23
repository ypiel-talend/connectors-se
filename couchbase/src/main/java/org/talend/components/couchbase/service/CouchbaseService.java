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
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import org.talend.components.couchbase.datastore.CouchbaseDataStore;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

import lombok.extern.slf4j.Slf4j;

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
}