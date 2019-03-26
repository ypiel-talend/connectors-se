package org.talend.components.mongodb.source;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import org.talend.components.mongodb.service.MongoDBService;

import static org.talend.sdk.component.api.record.Schema.Type.RECORD;

@Slf4j
@Documentation("TODO fill the documentation for this source")
public class MongoDBInputSource implements Serializable {

    private final MongoDBInputMapperConfiguration configuration;

    private final MongoDBService service;

    private final RecordBuilderFactory builderFactory;

    private MongoClient mongoClient;

    private MongoCursor<Document> cursor;

    private Schema schema;

    public MongoDBInputSource(@Option("configuration") final MongoDBInputMapperConfiguration configuration,
            final MongoDBService service, final RecordBuilderFactory builderFactory) {
        this.configuration = configuration;
        this.service = service;
        this.builderFactory = builderFactory;
    }

    @PostConstruct
    public void init() {
        mongoClient = service.getMongoClient(configuration.getDataset().getDatastore(),
                new MongoDBService.InputClientOptionsFactory(configuration));
        MongoCollection<Document> collection = service.getCollection(configuration.getDataset(), mongoClient);

        boolean needIndexWarning = true;
        String indexList = "";
        for (DBObject index : collection.listIndexes(DBObject.class)) {
            for (String key : ((com.mongodb.DBObject) index.get("key")).keySet()) {
                // The regexp is:
                // - contain the db DBcolumnName between two backslashed quotes
                // - is followed at some point by a colon
                // - there is no comma between the the DBcolumnName and the colon
                if (configuration.getQuery().matches(".*" + key + "[^,]*:.*")) {
                    // We have an index, do not print error message
                    needIndexWarning = false;
                } else {
                    // This index is not in the query, add it into the indexList
                    indexList += ", " + key;
                }
            }

        }
        if ((!"".equals(indexList)) && (needIndexWarning)) {
            log.warn("tMongoDBInput_1 - The query does not contain any reference an index.  [" + indexList.substring(1) + " ]");
        }
        Document myQuery = Document.parse(configuration.getQuery());
        FindIterable<Document> fi = collection.find(myQuery).noCursorTimeout(configuration.isNoQueryTimeout());
        fi.limit(configuration.getLimit());
        // Map<String, String> pathMap = new HashMap<String, String>();
        // fi = fi.limit(inputConfig.getLimite());
        cursor = fi.iterator();
    }

    @Producer
    public Record next() {
        if (cursor.hasNext()) {
            Document document = cursor.next();
            if (schema == null) {
                schema = parseSchema(document);
            }
            final Record.Builder recordBuilder = builderFactory.newRecordBuilder(schema);
            document.entrySet().stream().forEach(entry -> addColumn(recordBuilder, entry));
            return recordBuilder.build();
        }
        return null;
    }

    private Schema parseSchema(Document document) {
        final Schema.Builder schemaBuilder = builderFactory.newSchemaBuilder(RECORD);
        document.entrySet().stream().forEach(entry -> addField(schemaBuilder, entry));
        return schemaBuilder.build();
    }

    private void addField(final Schema.Builder schemaBuilder, final Map.Entry<String, Object> entry) {
        final Object value = entry.getValue();
        final Schema.Entry.Builder entryBuilder = builderFactory.newEntryBuilder();
        entryBuilder.withName(entry.getKey()).withNullable(true);
        if (value instanceof ObjectId) {
            schemaBuilder.withEntry(entryBuilder.withType(Schema.Type.STRING).build());
        } else if (value instanceof String) {
            schemaBuilder.withEntry(entryBuilder.withType(Schema.Type.STRING).build());
        } else if (value instanceof Boolean) {
            schemaBuilder.withEntry(entryBuilder.withType(Schema.Type.BOOLEAN).build());
        } else if (value instanceof Date) {
            schemaBuilder.withEntry(entryBuilder.withType(Schema.Type.DATETIME).build());
        } else if (value instanceof Double) {
            schemaBuilder.withEntry(entryBuilder.withType(Schema.Type.DOUBLE).build());
        } else if (value instanceof Integer) {
            schemaBuilder.withEntry(entryBuilder.withType(Schema.Type.INT).build());
        } else if (value instanceof Long) {
            schemaBuilder.withEntry(entryBuilder.withType(Schema.Type.LONG).build());
        } else if (value instanceof Binary) {
            schemaBuilder.withEntry(entryBuilder.withType(Schema.Type.BYTES).build());
        } else {
            schemaBuilder.withEntry(entryBuilder.withType(Schema.Type.STRING).build());
        }
    }

    private void addColumn(final Record.Builder builder, final Map.Entry<String, Object> entry) {
        final Object value = entry.getValue();
        final Schema.Entry.Builder entryBuilder = builderFactory.newEntryBuilder();
        entryBuilder.withName(entry.getKey()).withNullable(true);
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
        } else {
            builder.withString(entryBuilder.withType(Schema.Type.STRING).build(), value == null ? null : value.toString());
        }
    }

    public Object getValue(String parentNode, String currentName, Document dbObject) {
        Object value = null;
        if (dbObject == null) {
            return null;
        }
        if (parentNode == null || "".equals(parentNode)) {
            if ("*".equals(currentName)) {
                value = dbObject;
            } else if (dbObject.get(currentName) != null) {
                value = dbObject.get(currentName);
            }
        } else {
            String objNames[] = parentNode.split("\\.");
            org.bson.Document currentObj = dbObject;
            for (int i = 0; i < objNames.length; i++) {
                currentObj = (org.bson.Document) currentObj.get(objNames[i]);
                if (currentObj == null) {
                    break;
                }
            }
            if ("*".equals(currentName)) {
                value = currentObj;
            } else if (currentObj != null) {
                value = currentObj.get(currentName);
            }
        }
        return value;
    }

    @PreDestroy
    public void release() {
        mongoClient.close();
    }
}