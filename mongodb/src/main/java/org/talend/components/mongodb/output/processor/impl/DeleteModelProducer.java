package org.talend.components.mongodb.output.processor.impl;

import com.mongodb.client.model.DeleteOneModel;
import org.bson.Document;
import org.talend.components.mongodb.output.DBObjectUtil;
import org.talend.components.mongodb.output.MongoDBOutputConfiguration;
import org.talend.components.mongodb.output.OutputMapping;
import org.talend.components.mongodb.output.processor.ModelProducer;

import java.util.HashSet;
import java.util.Set;

public class DeleteModelProducer implements ModelProducer<DeleteOneModel<Document>> {

    private DBObjectUtil queryObjectUtil;

    private final Set<String> keys;

    public DeleteModelProducer(final MongoDBOutputConfiguration configuration) {
        this.keys = new HashSet<>(configuration.getKeys());
    }

    @Override
    public void addField(OutputMapping mapping, String col, Object value) {
        if (queryObjectUtil == null) {
            queryObjectUtil = new DBObjectUtil();
            queryObjectUtil.setObject(new Document());
        }
        if (keys.contains(col)) {
            queryObjectUtil.putkeyNode(mapping, col, value);
        }
    }

    @Override
    public DeleteOneModel<Document> createRecord() {
        Document filter = queryObjectUtil.getObject();
        queryObjectUtil = null;
        if (filter.keySet().isEmpty()) {
            return null;
        }
        return new DeleteOneModel<>(filter);
    }
}
