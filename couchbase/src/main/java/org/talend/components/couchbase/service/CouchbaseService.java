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
import com.couchbase.client.java.auth.Authenticator;
import com.couchbase.client.java.auth.PasswordAuthenticator;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.couchbase.datastore.CouchbaseDataStore;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

import lombok.extern.slf4j.Slf4j;

@Version(1)
@Slf4j
@Service
public class CouchbaseService {

    private transient static final Logger LOG = LoggerFactory.getLogger(CouchbaseService.class);

    @Service
    private CouchbaseDataStore couchBaseConnection;

    // @Service
    // private I18nMessage i18n;

    public static String[] resolveAddresses(String nodes) {
        String[] addresses = nodes.replaceAll(" ", "").split(",");
        for (int i = 0; i < addresses.length; i++) {
            log.info("Bootstrap node[" + i + "]: " + addresses[i]);
        }
        return addresses;
    }

    @HealthCheck("healthCheck")
    public HealthCheckStatus healthCheck(@Option("configuration.dataset.connection") final CouchbaseDataStore datastore) {
        CouchbaseEnvironment environment = null;
        Cluster cluster = null;
        Bucket bucket = null;
        try {
            String bootstrapNodes = datastore.getBootstrapNodes();
            String bucketName = datastore.getBucket();
            String password = datastore.getPassword();

            String[] urls = resolveAddresses(datastore.getBootstrapNodes());

            environment = new DefaultCouchbaseEnvironment.Builder()
                    // .bootstrapHttpDirectPort(port)
                    .connectTimeout(20000L).build();
            Authenticator authenticator = new PasswordAuthenticator(bucketName, password);
            cluster = CouchbaseCluster.create(environment, bootstrapNodes);
            cluster.authenticate(authenticator);
            bucket = cluster.openBucket(bucketName);

            // DiagnosticsReport report = cluster.diagnostics();
            // List<EndpointHealth> endpointHealths = report.endpoints();
            //
            // for (EndpointHealth health : endpointHealths) {
            // if (!health.state().equals(LifecycleState.CONNECTED)) {
            // return new HealthCheckStatus(HealthCheckStatus.Status.KO,
            // "Endpoint with id: " + health.id() + " Not connected");
            // }
            // }
        } catch (Throwable exception) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, exception.getMessage());
        } finally {
            closeConnection(environment, cluster, bucket);
        }
        return new HealthCheckStatus(HealthCheckStatus.Status.OK, "Connection OK");
        // todo: add i18n
    }

    public static void closeConnection(CouchbaseEnvironment environment, Cluster cluster, Bucket bucket) {
        if (bucket != null) {
            bucket.close();
        }
        if (cluster != null) {
            cluster.disconnect();
        }
        if (environment != null) {
            environment.shutdown();
        }
    }
}