package org.talend.components.mongodb.output;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.talend.sdk.component.api.record.Schema.Type.*;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertOneModel;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.talend.components.mongodb.dataset.MongoDBDataset;
import org.talend.components.mongodb.datastore.MongoDBDatastore;
import org.talend.components.mongodb.utils.MongoDBTestConstants;
import org.talend.components.mongodb.utils.MongoDBTestExtension;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.manager.chain.Job;

import java.util.*;
import java.util.stream.Collectors;

@WithComponents("org.talend.components.mongodb")
@ExtendWith(MongoDBTestExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MongoDBOutputOutputTestIT {

    @Injected
    private BaseComponentsHandler componentsHandler;

    private MongoDBTestExtension.TestContext testContext;

    @Service
    private RecordBuilderFactory recordBuilderFactory;

    private Schema schema;

    private MongoClient client;

    @BeforeAll
    private void init(MongoDBTestExtension.TestContext testContext) {
        this.testContext = testContext;
    }

    @BeforeEach
    private void tearDown() {
        this.client = createClient();
        client.getDatabase(MongoDBTestConstants.DATABASE_NAME).getCollection(MongoDBTestConstants.COLLECTION_NAME).drop();
        schema = createSchema();
    }

    @Test
    void insertBulk() {
        final int rowCount = 10;
        List<Record> data = createTestData(rowCount);
        componentsHandler.setInputData(data);

        MongoDBOutputConfiguration outputConfiguration = createConfiguration();
        outputConfiguration.setActionOnData(MongoDBOutputConfiguration.ActionOnData.INSERT);
        outputConfiguration.setDropCollectionIfExists(true);
        outputConfiguration.setBulkWrite(true);
        outputConfiguration.setBulkWriteType(MongoDBOutputConfiguration.BulkWriteType.ORDERED);
        final String config = configurationByExample().forInstance(outputConfiguration).configured().toQueryString();

        Job.components().component("emitter", "test://emitter").component("mongoDBOutput", "MongoDB://MongoDBOutput?" + config)
                .connections().from("emitter").to("mongoDBOutput").build().run();

        MongoClient client = createClient();
        MongoCollection<Document> collection = client.getDatabase(MongoDBTestConstants.DATABASE_NAME)
                .getCollection(MongoDBTestConstants.COLLECTION_NAME);
        long documentsCount = collection.countDocuments();

        assertEquals(rowCount, documentsCount);
    }

    @Test
    void insertSingle() {
        final int rowCount = 10;
        List<Record> data = createTestData(rowCount);
        componentsHandler.setInputData(data);

        MongoDBOutputConfiguration outputConfiguration = createConfiguration();
        outputConfiguration.setActionOnData(MongoDBOutputConfiguration.ActionOnData.INSERT);
        outputConfiguration.setDropCollectionIfExists(true);
        outputConfiguration.setBulkWrite(false);
        outputConfiguration.setBulkWriteType(MongoDBOutputConfiguration.BulkWriteType.ORDERED);
        final String config = configurationByExample().forInstance(outputConfiguration).configured().toQueryString();

        Job.components().component("emitter", "test://emitter").component("mongoDBOutput", "MongoDB://MongoDBOutput?" + config)
                .connections().from("emitter").to("mongoDBOutput").build().run();

        MongoClient client = createClient();
        MongoCollection<Document> collection = client.getDatabase(MongoDBTestConstants.DATABASE_NAME)
                .getCollection(MongoDBTestConstants.COLLECTION_NAME);
        long documentsCount = collection.countDocuments();

        assertEquals(rowCount, documentsCount);
    }

    @Test
    void deleteBulk() {
        // Write initial data
        MongoCollection<Document> collection = client.getDatabase(MongoDBTestConstants.DATABASE_NAME)
                .getCollection(MongoDBTestConstants.COLLECTION_NAME);

        int documentsCount = 10;
        List<Document> testDocs = createInitialDocuments(documentsCount);
        List<InsertOneModel<Document>> insertList = testDocs.stream().map(InsertOneModel::new).collect(Collectors.toList());
        collection.bulkWrite(insertList);

        // check that data was inserted
        assertEquals(documentsCount, collection.countDocuments());

        List<Record> data = convertToRecords(testDocs.iterator(), schema);
        componentsHandler.setInputData(data);

        MongoDBOutputConfiguration outputConfiguration = createConfiguration();
        outputConfiguration.setKeys(Arrays.asList("col1"));

        outputConfiguration.setActionOnData(MongoDBOutputConfiguration.ActionOnData.DELETE);
        outputConfiguration.setBulkWrite(true);
        outputConfiguration.setBulkWriteType(MongoDBOutputConfiguration.BulkWriteType.ORDERED);
        final String config = configurationByExample().forInstance(outputConfiguration).configured().toQueryString();

        Job.components().component("emitter", "test://emitter").component("mongoDBOutput", "MongoDB://MongoDBOutput?" + config)
                .connections().from("emitter").to("mongoDBOutput").build().run();

        long resCount = collection.countDocuments();
        assertEquals(0, resCount, "Documents have not been deleted");
    }

    @Test
    void deleteSingle() {
        // Write initial data
        MongoCollection<Document> collection = client.getDatabase(MongoDBTestConstants.DATABASE_NAME)
                .getCollection(MongoDBTestConstants.COLLECTION_NAME);

        int documentsCount = 10;
        List<Document> testDocs = createInitialDocuments(documentsCount);
        List<InsertOneModel<Document>> insertList = testDocs.stream().map(InsertOneModel::new).collect(Collectors.toList());
        collection.bulkWrite(insertList);

        // check that data was inserted
        assertEquals(documentsCount, collection.countDocuments());

        List<Record> data = convertToRecords(testDocs.iterator(), schema);
        componentsHandler.setInputData(data);

        MongoDBOutputConfiguration outputConfiguration = createConfiguration();
        outputConfiguration.setKeys(Arrays.asList("col1"));

        outputConfiguration.setActionOnData(MongoDBOutputConfiguration.ActionOnData.DELETE);
        outputConfiguration.setBulkWrite(false);
        outputConfiguration.setBulkWriteType(MongoDBOutputConfiguration.BulkWriteType.ORDERED);
        final String config = configurationByExample().forInstance(outputConfiguration).configured().toQueryString();

        Job.components().component("emitter", "test://emitter").component("mongoDBOutput", "MongoDB://MongoDBOutput?" + config)
                .connections().from("emitter").to("mongoDBOutput").build().run();

        long resCount = collection.countDocuments();
        assertEquals(0, resCount, "Documents have not been deleted");
    }

    @Test
    void upsertBulk() {
        // Write initial data
        MongoCollection<Document> collection = client.getDatabase(MongoDBTestConstants.DATABASE_NAME)
                .getCollection(MongoDBTestConstants.COLLECTION_NAME);

        int documentsCount = 10;
        List<Document> testDocs = createInitialDocuments(documentsCount);
        List<InsertOneModel<Document>> insertList = testDocs.stream().map(InsertOneModel::new).collect(Collectors.toList());
        collection.bulkWrite(insertList);

        // check that data was inserted
        assertEquals(documentsCount, collection.countDocuments());

        for (Document doc : testDocs) {
            doc.put("col2", "unknown");
        }

        testDocs.add(createDocument(documentsCount + 1));

        List<Record> data = convertToRecords(testDocs.iterator(), schema);
        componentsHandler.setInputData(data);

        MongoDBOutputConfiguration outputConfiguration = createConfiguration();
        outputConfiguration.setKeys(Arrays.asList("col1"));

        outputConfiguration.setActionOnData(MongoDBOutputConfiguration.ActionOnData.UPSERT);
        outputConfiguration.setBulkWrite(true);
        outputConfiguration.setBulkWriteType(MongoDBOutputConfiguration.BulkWriteType.ORDERED);
        final String config = configurationByExample().forInstance(outputConfiguration).configured().toQueryString();

        Job.components().component("emitter", "test://emitter").component("mongoDBOutput", "MongoDB://MongoDBOutput?" + config)
                .connections().from("emitter").to("mongoDBOutput").build().run();

        long resCount = collection.countDocuments();
        assertEquals(documentsCount + 1, resCount);
    }

    @Test
    void upsertSingle() {
        // Write initial data
        MongoCollection<Document> collection = client.getDatabase(MongoDBTestConstants.DATABASE_NAME)
                .getCollection(MongoDBTestConstants.COLLECTION_NAME);

        int documentsCount = 10;
        List<Document> testDocs = createInitialDocuments(documentsCount);
        List<InsertOneModel<Document>> insertList = testDocs.stream().map(InsertOneModel::new).collect(Collectors.toList());
        collection.bulkWrite(insertList);

        // check that data was inserted
        assertEquals(documentsCount, collection.countDocuments());

        for (Document doc : testDocs) {
            doc.put("col2", "unknown");
        }

        testDocs.add(createDocument(documentsCount + 1));

        List<Record> data = convertToRecords(testDocs.iterator(), schema);
        componentsHandler.setInputData(data);

        MongoDBOutputConfiguration outputConfiguration = createConfiguration();
        outputConfiguration.setKeys(Arrays.asList("col1"));

        outputConfiguration.setActionOnData(MongoDBOutputConfiguration.ActionOnData.UPSERT);
        outputConfiguration.setBulkWrite(false);
        final String config = configurationByExample().forInstance(outputConfiguration).configured().toQueryString();

        Job.components().component("emitter", "test://emitter").component("mongoDBOutput", "MongoDB://MongoDBOutput?" + config)
                .connections().from("emitter").to("mongoDBOutput").build().run();

        long resCount = collection.countDocuments();
        assertEquals(documentsCount + 1, resCount);
    }

    private List<Document> createInitialDocuments(int count) {
        List<Document> result = new ArrayList<>();
        for (int currentCount = 0; currentCount < count; currentCount++) {
            Document doc = createDocument(currentCount);
            result.add(doc);
        }
        return result;
    }

    private Document createDocument(int currentCount) {
        Document doc = new Document();
        doc.put("col1", currentCount);
        doc.put("col2", String.valueOf("artist" + currentCount));
        doc.put("col3", currentCount * 100L);
        doc.put("col4", currentCount / 3.0);
        doc.put("col5", currentCount % 2 == 0);
        doc.put("col6", new Date());
        doc.put("col7", ("String" + currentCount).getBytes());
        return doc;
    }

    private MongoDBOutputConfiguration createConfiguration() {
        MongoDBDatastore datastore = testContext.getDataStore();
        MongoDBDataset dataset = new MongoDBDataset();
        dataset.setDatastore(datastore);
        dataset.setCollection(MongoDBTestConstants.COLLECTION_NAME);

        MongoDBOutputConfiguration outputConfiguration = new MongoDBOutputConfiguration();
        outputConfiguration.setDataset(dataset);
        return outputConfiguration;
    }

    private List<Record> createTestData(int count) {
        List<Record> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(next(i));
        }
        return result;
    }

    public Schema createSchema() {
        // No reason to check float, as mongodb saves it as double anyway.
        Schema.Builder schemaBuilder = recordBuilderFactory.newSchemaBuilder(Schema.Type.RECORD)
                .withEntry(recordBuilderFactory.newEntryBuilder().withName("col1").withType(INT).withNullable(false).build())
                .withEntry(recordBuilderFactory.newEntryBuilder().withName("col2").withType(STRING).build())
                .withEntry(recordBuilderFactory.newEntryBuilder().withName("col3").withType(LONG).build())
                .withEntry(recordBuilderFactory.newEntryBuilder().withName("col4").withType(DOUBLE).build())
                .withEntry(recordBuilderFactory.newEntryBuilder().withName("col5").withType(BOOLEAN).build())
                .withEntry(recordBuilderFactory.newEntryBuilder().withName("col6").withType(DATETIME).build())
                .withEntry(recordBuilderFactory.newEntryBuilder().withName("col7").withType(BYTES).build());

        return schemaBuilder.build();
    }

    public Record next(int currentCount) {
        final Record.Builder builder = recordBuilderFactory.newRecordBuilder(schema);
        builder.withInt("col1", currentCount);
        builder.withString("col2", String.valueOf("artist" + currentCount));
        builder.withLong("col3", currentCount * 100);
        builder.withDouble("col4", currentCount / 3.0);
        builder.withBoolean("col5", currentCount % 2 == 0);
        builder.withDateTime("col6", new Date());
        builder.withBytes("col7", "String".getBytes());

        return builder.build();
    }

    private MongoClient createClient() {
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        if (testContext.getDataStore().isUseSSL()) {
            builder.sslEnabled(true);
        }
        MongoClientOptions opts = builder.build();
        MongoClient client = new MongoClient(
                new ServerAddress(testContext.getDataStore().getServer(), testContext.getDataStore().getPort()),
                MongoCredential.createCredential(testContext.getDataStore().getUsername(),
                        testContext.getDataStore().getAuthenticationDatabase(),
                        testContext.getDataStore().getPassword().toCharArray()),
                opts);
        return client;
    }

    private List<Record> convertToRecords(Iterator<Document> documentsIterator, Schema schema) {
        List<Record> result = new ArrayList<>();
        while (documentsIterator.hasNext()) {
            Document doc = documentsIterator.next();
            Record.Builder recordBuilder = recordBuilderFactory.newRecordBuilder(schema);
            schema.getEntries().stream().map(Schema.Entry::getName).forEach(c -> addColumn(recordBuilder, c, doc.get(c)));
            result.add(recordBuilder.build());
        }
        return result;
    }

    private void addColumn(final Record.Builder builder, final String name, Object value) {
        final Schema.Entry.Builder entryBuilder = recordBuilderFactory.newEntryBuilder();
        entryBuilder.withName(name).withNullable(true);
        if (value instanceof ObjectId) {
            builder.withString(entryBuilder.withType(Schema.Type.STRING).build(), value == null ? null : value.toString());
        } else if (value instanceof String) {
            builder.withString(entryBuilder.withType(Schema.Type.STRING).build(), value == null ? null : (String) value);
        } else if (value instanceof Boolean) {
            builder.withBoolean(entryBuilder.withType(Schema.Type.BOOLEAN).build(), value == null ? null : (Boolean) value);
        } else if (value instanceof Date) {
            builder.withDateTime(entryBuilder.withType(Schema.Type.DATETIME).build(), value == null ? null : (Date) value);
        } else if (value instanceof Double) {
            builder.withDouble(entryBuilder.withType(Schema.Type.DOUBLE).build(), value == null ? null : (Double) value);
        } else if (value instanceof Integer) {
            builder.withInt(entryBuilder.withType(Schema.Type.INT).build(), value == null ? null : (Integer) value);
        } else if (value instanceof Long) {
            builder.withLong(entryBuilder.withType(Schema.Type.LONG).build(), value == null ? null : (Long) value);
        } else if (value instanceof Binary) {
            builder.withBytes(entryBuilder.withType(Schema.Type.BYTES).build(),
                    value == null ? null : ((Binary) value).getData());
        } else if (value instanceof byte[]) {
            builder.withBytes(entryBuilder.withType(Schema.Type.BYTES).build(), (byte[]) value);
        } else {
            builder.withString(entryBuilder.withType(Schema.Type.STRING).build(), value == null ? null : value.toString());
        }
    }
}