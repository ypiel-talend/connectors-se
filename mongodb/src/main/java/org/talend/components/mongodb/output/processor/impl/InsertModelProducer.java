package org.talend.components.mongodb.output.processor.impl;

import com.mongodb.client.model.InsertOneModel;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.talend.components.mongodb.output.MongoDocumentWrapper;
import org.talend.components.mongodb.output.OutputMapping;
import org.talend.components.mongodb.output.processor.ModelProducer;
import org.talend.components.mongodb.service.I18nMessage;

@Slf4j
public class InsertModelProducer implements ModelProducer<InsertOneModel<Document>> {

    private MongoDocumentWrapper mongoDocumentWrapper;

    @Override
    public void addField(OutputMapping mapping, String col, Object value) {
        if (mongoDocumentWrapper == null) {
            mongoDocumentWrapper = new MongoDocumentWrapper();
        }
        mongoDocumentWrapper.put(mapping, col, value);
    }

    @Override
    public InsertOneModel<Document> createRecord(I18nMessage i18nMessage) {
        InsertOneModel<Document> model = new InsertOneModel<>(mongoDocumentWrapper.getObject());
        mongoDocumentWrapper = null;
        return model;
    }
}
