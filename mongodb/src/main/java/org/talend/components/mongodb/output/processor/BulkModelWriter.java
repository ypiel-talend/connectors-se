package org.talend.components.mongodb.output.processor;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;
import org.talend.components.mongodb.output.MongoDBOutputConfiguration;

import java.util.ArrayList;
import java.util.List;

public class BulkModelWriter<T extends WriteModel<Document>> implements ModelWriter<T> {

    private final List<T> batch = new ArrayList<>();

    private final MongoCollection<Document> collection;

    private final BulkWriteOptions options;

    public BulkModelWriter(MongoCollection<Document> collection, MongoDBOutputConfiguration.BulkWriteType writeType) {
        this.collection = collection;
        this.options = new BulkWriteOptions().ordered(writeType == MongoDBOutputConfiguration.BulkWriteType.ORDERED);
    }

    @Override
    public void putModel(T model) {
        batch.add(model);
    }

    @Override
    public void flush() {
        if (batch != null && !batch.isEmpty()) {
            collection.bulkWrite(batch, options);
            batch.clear();
        }
    }

    @Override
    public void close() {
        flush();
    }
}
