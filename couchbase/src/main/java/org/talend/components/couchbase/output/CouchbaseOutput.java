package org.talend.components.couchbase.output;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.talend.components.couchbase.service.CouchbaseService;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.*;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.util.List;

@Version(1) // default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Slf4j
@Icon(Icon.IconType.STAR) // you can use a custom one using @Icon(value=CUSTOM, custom="filename") and adding
                          // icons/filename_icon32.png in resources
@Processor(name = "CouchbaseOutput")
@Documentation("TODO fill the documentation for this processor")
public class CouchbaseOutput implements Serializable {

    private final CouchbaseOutputConfiguration configuration;

    private final CouchbaseService service;

    private CouchbaseCluster cluster;

    private Bucket bucket;

    private String idFieldName;

    private boolean dieOnError;

    public CouchbaseOutput(@Option("configuration") final CouchbaseOutputConfiguration configuration,
            final CouchbaseService service) {
        this.configuration = configuration;
        this.service = service;
    }

    @PostConstruct
    public void init() {
        // this method will be executed once for the whole component execution,
        // this is where you can establish a connection for instance
        // Note: if you don't need it you can delete it
        String bootstrapNodes = configuration.getDataSet().getDatastore().getBootstrapNodes();
        String bucketName = configuration.getDataSet().getDatastore().getBucket();
        String password = configuration.getDataSet().getDatastore().getPassword();
        idFieldName = configuration.getIdFieldName();
        dieOnError = configuration.isDieOnError();

        CouchbaseEnvironment environment = new DefaultCouchbaseEnvironment.Builder().connectTimeout(20000L).build();
        this.cluster = CouchbaseCluster.create(environment, bootstrapNodes);
        bucket = cluster.openBucket(bucketName, password);
    }

    @BeforeGroup
    public void beforeGroup() {
        // if the environment supports chunking this method is called at the beginning if a chunk
        // it can be used to start a local transaction specific to the backend you use
        // Note: if you don't need it you can delete it
    }

    @ElementListener
    public void onNext(@Input final Record defaultInput) {
        // this is the method allowing you to handle the input(s) and emit the output(s)
        // after some custom logic you put here, to send a value to next element you can use an
        // output parameter and call emit(value).
        bucket.upsert(toJsonDocument(idFieldName, defaultInput));
    }

    @AfterGroup
    public void afterGroup() {
        // symmetric method of the beforeGroup() executed after the chunk processing
        // Note: if you don't need it you can delete it
    }

    @PreDestroy
    public void release() {
        // this is the symmetric method of the init() one,
        // release potential connections you created or data you cached
        // Note: if you don't need it you can delete it
        bucket.close();
        cluster.disconnect();
    }

    private JsonDocument toJsonDocument(String idFieldName, Record record) {
        List<Schema.Entry> entries = record.getSchema().getEntries();
        JsonObject jsonObject = JsonObject.create();
        for (Schema.Entry entry : entries) {
            String entryName = entry.getName();

            Object value = null;

            switch (entry.getType()) {
            case INT:
                value = record.getInt(entryName);
                break;
            case LONG:
                value = record.getLong(entryName);
                break;
            case BYTES:
                value = record.getBytes(entryName);
                break;
            case FLOAT:
                value = record.getFloat(entryName);
                break;
            case DOUBLE:
                value = record.getDouble(entryName);
                break;
            case STRING:
                value = record.getString(entryName);
                break;
            case BOOLEAN:
                value = record.getBoolean(entryName);
                break;
            case ARRAY:
                value = record.getArray(List.class, entryName);
                break;
            case DATETIME:
                value = record.getDateTime(entryName);
                break;
            case RECORD:
                value = record.getRecord(entryName);
                break;
            default:
                value = record.get(Object.class, entryName);
                throw new IllegalArgumentException("Unknown Type " + entry.getType());
            }

            if (entryName.equals(idFieldName)) {
                value = String.valueOf(value);
            }
            jsonObject.put(entryName, value);
        }
        return JsonDocument.create(String.valueOf(jsonObject.get(idFieldName)), jsonObject);
    }
}