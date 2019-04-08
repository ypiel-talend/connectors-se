package org.talend.components.couchbase.source;

import org.junit.jupiter.api.BeforeAll;
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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

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

    private static final ZonedDateTime ZONED_DATE_TIME = ZonedDateTime.of(2018, 10, 30, 10, 30, 59, 0, ZoneId.of("UTC"));

    void madeRecords() {
        final Schema.Entry.Builder entryBuilder = recordBuilderFactory.newEntryBuilder();

        records = new ArrayList<>();
        Record record1 = recordBuilderFactory.newRecordBuilder()
                .withString(entryBuilder.withName("t_string").withType(Schema.Type.STRING).build(), "data1")
                .withInt(entryBuilder.withName("t_int").withType(Schema.Type.INT).build(), 11111)
                .withLong(entryBuilder.withName("t_long").withType(Schema.Type.LONG).build(), 1_000_000_000_000L)
                .withBytes(entryBuilder.withName("t_bytes").withType(Schema.Type.BYTES).build(), "test1".getBytes())
                .withFloat(entryBuilder.withName("t_float").withType(Schema.Type.FLOAT).build(), 1000.0f)
                .withDouble(entryBuilder.withName("t_double").withType(Schema.Type.DOUBLE).build(), 1.5)
                .withBoolean(entryBuilder.withName("t_boolean").withType(Schema.Type.BOOLEAN).build(), false)
                .withDateTime(entryBuilder.withName("t_datetime").withType(Schema.Type.DATETIME).build(), ZONED_DATE_TIME)
                .build();
        Record record2 = recordBuilderFactory.newRecordBuilder()
                .withString(entryBuilder.withName("t_string").withType(Schema.Type.STRING).build(), "data2")
                .withInt(entryBuilder.withName("t_int").withType(Schema.Type.INT).build(), 22222)
                .withLong(entryBuilder.withName("t_long").withType(Schema.Type.LONG).build(), 2_000_000_000_000L)
                .withBytes(entryBuilder.withName("t_bytes").withType(Schema.Type.BYTES).build(), "test2".getBytes())
                .withFloat(entryBuilder.withName("t_float").withType(Schema.Type.FLOAT).build(), 2000.0f)
                .withDouble(entryBuilder.withName("t_double").withType(Schema.Type.DOUBLE).build(), 2.5)
                .withBoolean(entryBuilder.withName("t_boolean").withType(Schema.Type.BOOLEAN).build(), true)
                .withDateTime(entryBuilder.withName("t_datetime").withType(Schema.Type.DATETIME).build(), ZONED_DATE_TIME)
                .build();
        records.add(record1);
        records.add(record2);
        componentsHandler.setInputData(records);
    }

    @Test
    void totalNumbersOfRecordsTest() {
        madeRecords();
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
        madeRecords();
        final String outputConfig = configurationByExample().forInstance(getOutputConfiguration()).configured().toQueryString();

        Job.components().component("Couchbase_Output", "Couchbase://Output?" + outputConfig)
                .component("emitter", "test://emitter").connections().from("emitter").to("Couchbase_Output").build().run();

        final String inputConfig = configurationByExample().forInstance(getInputConfiguration()).configured().toQueryString();
        Job.components().component("Couchbase_Input", "Couchbase://Input?" + inputConfig)
                .component("collector", "test://collector").connections().from("Couchbase_Input").to("collector").build().run();

        final List<Record> res = componentsHandler.getCollectedData(Record.class);
        // check first record
        assertEquals("data1", res.get(0).getString("t_string"));
        assertEquals(11111, res.get(0).getInt("t_int"));
        assertEquals(1_000_000_000_000L, res.get(0).getLong("t_long"));
        // assertEquals("test1".getBytes(), res.get(0).getString("t_bytes"));
        // TODO: think about byte arrays
        assertEquals(1000.0F, res.get(0).getFloat("t_float"));
        assertEquals(1.5, res.get(0).getDouble("t_double"));
        assertEquals(false, res.get(0).getBoolean("t_boolean"));
        assertEquals(ZONED_DATE_TIME, res.get(0).getDateTime("t_datetime"));

        assertEquals("data2", res.get(1).getString("t_string"));
        assertEquals(22222, res.get(1).getInt("t_int"));
        assertEquals(2_000_000_000_000L, res.get(1).getLong("t_long"));
        // assertEquals("test2".getBytes(), res.get(1).getString("t_bytes"));
        // TODO: think about byte arrays
        assertEquals(2000.0F, res.get(1).getFloat("t_float"));
        assertEquals(2.5, res.get(1).getDouble("t_double"));
        assertEquals(true, res.get(1).getBoolean("t_boolean"));
        assertEquals(ZONED_DATE_TIME, res.get(1).getDateTime("t_datetime"));
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
