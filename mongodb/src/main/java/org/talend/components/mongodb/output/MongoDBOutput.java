package org.talend.components.mongodb.output;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOneModel;
import org.bson.Document;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.*;
import org.talend.sdk.component.api.record.Record;

import org.talend.components.mongodb.service.MongoDBService;
import org.talend.sdk.component.api.record.Schema;

@Version(1) // default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Icon(Icon.IconType.STAR) // you can use a custom one using @Icon(value=CUSTOM, custom="filename") and adding
                          // icons/filename_icon32.png in resources
@Processor(name = "MongoDBOutput")
@Documentation("TODO fill the documentation for this processor")
public class MongoDBOutput implements Serializable {

    private final MongoDBOutputConfiguration configuration;

    private final MongoDBService service;

    private MongoClient client;

    private MongoCollection<Document> collection;

    private Map<String, OutputMapping> mapping;

    private Schema schema;

    private List<String> columnsList = new ArrayList<>();

    public MongoDBOutput(@Option("configuration") final MongoDBOutputConfiguration configuration, final MongoDBService service) {
        this.configuration = configuration;
        this.service = service;
    }

    @PostConstruct
    public void init() {
        // this method will be executed once for the whole component execution,
        // this is where you can establish a connection for instance
        // Note: if you don't need it you can delete it
        this.client = service.getMongoClient(configuration.getDataset().getDatastore(),
                new MongoDBService.OutputClientOptionsFactory(configuration));
        this.collection = service.getCollection(configuration.getDataset(), client);
        if (configuration.isDropCollectionIfExists()) {
            collection.drop();
        }
        List<OutputMapping> outputMappings = configuration.getOutputMappingList();
        if (outputMappings != null) {
            mapping = outputMappings.stream().collect(Collectors.toMap(OutputMapping::getColumn, c -> c));
        } else {
            mapping = Collections.emptyMap();
        }
        if (configuration.getDataset().getSchema() != null) {
            columnsList.addAll(configuration.getDataset().getSchema());
        }
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
        final DBObjectUtil_tMongoDBOutput_1 dbObjectUtil = new DBObjectUtil_tMongoDBOutput_1();
        dbObjectUtil.setObject(new Document());
        if (columnsList.isEmpty()) {
            columnsList.addAll(
                    defaultInput.getSchema().getEntries().stream().map(Schema.Entry::getName).collect(Collectors.toList()));
        }
        final Map<String, Schema.Type> defaultInputTypesMap = defaultInput.getSchema().getEntries().stream()
                .collect(Collectors.toMap(Schema.Entry::getName, Schema.Entry::getType));
        columnsList.stream().forEach(
                col -> dbObjectUtil.put(mapping.get(col), col, getValue(defaultInput, col, defaultInputTypesMap.get(col))));
        org.bson.Document updateObj_tMongoDBOutput_1 = dbObjectUtil.getObject();
        collection.insertOne(updateObj_tMongoDBOutput_1);
    }

    private Object getValue(Record defaultInput, String col, Schema.Type type) {
        Object value = null;
        if (type == null) {
            value = defaultInput.get(Object.class, col);
        } else {
            switch (type) {
            case INT:
                value = defaultInput.getInt(col);
                break;
            case LONG:
                value = defaultInput.getLong(col);
                break;
            case RECORD:
                break;
            case ARRAY:
                value = defaultInput.getArray(Object.class, col);
                break;
            case STRING:
                value = defaultInput.getString(col);
                break;
            case BYTES:
                value = defaultInput.getBytes(col);
                break;
            case FLOAT:
                value = defaultInput.getFloat(col);
                break;
            case DOUBLE:
                value = defaultInput.getDouble(col);
                break;
            case BOOLEAN:
                value = defaultInput.getBoolean(col);
                break;
            case DATETIME:
                value = defaultInput.getDateTime(col);
                break;
            }
        }
        return value;
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
        client.close();
    }

    static class DBObjectUtil_tMongoDBOutput_1 {

        private org.bson.Document object = null;

        // Put value to embedded document
        // If have no embedded document, put the value to root document
        public void put(OutputMapping mapping, String curentName, Object value) {
            String parentNode = mapping == null ? null : mapping.getParentNodePath();
            boolean removeNullField = mapping == null ? false : mapping.isRemoveNullField();
            if (removeNullField && value == null) {
                return;
            }
            if (parentNode == null || "".equals(parentNode)) {
                object.put(curentName, value);
            } else {
                String objNames[] = parentNode.split("\\.");
                org.bson.Document lastNode = getParentNode(parentNode, objNames.length - 1);
                lastNode.put(curentName, value);
                org.bson.Document parenttNode = null;
                for (int i = objNames.length - 1; i >= 0; i--) {
                    parenttNode = getParentNode(parentNode, i - 1);
                    parenttNode.put(objNames[i], lastNode);
                    lastNode = clone(parenttNode);
                }
                object = lastNode;
            }
        }

        private org.bson.Document clone(org.bson.Document source) {
            org.bson.Document to = new org.bson.Document();
            for (java.util.Map.Entry<String, Object> cur : source.entrySet()) {
                to.append(cur.getKey(), cur.getValue());
            }
            return to;
        }

        // Get node(embedded document) by path configuration
        public org.bson.Document getParentNode(String parentNode, int index) {
            org.bson.Document document = object;
            if (parentNode == null || "".equals(parentNode)) {
                return object;
            } else {
                String objNames[] = parentNode.split("\\.");
                for (int i = 0; i <= index; i++) {
                    document = (org.bson.Document) document.get(objNames[i]);
                    if (document == null) {
                        document = new org.bson.Document();
                        return document;
                    }
                    if (i == index) {
                        break;
                    }
                }
                return document;
            }
        }

        public void putkeyNode(OutputMapping mapping, String curentName, Object value) {
            String parentNode = mapping.getParentNodePath();
            if (mapping == null || parentNode == null || "".equals(parentNode) || ".".equals(parentNode)) {
                put(mapping, curentName, value);
            } else {
                put(null, parentNode + "." + curentName, value);
            }
        }

        public org.bson.Document getObject() {
            return this.object;
        }

        public void setObject(org.bson.Document object) {
            this.object = object;
        }

    }

}