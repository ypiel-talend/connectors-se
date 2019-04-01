package org.talend.components.mongodb.output.processor.impl;

import com.mongodb.client.model.InsertOneModel;
import org.bson.Document;
import org.talend.components.mongodb.output.DBObjectUtil;
import org.talend.components.mongodb.output.OutputMapping;
import org.talend.components.mongodb.output.processor.ModelProducer;

public class InsertModelProducer implements ModelProducer<InsertOneModel<Document>> {

    private DBObjectUtil dbObjectUtil;

    @Override
    public void addField(OutputMapping mapping, String col, Object value) {
        if (dbObjectUtil == null) {
            dbObjectUtil = new DBObjectUtil();
            dbObjectUtil.setObject(new Document());
        }
        dbObjectUtil.put(mapping, col, value);
    }

    @Override
    public InsertOneModel<Document> createRecord() {
        InsertOneModel<Document> model = new InsertOneModel<>(dbObjectUtil.getObject());
        dbObjectUtil = null;
        return model;
    }
}
