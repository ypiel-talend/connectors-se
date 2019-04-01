package org.talend.components.mongodb.output.processor.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateManyModel;
import org.bson.Document;
import org.talend.components.mongodb.output.processor.SingleModelWriter;

public class SetManyModelWriter extends SingleModelWriter<UpdateManyModel<Document>> {

    public SetManyModelWriter(MongoCollection<Document> collection) {
        super(collection);
    }

    @Override
    public void putModel(UpdateManyModel<Document> model) {
        collection.updateMany(model.getFilter(), model.getUpdate());
    }
}
