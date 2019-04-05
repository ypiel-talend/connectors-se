package org.talend.components.mongodb.source;

import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import org.talend.components.mongodb.service.ClientOptionsFactory;
import org.talend.components.mongodb.service.I18nMessage;
import org.talend.components.mongodb.source.MongoDBInputMapperConfiguration;

public class InputClientOptionsFactory extends ClientOptionsFactory {

    private final MongoDBInputMapperConfiguration properties;

    public InputClientOptionsFactory(final MongoDBInputMapperConfiguration properties, final I18nMessage i18nMessage) {
        super(properties.getDataset().getDatastore(), i18nMessage);
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
            throw new IllegalArgumentException(i18nMessage.unknownReadPreference(String.valueOf(readPreference)));
        }
    }

}
