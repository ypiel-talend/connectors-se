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

    public static class OutputClientOptionsFactory extends ClientOptionsFactory {

        private final MongoDBOutputConfiguration properties;

        public OutputClientOptionsFactory(final MongoDBOutputConfiguration properties) {
            super(properties.getDataset().getDatastore());
            this.properties = properties;
        }

        @Override
        protected void setSpecificOptions(final MongoClientOptions.Builder builder) {
            if (properties.isSetWriteConcern() && properties.getWriteConcern() != null) {
                builder.writeConcern(convertWriteConcern(properties.getWriteConcern())).build();
            }
        }

        private WriteConcern convertWriteConcern(MongoDBOutputConfiguration.WriteConcern writeConcern) {
            switch (writeConcern) {
            case ACKNOWLEDGED:
                return WriteConcern.ACKNOWLEDGED;
            case UNACKNOWLEDGED:
                return WriteConcern.UNACKNOWLEDGED;
            case JOURNALED:
                return WriteConcern.JOURNALED;
            case REPLICA_ACKNOWLEDGED:
                return WriteConcern.W2;
            default:
                throw new IllegalArgumentException("Unknown write concern");
            }
        }

    }

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