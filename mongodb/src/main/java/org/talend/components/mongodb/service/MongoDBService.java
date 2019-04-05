package org.talend.components.mongodb.service;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.talend.components.mongodb.dataset.MongoDBDataset;
import org.talend.components.mongodb.datastore.MongoDBDatastore;
import org.talend.components.mongodb.output.MongoDBOutputConfiguration;
import org.talend.components.mongodb.source.MongoDBInputMapperConfiguration;
import org.talend.sdk.component.api.service.Service;

@Service
@Slf4j
public class MongoDBService {

    @Service
    private I18nMessage i18nMessage;

    public MongoClient getMongoClient(final MongoDBDatastore datastore, final ClientOptionsFactory optionsFactory) {
        MongoClientFactory factory = MongoClientFactory.getInstance(datastore, optionsFactory.createOptions(), i18nMessage);
        log.debug(i18nMessage.factoryClass(factory.getClass().getName()));
        MongoClient mongo = factory.createClient();
        return mongo;
    }

    public MongoCollection<Document> getCollection(final MongoDBDataset dataset, final MongoClient client) {
        log.debug(i18nMessage.retrievingCollection(dataset.getCollection()));
        MongoDatabase db = client.getDatabase(dataset.getDatastore().getDatabase());
        return db.getCollection(dataset.getCollection());
    }

}