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

package org.talend.components.couchbase;

import com.couchbase.client.java.bucket.BucketType;
import com.couchbase.client.java.cluster.DefaultBucketSettings;
import org.junit.jupiter.api.extension.Extension;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.couchbase.CouchbaseContainer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CouchbaseContainerTest implements Extension {

    public static final String BUCKET_NAME = "student";

    public static final String BUCKET_PASSWORD = "secret";

    public static final int BUCKET_QUOTA = 100;

    public static final String CLUSTER_USERNAME = "student";

    public static final String CLUSTER_PASSWORD = "secret";

    private static final List<String> ports = new ArrayList(
            Arrays.asList(new String[] { "8091:8091", "8092:8092", "8093:8093", "8094:8094", "11210:11210" }));

    public static final CouchbaseContainer COUCHBASE_CONTAINER;

    static {
        COUCHBASE_CONTAINER = new CouchbaseContainer().withClusterAdmin(CLUSTER_USERNAME, CLUSTER_PASSWORD)
                .withNewBucket(DefaultBucketSettings.builder().enableFlush(true).name(BUCKET_NAME).password(BUCKET_PASSWORD)
                        .quota(BUCKET_QUOTA).type(BucketType.COUCHBASE).build());
        COUCHBASE_CONTAINER.setPortBindings(ports);
        COUCHBASE_CONTAINER.start();
    }
}
