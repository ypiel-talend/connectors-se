package org.talend.components.mongodb.output.processor.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOneModel;
import org.bson.Document;
import org.talend.components.mongodb.output.processor.SingleModelWriter;

public class UpsertModelWriter extends SingleModelWriter<ReplaceOneModel<Document>> {

    public UpsertModelWriter(MongoCollection<Document> collection) {
        super(collection);
    }

    @Override
    public void putModel(ReplaceOneModel<Document> model) {
        collection.replaceOne(model.getFilter(), model.getReplacement(), model.getReplaceOptions());
    }
}
