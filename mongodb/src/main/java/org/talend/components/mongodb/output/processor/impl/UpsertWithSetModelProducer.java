package org.talend.components.mongodb.output.processor.impl;

import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.talend.components.mongodb.output.MongoDBOutputConfiguration;

public class UpsertWithSetModelProducer extends AbstractFilterValueModelProducer<UpdateOneModel<Document>> {

    public UpsertWithSetModelProducer(MongoDBOutputConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected UpdateOneModel<Document> doCreateModel(Document filter, Document object) {
        return new UpdateOneModel<>(filter, new Document("$set", object), new UpdateOptions().upsert(true));
    }
}
