package org.talend.components.couchbase.service;

import com.couchbase.client.core.message.internal.DiagnosticsReport;
import com.couchbase.client.core.message.internal.EndpointHealth;
import com.couchbase.client.core.state.LifecycleState;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.auth.Authenticator;
import com.couchbase.client.java.auth.PasswordAuthenticator;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.couchbase.datastore.CouchbaseDataStore;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

import java.util.List;

@Version(1)
@Slf4j
@Service
public class CouchbaseService {

    @Service
    private CouchbaseDataStore couchBaseConnection;

    private transient static final Logger LOG = LoggerFactory.getLogger(CouchbaseService.class);

    // @Service
    // private I18nMessage i18n;

    // you can put logic here you can reuse in components
    @HealthCheck("healthCheck")
    public HealthCheckStatus healthCheck(@Option("configuration.dataset.connection") final CouchbaseDataStore datastore) {
        Cluster cluster = null;
        Bucket bucket = null;
        try {
            String bootstrapNodes = datastore.getBootstrapNodes();
            String bucketName = datastore.getBucket();
            String password = datastore.getPassword();

            String[] urls = resolveAddresses(datastore.getBootstrapNodes());

            CouchbaseEnvironment environment = new DefaultCouchbaseEnvironment.Builder()
                    //.bootstrapHttpDirectPort(port)
                    .connectTimeout(20000L)
                    .build();
            Authenticator authenticator = new PasswordAuthenticator(bucketName, password);
            cluster = CouchbaseCluster.create(environment, bootstrapNodes);
            cluster.authenticate(authenticator);
            bucket = cluster.openBucket(bucketName);


//            DiagnosticsReport report = cluster.diagnostics();
//            List<EndpointHealth> endpointHealths = report.endpoints();
//
//            for (EndpointHealth health : endpointHealths) {
//                if (!health.state().equals(LifecycleState.CONNECTED)) {
//                    return new HealthCheckStatus(HealthCheckStatus.Status.KO,
//                            "Endpoint with id: " + health.id() + " Not connected");
//                }
//            }
        } catch (Throwable exception) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, exception.getMessage());
        } finally {
            if (bucket != null) {
                bucket.close();
            }
            if (cluster != null) {
                cluster.disconnect();
            }
        }
        return new HealthCheckStatus(HealthCheckStatus.Status.OK, "Connection OK");
        // todo: add i18n
    }

    static String[] resolveAddresses(String nodes) {
        String[] addresses = nodes.split(",");
        for (int i = 0; i < addresses.length; i++) {
            log.info("Bootstrap node[" + i + "]: " + addresses[i]);
        }
        return addresses;
    }
}