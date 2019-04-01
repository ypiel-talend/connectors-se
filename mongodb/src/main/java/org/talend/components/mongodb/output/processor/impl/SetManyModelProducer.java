package org.talend.components.mongodb.output.processor.impl;

import com.mongodb.client.model.UpdateManyModel;
import org.bson.Document;
import org.talend.components.mongodb.output.MongoDBOutputConfiguration;

public class SetManyModelProducer extends AbstractFilterValueModelProducer<UpdateManyModel<Document>> {

    public SetManyModelProducer(MongoDBOutputConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected UpdateManyModel<Document> doCreateModel(Document filter, Document object) {
        return new UpdateManyModel<>(filter, new Document("$set", object));
    }
}
