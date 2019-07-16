package org.talend.components.couchbase;

import com.couchbase.client.core.env.QueryServiceConfig;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.talend.components.couchbase.dataset.CouchbaseDataSet;
import org.talend.components.couchbase.datastore.CouchbaseDataStore;
import org.talend.components.couchbase.service.CouchbaseService;
import org.talend.components.couchbase.service.I18nMessage;
import org.talend.components.couchbase.source.CouchbaseInputConfiguration;
import org.talend.components.couchbase.source.CouchbaseInputMapper;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.input.Mapper;
import org.talend.sdk.component.runtime.manager.chain.Job;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;


@WithComponents("org.talend.components.couchbase")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MainTest extends CouchbaseUtilTest{

    @Injected
    private BaseComponentsHandler componentsHandler;

    @Test
    @DisplayName("Check input data")
    void couchbaseInputDataTest() {
        componentsHandler.resetState();
        insert1000JsonObjectsToDB();

        Mapper mapper = componentsHandler.asManager().findMapper("Couchbase", "Input", 1, configurationByExample(getInputConfiguration())).get();

        final List<Record> res = componentsHandler.collect(Record.class, mapper, 2000, 4).collect(Collectors.toList());

        assertNotNull(res);
        assertEquals(1000, res.size());
    }

    private CouchbaseInputConfiguration getInputConfiguration() {
        CouchbaseDataStore couchbaseDataStore = new CouchbaseDataStore();
        couchbaseDataStore.setBootstrapNodes(COUCHBASE_CONTAINER.getContainerIpAddress());
        couchbaseDataStore.setUsername(CLUSTER_USERNAME);
        couchbaseDataStore.setPassword(CLUSTER_PASSWORD);
        couchbaseDataStore.setConnectTimeout(DEFAULT_TIMEOUT_IN_SEC);

        CouchbaseDataSet couchbaseDataSet = new CouchbaseDataSet();
        couchbaseDataSet.setDatastore(couchbaseDataStore);
        couchbaseDataSet.setBucket(BUCKET_NAME);

        CouchbaseInputConfiguration configuration = new CouchbaseInputConfiguration();
        return configuration.setDataSet(couchbaseDataSet);
    }

    private void insert1000JsonObjectsToDB() {
        CouchbaseEnvironment environment = new DefaultCouchbaseEnvironment.Builder()
                .connectTimeout(DEFAULT_TIMEOUT_IN_SEC * 1000)
                .queryServiceConfig(QueryServiceConfig.create(10,200))
                .build();
        Cluster cluster = CouchbaseCluster.create(environment, COUCHBASE_CONTAINER.getContainerIpAddress());
        Bucket bucket = cluster.openBucket(BUCKET_NAME, BUCKET_PASSWORD);

        bucket.bucketManager().createN1qlPrimaryIndex(true, false);

        bucket.bucketManager().flush();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<JsonObject> jsonObjects = super.create1000JsonObjects();

        for (int i=0; i<jsonObjects.size(); i++){
            bucket.insert(JsonDocument.create("id_" + String.valueOf(i), jsonObjects.get(i)));
        }

        // Wait while data is writing (Jenkins fix)
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        bucket.close();
        cluster.disconnect();
        environment.shutdown();
    }
}
