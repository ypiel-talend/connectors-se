package org.talend.components.couchbase.source;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.talend.components.couchbase.CouchbaseContainerTest;
import org.talend.components.couchbase.dataset.CouchbaseDataSet;
import org.talend.components.couchbase.datastore.CouchbaseDataStore;
import org.talend.components.couchbase.output.CouchbaseOutputConfiguration;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.manager.chain.Job;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

@WithComponents("org.talend.components.couchbase")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
// @Environment(ContextualEnvironment.class)
public class CouchbaseInputSourceTest extends CouchbaseContainerTest {

    @Injected
    private BaseComponentsHandler componentsHandler;

    @Service
    private RecordBuilderFactory recordBuilderFactory;

    private List<Record> records;

    void prepareRecordData() {
        final Schema.Entry.Builder entryBuilder = recordBuilderFactory.newEntryBuilder();

        records = new ArrayList<>();
        Record record = recordBuilderFactory.newRecordBuilder()
                .withString(entryBuilder.withName("t_string").withType(Schema.Type.STRING).build(), "data1")
                .withInt(entryBuilder.withName("t_int").withType(Schema.Type.INT).build(), 111)
                .withDouble(entryBuilder.withName("t_double").withType(Schema.Type.DOUBLE).build(), 2.5).build();
        Record record2 = recordBuilderFactory.newRecordBuilder()
                .withString(entryBuilder.withName("t_string").withType(Schema.Type.STRING).build(), "data2")
                .withInt(entryBuilder.withName("t_int").withType(Schema.Type.INT).build(), 222)
                .withDouble(entryBuilder.withName("t_double").withType(Schema.Type.DOUBLE).build(), 1000.0).build();

        records.add(record);
        records.add(record2);
        componentsHandler.setInputData(records);
    }

    @Test
    void couchbaseInputTest() {
        prepareRecordData();
        final String outputConfig = configurationByExample().forInstance(getOutputConfiguration()).configured().toQueryString();

        Job.components().component("Couchbase_Output", "Couchbase://Output?" + outputConfig)
                .component("emitter", "test://emitter").connections().from("emitter").to("Couchbase_Output").build().run();

        final String inputConfig = configurationByExample().forInstance(getInputConfiguration()).configured().toQueryString();
        Job.components().component("Couchbase_Input", "Couchbase://Input?" + inputConfig)
                .component("collector", "test://collector").connections().from("Couchbase_Input").to("collector").build().run();

        final List<Record> res = componentsHandler.getCollectedData(Record.class);
        assertEquals(2, res.size());
    }

    @Test
    void couchbaseInputDataTest() {
        prepareRecordData();
        final String outputConfig = configurationByExample().forInstance(getOutputConfiguration()).configured().toQueryString();

        Job.components().component("Couchbase_Output", "Couchbase://Output?" + outputConfig)
                .component("emitter", "test://emitter").connections().from("emitter").to("Couchbase_Output").build().run();

        final String inputConfig = configurationByExample().forInstance(getInputConfiguration()).configured().toQueryString();
        Job.components().component("Couchbase_Input", "Couchbase://Input?" + inputConfig)
                .component("collector", "test://collector").connections().from("Couchbase_Input").to("collector").build().run();

        final List<Record> res = componentsHandler.getCollectedData(Record.class);
        assertEquals("data1", res.get(0).getString("t_string"));
        assertEquals(111, res.get(0).getInt("t_int"));
        assertEquals(2.5, res.get(0).getDouble("t_double"));

        assertEquals("data2", res.get(1).getString("t_string"));
        assertEquals(222, res.get(1).getInt("t_int"));
        assertEquals(1000.0, res.get(1).getDouble("t_double"));
    }

    private CouchbaseInputMapperConfiguration getInputConfiguration() {
        CouchbaseDataStore couchbaseDataStore = new CouchbaseDataStore();
        couchbaseDataStore.setBootstrapNodes(COUCHBASE_CONTAINER.getContainerIpAddress());
        couchbaseDataStore.setBucket(BUCKET_NAME);
        couchbaseDataStore.setPassword(BUCKET_PASSWORD);

        CouchbaseDataSet couchbaseDataSet = new CouchbaseDataSet();
        couchbaseDataSet.setDatastore(couchbaseDataStore);

        CouchbaseInputMapperConfiguration configuration = new CouchbaseInputMapperConfiguration();
        return configuration.setDataSet(couchbaseDataSet);
    }

    private CouchbaseOutputConfiguration getOutputConfiguration() {
        CouchbaseDataStore couchbaseDataStore = new CouchbaseDataStore();
        couchbaseDataStore.setBootstrapNodes(COUCHBASE_CONTAINER.getContainerIpAddress());
        couchbaseDataStore.setBucket(BUCKET_NAME);
        couchbaseDataStore.setPassword(BUCKET_PASSWORD);

        CouchbaseDataSet couchbaseDataSet = new CouchbaseDataSet();
        couchbaseDataSet.setDatastore(couchbaseDataStore);

        CouchbaseOutputConfiguration configuration = new CouchbaseOutputConfiguration();
        configuration.setIdFieldName("t_string");
        configuration.setDataSet(couchbaseDataSet);
        return configuration;
    }
}
