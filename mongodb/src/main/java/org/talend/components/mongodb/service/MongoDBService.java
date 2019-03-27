package org.talend.components.mongodb.service;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.talend.components.mongodb.dataset.MongoDBDataset;
import org.talend.components.mongodb.datastore.MongoDBDatastore;
import org.talend.components.mongodb.source.MongoDBInputMapperConfiguration;
import org.talend.sdk.component.api.service.Service;

@Service
@Slf4j
public class MongoDBService {

    public static class DefaultClientOptionsFactory extends ClientOptionsFactory {

        public DefaultClientOptionsFactory(final MongoDBDatastore datastore) {
            super(datastore);
        }

        @Override
        public void setSpecificOptions(final MongoClientOptions.Builder builder) {
            // noop
        }
    }

    public static class InputClientOptionsFactory extends ClientOptionsFactory {

        private final MongoDBInputMapperConfiguration properties;

        public InputClientOptionsFactory(final MongoDBInputMapperConfiguration properties) {
            super(properties.getDataset().getDatastore());
            this.properties = properties;
        }

        @Override
        protected void setSpecificOptions(final MongoClientOptions.Builder builder) {
            if (properties.isSetReadPreference() && properties.getReadPreference() != null) {
                builder.readPreference(convertReadPreference(properties.getReadPreference())).build();
            }
        }

        private ReadPreference convertReadPreference(MongoDBInputMapperConfiguration.ReadPreference readPreference) {
            switch (readPreference) {
            case NEAREST:
                return ReadPreference.nearest();
            case PRIMARY:
                return ReadPreference.primary();
            case SECONDARY:
                return ReadPreference.secondary();
            case PRIMARY_PREFERRED:
                return ReadPreference.primaryPreferred();
            case SECONDARY_PREFERRED:
                return ReadPreference.secondaryPreferred();
            default:
                throw new IllegalArgumentException("Unknown read preference");
            }
        }

    }

    public MongoClient getMongoClient(final MongoDBDatastore datastore, final ClientOptionsFactory optionsFactory) {
        MongoClientFactory factory = MongoClientFactory.getInstance(datastore, optionsFactory.createOptions());
        MongoClient mongo = factory.createClient();
        return mongo;
    }

    public MongoCollection<Document> getCollection(final MongoDBDataset dataset, final MongoClient client) {
        MongoDatabase db = client.getDatabase(dataset.getDatastore().getDatabase());
        return db.getCollection(dataset.getCollection());
    }

}