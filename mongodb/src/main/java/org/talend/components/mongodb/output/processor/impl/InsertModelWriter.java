package org.talend.components.mongodb.output.processor.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertOneModel;
import org.bson.Document;
import org.talend.components.mongodb.output.processor.SingleModelWriter;

public class InsertModelWriter extends SingleModelWriter<InsertOneModel<Document>> {

    public InsertModelWriter(MongoCollection<Document> collection) {
        super(collection);
    }

    @Override
    public void putModel(InsertOneModel<Document> model) {
        collection.insertOne(model.getDocument());
    }

}
