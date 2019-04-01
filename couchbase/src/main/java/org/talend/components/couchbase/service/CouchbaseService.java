package org.talend.components.couchbase.service;

import com.couchbase.client.core.message.internal.DiagnosticsReport;
import com.couchbase.client.core.message.internal.EndpointHealth;
import com.couchbase.client.core.state.LifecycleState;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.talend.components.couchbase.datastore.CouchbaseDataStore;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

import java.util.List;

@Slf4j
@Service
public class CouchbaseService {

    @Service
    private CouchbaseDataStore couchBaseConnection;

    // @Service
    // private I18nMessage i18n;

    // you can put logic here you can reuse in components
    @HealthCheck("healthCheck")
    public HealthCheckStatus healthCheck(@Option("configuration.dataset.connection") final CouchbaseDataStore datastore) {
        try {
            String[] urls = resolveAddresses(datastore.getBootstrapNodes());

            String bootstrapNodes = datastore.getBootstrapNodes();
            String bucketName = datastore.getBucket();
            String password = datastore.getPassword();

            CouchbaseEnvironment environment = new DefaultCouchbaseEnvironment.Builder().connectTimeout(20000L).build();
            CouchbaseCluster cluster = CouchbaseCluster.create(environment, bootstrapNodes);
            Bucket bucket = cluster.openBucket(bucketName, password);

            DiagnosticsReport report = cluster.diagnostics();
            List<EndpointHealth> endpointHealths = report.endpoints();
            for (EndpointHealth health : endpointHealths) {
                if (!health.state().equals(LifecycleState.CONNECTED)) {
                    return new HealthCheckStatus(HealthCheckStatus.Status.KO,
                            "Endpoint with id: " + health.id() + " Not connected");
                }
            }
        } catch (Throwable exception) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, exception.getMessage());
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