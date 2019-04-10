package org.talend.components.couchbase.testutils;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.talend.components.couchbase.datastore.CouchbaseDataStore;
import org.talend.components.couchbase.service.CouchbaseService;

public class Delete {

    @Disabled
    @Test
    void testConn() {
        CouchbaseDataStore couchbaseDataStore = new CouchbaseDataStore();
        couchbaseDataStore.setBootstrapNodes("172.20.17.139");
        couchbaseDataStore.setBucket("test");
        couchbaseDataStore.setPassword("123456");

        String bootStrapNodes = "172.20.17.139";
        String bucketName = "test";
        String password = "123456";

        CouchbaseService couchbaseService = new CouchbaseService();
        System.out.println(couchbaseService.healthCheck(couchbaseDataStore));

        CouchbaseEnvironment environment = new DefaultCouchbaseEnvironment.Builder().connectTimeout(20000L).build();
        Cluster cluster = CouchbaseCluster.create(environment, bootStrapNodes);
        Bucket bucket = cluster.openBucket(bucketName, password);

        JsonDocument jsonDocument = bucket.get("3");
        // System.out.println(jsonDocument);
        System.out.println(jsonDocument.content().toString());

        N1qlQueryResult n1qlQueryResult = bucket.query(N1qlQuery
                .simple("SELECT META(" + bucketName + ").id FROM " + bucketName + " ORDER BY META(" + bucketName + ").id"));
        n1qlQueryResult.allRows().stream().map(index -> index.value().get("id"))
                .forEach(index -> System.out.println(index.toString()));

    }
}
