package org.talend.components.mongodb.output.processor.impl;

import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.talend.components.mongodb.output.MongoDBOutputConfiguration;

public class UpsertModelProducer extends AbstractFilterValueModelProducer<ReplaceOneModel<Document>> {

    public UpsertModelProducer(MongoDBOutputConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected ReplaceOneModel<Document> doCreateModel(Document filter, Document object) {
        return new ReplaceOneModel<>(filter, object, new ReplaceOptions().upsert(true));
    }
}
