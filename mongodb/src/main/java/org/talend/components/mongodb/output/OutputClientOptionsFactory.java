package org.talend.components.mongodb.output;

import com.mongodb.MongoClientOptions;
import com.mongodb.WriteConcern;
import org.talend.components.mongodb.output.MongoDBOutputConfiguration;
import org.talend.components.mongodb.service.ClientOptionsFactory;
import org.talend.components.mongodb.service.I18nMessage;

public class OutputClientOptionsFactory extends ClientOptionsFactory {

    private final MongoDBOutputConfiguration properties;

    public OutputClientOptionsFactory(final MongoDBOutputConfiguration properties, final I18nMessage i18nMessage) {
        super(properties.getDataset().getDatastore(), i18nMessage);
        this.properties = properties;
    }

    @Override
    protected void setSpecificOptions(final MongoClientOptions.Builder builder) {
        if (properties.isSetWriteConcern() && properties.getWriteConcern() != null) {
            builder.writeConcern(convertWriteConcern(properties.getWriteConcern())).build();
        }
    }

    protected WriteConcern convertWriteConcern(MongoDBOutputConfiguration.WriteConcern writeConcern) {
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
            throw new IllegalArgumentException(i18nMessage.unknownWriteConcern(String.valueOf(writeConcern)));
        }
    }

}
