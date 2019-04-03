package org.talend.components.mongodb.output.processor.impl;

import com.mongodb.client.model.WriteModel;
import org.bson.Document;
import org.talend.components.mongodb.output.MongoDocumentWrapper;
import org.talend.components.mongodb.output.MongoDBOutputConfiguration;
import org.talend.components.mongodb.output.OutputMapping;
import org.talend.components.mongodb.output.processor.ModelProducer;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractFilterValueModelProducer<T extends WriteModel<Document>> implements ModelProducer<T> {

    private MongoDocumentWrapper mongoDocumentWrapper;

    private MongoDocumentWrapper queryDocumentWrapper;

    private final Set<String> keys;

    public AbstractFilterValueModelProducer(MongoDBOutputConfiguration configuration) {
        this.keys = new HashSet<>(configuration.getKeys());
    }

    @Override
    public void addField(OutputMapping mapping, String col, Object value) {
        if (queryDocumentWrapper == null) {
            queryDocumentWrapper = new MongoDocumentWrapper();
        }
        if (keys.contains(col)) {
            queryDocumentWrapper.putkeyNode(mapping, col, value);
        }
        if (mongoDocumentWrapper == null) {
            mongoDocumentWrapper = new MongoDocumentWrapper();
        }
        mongoDocumentWrapper.put(mapping, col, value);
    }

    @Override
    public T createRecord() {
        Document filter = queryDocumentWrapper.getObject();
        Document object = mongoDocumentWrapper.getObject();
        queryDocumentWrapper = null;
        mongoDocumentWrapper = null;
        if (filter.keySet().isEmpty()) {
            return null;
        }
        return doCreateModel(filter, object);
    }

    protected abstract T doCreateModel(Document filter, Document object);
}
