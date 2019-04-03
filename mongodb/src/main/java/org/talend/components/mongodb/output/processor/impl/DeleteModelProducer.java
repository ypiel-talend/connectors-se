package org.talend.components.mongodb.output.processor.impl;

import com.mongodb.client.model.DeleteOneModel;
import org.bson.Document;
import org.talend.components.mongodb.output.MongoDocumentWrapper;
import org.talend.components.mongodb.output.MongoDBOutputConfiguration;
import org.talend.components.mongodb.output.OutputMapping;
import org.talend.components.mongodb.output.processor.ModelProducer;

import java.util.HashSet;
import java.util.Set;

public class DeleteModelProducer implements ModelProducer<DeleteOneModel<Document>> {

    private MongoDocumentWrapper queryDocumentWrapper;

    private final Set<String> keys;

    public DeleteModelProducer(final MongoDBOutputConfiguration configuration) {
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
    }

    @Override
    public DeleteOneModel<Document> createRecord() {
        Document filter = queryDocumentWrapper.getObject();
        queryDocumentWrapper = null;
        if (filter.keySet().isEmpty()) {
            return null;
        }
        return new DeleteOneModel<>(filter);
    }
}
