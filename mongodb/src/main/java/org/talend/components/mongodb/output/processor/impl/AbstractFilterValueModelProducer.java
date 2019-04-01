package org.talend.components.mongodb.output.processor.impl;

import com.mongodb.client.model.WriteModel;
import org.bson.Document;
import org.talend.components.mongodb.output.DBObjectUtil;
import org.talend.components.mongodb.output.MongoDBOutputConfiguration;
import org.talend.components.mongodb.output.OutputMapping;
import org.talend.components.mongodb.output.processor.ModelProducer;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractFilterValueModelProducer<T extends WriteModel<Document>> implements ModelProducer<T> {

    private DBObjectUtil dbObjectUtil;

    private DBObjectUtil queryObjectUtil;

    private final Set<String> keys;

    public AbstractFilterValueModelProducer(MongoDBOutputConfiguration configuration) {
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
        if (dbObjectUtil == null) {
            dbObjectUtil = new DBObjectUtil();
            dbObjectUtil.setObject(new Document());
        }
        dbObjectUtil.put(mapping, col, value);
    }

    @Override
    public T createRecord() {
        Document filter = queryObjectUtil.getObject();
        Document object = dbObjectUtil.getObject();
        queryObjectUtil = null;
        dbObjectUtil = null;
        if (filter.keySet().isEmpty()) {
            return null;
        }
        return doCreateModel(filter, object);
    }

    protected abstract T doCreateModel(Document filter, Document object);
}
