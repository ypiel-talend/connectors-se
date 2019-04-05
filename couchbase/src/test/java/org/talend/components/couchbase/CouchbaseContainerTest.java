package org.talend.components.couchbase;

import com.couchbase.client.java.bucket.BucketType;
import com.couchbase.client.java.cluster.DefaultBucketSettings;
import org.testcontainers.couchbase.CouchbaseContainer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CouchbaseContainerTest {

    public static final String BUCKET_NAME = "student";

    public static final String BUCKET_PASSWORD = "secret";

    public static final int BUCKET_QUOTA = 100;

    public static final String CLUSTER_USERNAME = "student";

    public static final String CLUSTER_PASSWORD = "secret";

    private static final List ports = new ArrayList(Arrays.asList(new String[] { "8091:8091", "18091:18091" }));

    public static final CouchbaseContainer COUCHBASE_CONTAINER;

    static {
        COUCHBASE_CONTAINER = new CouchbaseContainer().withClusterAdmin(CLUSTER_USERNAME, CLUSTER_PASSWORD)
                .withNewBucket(DefaultBucketSettings.builder().enableFlush(true).name(BUCKET_NAME).password(BUCKET_PASSWORD)
                        .quota(BUCKET_QUOTA).type(BucketType.COUCHBASE).build())
                .withStartupTimeout(Duration.ofSeconds(120));
        COUCHBASE_CONTAINER.setPortBindings(ports);
        COUCHBASE_CONTAINER.start(); //
    }
}
