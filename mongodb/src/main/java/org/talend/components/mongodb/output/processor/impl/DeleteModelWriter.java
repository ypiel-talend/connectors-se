package org.talend.components.mongodb.output.processor.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.DeleteOneModel;
import org.bson.Document;
import org.talend.components.mongodb.output.processor.SingleModelWriter;

public class DeleteModelWriter extends SingleModelWriter<DeleteOneModel<Document>> {

    public DeleteModelWriter(MongoCollection<Document> collection) {
        super(collection);
    }

    @Override
    public void putModel(DeleteOneModel<Document> model) {
        collection.deleteOne(model.getFilter());
    }
}
