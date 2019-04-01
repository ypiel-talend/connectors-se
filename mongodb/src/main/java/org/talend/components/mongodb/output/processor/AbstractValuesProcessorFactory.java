package org.talend.components.mongodb.output.processor;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.talend.components.mongodb.output.MongoDBOutputConfiguration;

public interface AbstractValuesProcessorFactory<T> {

    ModelProducer<T> createProducer(final MongoDBOutputConfiguration configuration);

    ModelWriter<T> createWriter(final MongoCollection<Document> collection);

}
