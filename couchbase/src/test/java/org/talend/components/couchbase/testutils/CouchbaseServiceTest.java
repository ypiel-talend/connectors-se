package org.talend.components.couchbase.testutils;

import com.couchbase.client.java.bucket.BucketType;
import com.couchbase.client.java.cluster.DefaultBucketSettings;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.talend.components.couchbase.datastore.CouchbaseDataStore;
import org.talend.components.couchbase.service.CouchbaseService;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.junit5.WithComponents;
import org.testcontainers.couchbase.CouchbaseContainer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WithComponents("org.talend.components.couchbase")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CouchbaseServiceTest {// implements ExtensionContext.Store.CloseableResource, BeforeAllCallback, ParameterResolver {

    private static final String BUCKET_NAME = "student";

    private static final String BUCKET_PASSWORD = "secret";

    private static final int BUCKET_QUOTA = 100;

    private static final String CLUSTER_USERNAME = "student";

    private static final String CLUSTER_PASSWORD = "secret";

    private static boolean started = false;

    private static List ports = new ArrayList(Arrays.asList(new String [] {"8091:8091", "18091:18091"}));

    private static String url;

    private static final CouchbaseContainer COUCHBASE_CONTAINER = new CouchbaseContainer()
            .withClusterAdmin(CLUSTER_USERNAME, CLUSTER_PASSWORD).withNewBucket(DefaultBucketSettings.builder().enableFlush(true)
                    .name(BUCKET_NAME).password(BUCKET_PASSWORD).quota(BUCKET_QUOTA).type(BucketType.COUCHBASE).build()).withStartupTimeout(Duration.ofSeconds(120));

    static int bootstrapport;

    @BeforeClass
    public static void beforeAll() {
        COUCHBASE_CONTAINER.setPortBindings(ports);
        COUCHBASE_CONTAINER.start();
        url = COUCHBASE_CONTAINER.getContainerIpAddress();
    }

    @Test
    public void couchbaseSuccessfulConnectionTest() {
        CouchbaseDataStore couchbaseDataStore = new CouchbaseDataStore();
        couchbaseDataStore.setBootstrapNodes(url);
        couchbaseDataStore.setBucket(BUCKET_NAME);
        couchbaseDataStore.setPassword(BUCKET_PASSWORD);

        CouchbaseService couchbaseService = new CouchbaseService();
        assertEquals(new HealthCheckStatus(HealthCheckStatus.Status.OK, "Connection OK"),
                couchbaseService.healthCheck(couchbaseDataStore));
    }

    @Test
    public void couchbaseNotSuccessfulConnectionTest() {
        String wrongPassword = "wrongpass";
        String expectedComment = "Passwords for bucket \"student\" do not match.";

        CouchbaseDataStore couchbaseDataStore = new CouchbaseDataStore();
        couchbaseDataStore.setBootstrapNodes(url);
        couchbaseDataStore.setBucket(BUCKET_NAME);
        couchbaseDataStore.setPassword(wrongPassword);

        CouchbaseService couchbaseService = new CouchbaseService();
        assertEquals(new HealthCheckStatus(HealthCheckStatus.Status.KO, expectedComment),
                couchbaseService.healthCheck(couchbaseDataStore));
    }


    @AfterClass
    public static void close() {
        COUCHBASE_CONTAINER.stop();
    }

    // @Override
    // public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
    // throws ParameterResolutionException {
    // return false;
    // }
    //
    // @Override
    // public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
    // throws ParameterResolutionException {
    // return null;
    // }
}
