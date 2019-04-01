package org.talend.components.mongodb.output.processor.impl;

import com.mongodb.client.model.UpdateOneModel;
import org.bson.Document;
import org.talend.components.mongodb.output.MongoDBOutputConfiguration;

public class SetOneModelProducer extends AbstractFilterValueModelProducer<UpdateOneModel<Document>> {

    public SetOneModelProducer(MongoDBOutputConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected UpdateOneModel<Document> doCreateModel(Document filter, Document object) {
        return new UpdateOneModel<>(filter, new Document("$set", object));
    }
}
