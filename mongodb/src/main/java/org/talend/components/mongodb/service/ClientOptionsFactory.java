package org.talend.components.mongodb.service;

import com.mongodb.MongoClientOptions;
import org.talend.components.mongodb.datastore.MongoDBDatastore;

public abstract class ClientOptionsFactory {

    private final MongoDBDatastore datastore;

    public ClientOptionsFactory(final MongoDBDatastore properties) {
        this.datastore = properties;
    }

    public MongoClientOptions createOptions() {
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        if (datastore.isUseSSL()) {
            builder.sslEnabled(true);
        }
        setSpecificOptions(builder);
        return builder.build();
    }

    protected abstract void setSpecificOptions(final MongoClientOptions.Builder builder);

}
