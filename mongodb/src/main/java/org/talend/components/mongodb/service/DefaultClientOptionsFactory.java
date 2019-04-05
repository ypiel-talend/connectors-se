package org.talend.components.mongodb.service;

import com.mongodb.MongoClientOptions;
import org.talend.components.mongodb.datastore.MongoDBDatastore;
import org.talend.components.mongodb.service.ClientOptionsFactory;
import org.talend.components.mongodb.service.I18nMessage;

public class DefaultClientOptionsFactory extends ClientOptionsFactory {

    public DefaultClientOptionsFactory(final MongoDBDatastore datastore, final I18nMessage i18nMessage) {
        super(datastore, i18nMessage);
    }

    @Override
    public void setSpecificOptions(final MongoClientOptions.Builder builder) {
        // noop
    }
}
