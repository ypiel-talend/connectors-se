package org.talend.components.mongodb.output.processor;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;

public abstract class SingleModelWriter<T extends WriteModel<Document>> implements ModelWriter<T> {

    protected final MongoCollection<Document> collection;

    public SingleModelWriter(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    @Override
    public void flush() {
        // noop
    }

    @Override
    public void close() {
        // noop
    }

}
