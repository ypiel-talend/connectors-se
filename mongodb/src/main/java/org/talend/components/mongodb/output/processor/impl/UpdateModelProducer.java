package org.talend.components.mongodb.output.processor.impl;

import com.mongodb.client.model.ReplaceOneModel;
import org.bson.Document;
import org.talend.components.mongodb.output.MongoDBOutputConfiguration;

public class UpdateModelProducer extends AbstractFilterValueModelProducer<ReplaceOneModel<Document>> {

    public UpdateModelProducer(MongoDBOutputConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected ReplaceOneModel<Document> doCreateModel(Document filter, Document object) {
        return new ReplaceOneModel<>(filter, object);
    }
}
