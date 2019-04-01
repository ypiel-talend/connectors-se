package org.talend.components.mongodb.output.processor.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOneModel;
import org.bson.Document;
import org.talend.components.mongodb.output.processor.SingleModelWriter;

public class UpsertWithSetModelWriter extends SingleModelWriter<UpdateOneModel<Document>> {

    public UpsertWithSetModelWriter(MongoCollection<Document> collection) {
        super(collection);
    }

    @Override
    public void putModel(UpdateOneModel<Document> model) {
        collection.updateOne(model.getFilter(), model.getUpdate(), model.getOptions());
    }
}
