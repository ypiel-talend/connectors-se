/*
 * Copyright (C) 2006-2021 Talend Inc. - www.talend.com
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

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.talend.components.couchbase.datastore.CouchbaseDataStore;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit5.Injected;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.couchbase.CouchbaseService;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;

public abstract class CouchbaseUtilTest {

    protected static final String BUCKET_NAME = "student";

    protected static final String BUCKET_PASSWORD = "secret";

    private static final int BUCKET_QUOTA = 100;

    private static final String CLUSTER_USERNAME = "student";

    private static final String CLUSTER_PASSWORD = "secret";

    protected static final String ANALYTICS_BUCKET = "typesBucket";

    protected static final String ANALYTICS_DATASET = "typesDataset";

    private static final List<String> ports =
            Arrays.asList("8091:8091", "8092:8092", "8093:8093", "8094:8094", "8095:8095",
                    "11210:11210");

    private static final CouchbaseContainer COUCHBASE_CONTAINER;

    protected final Cluster couchbaseCluster;

    protected final CouchbaseDataStore couchbaseDataStore;

    @Injected
    protected BaseComponentsHandler componentsHandler;

    @Service
    protected RecordBuilderFactory recordBuilderFactory;

    static {
    	BucketDefinition bucketDefinition = new BucketDefinition(BUCKET_NAME);
    	COUCHBASE_CONTAINER = new AnalyticsCouchbaseContainer(
                System.getenv("TESTCONTAINERS_HUB_IMAGE_NAME_PREFIX") + "couchbase/server:5.5.1")
    			.withBucket(bucketDefinition)
    			.withCredentials(BUCKET_NAME, BUCKET_PASSWORD);
        /*COUCHBASE_CONTAINER = new AnalyticsCouchbaseContainer(
                System.getenv("TESTCONTAINERS_HUB_IMAGE_NAME_PREFIX") + "couchbase/server:5.5.1")
                        .withClusterAdmin(CLUSTER_USERNAME, CLUSTER_PASSWORD)
                        .withNewBucket(DefaultBucketSettings.builder()
                                .enableFlush(true)
                                .name(BUCKET_NAME)
                                .password(BUCKET_PASSWORD)
                                .quota(BUCKET_QUOTA)
                                .type(BucketType.COUCHBASE)
                                .build());*/
        COUCHBASE_CONTAINER.setPortBindings(ports);
        COUCHBASE_CONTAINER.start();
        
    }

    public CouchbaseUtilTest() {
        couchbaseDataStore = new CouchbaseDataStore();
        couchbaseDataStore.setBootstrapNodes(COUCHBASE_CONTAINER.getContainerIpAddress());
        couchbaseDataStore.setUsername(CLUSTER_USERNAME);
        couchbaseDataStore.setPassword(CLUSTER_PASSWORD);

        couchbaseCluster = Cluster.connect(
        		COUCHBASE_CONTAINER.getConnectionString(),
        		ClusterOptions.clusterOptions(COUCHBASE_CONTAINER.getUsername(), COUCHBASE_CONTAINER.getPassword())
        	);
        		
    }

    protected String generateDocId(String prefix, int number) {
        return prefix + "_" + number;
    }

    private static class AnalyticsCouchbaseContainer extends CouchbaseContainer {

        public AnalyticsCouchbaseContainer(String name) {
            super(name);
        }
        
        public Set<CouchbaseService> enabledServices = EnumSet.of(
                CouchbaseService.KV,
                CouchbaseService.QUERY,
                CouchbaseService.SEARCH,
                CouchbaseService.INDEX,
                CouchbaseService.ANALYTICS
            );
        
    }
}
