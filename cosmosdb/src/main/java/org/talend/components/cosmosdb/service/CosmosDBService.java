/*
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.talend.components.cosmosdb.service;

import org.talend.components.cosmosdb.configuration.CosmosDBDataset;
import org.talend.components.cosmosdb.configuration.CosmosDBInputConfiguration;
import org.talend.components.cosmosdb.configuration.CosmosDBOutputConfiguration;
import org.talend.components.cosmosdb.configuration.mongoapi.MongoApiAuthentication;
import org.talend.components.cosmosdb.configuration.mongoapi.MongoApiConnectionConfiguration;
import org.talend.components.mongodb.dataset.MongoDBDataset;
import org.talend.components.mongodb.datastore.MongoAuthentication;
import org.talend.components.mongodb.datastore.MongoDBDatastore;
import org.talend.components.mongodb.output.MongoDBOutputConfiguration;
import org.talend.components.mongodb.service.MongoDBService;
import org.talend.components.mongodb.source.MongoDBInputMapperConfiguration;
import org.talend.sdk.component.api.service.Service;

import lombok.Getter;

@Service
@Getter
public class CosmosDBService {

    @Service
    private MongoDBService mongoDBService;

    public MongoDBOutputConfiguration convertToMongoApiConfig(CosmosDBOutputConfiguration cosmosOutputConfig) {
        MongoDBOutputConfiguration mongoApiConfig = new MongoDBOutputConfiguration();
        MongoDBDataset mongoDBDataset = convertToMongoDataset(cosmosOutputConfig.getMongoConfig());
        mongoApiConfig.setDataset(mongoDBDataset);
        mongoApiConfig.setOutputConfigExtension(cosmosOutputConfig.getOutputConfigExtension());
        return mongoApiConfig;
    }

    public MongoDBInputMapperConfiguration convertToMongoInputConfiguration(
            CosmosDBInputConfiguration cosmosDBInputConfiguration) {
        MongoDBInputMapperConfiguration mongoApiConfig = new MongoDBInputMapperConfiguration();
        MongoDBDataset mongoDBDataset = convertToMongoDataset(cosmosDBInputConfiguration.getMongoConfig());
        mongoApiConfig.setDataset(mongoDBDataset);
        mongoApiConfig.setConfigurationExtension(cosmosDBInputConfiguration.getInputConfigExtension());
        return mongoApiConfig;
    }

    public MongoDBDataset convertToMongoDataset(CosmosDBDataset dataset) {
        MongoDBDatastore mongoDatastore = convertToMongoDatastore(dataset.getDatastore().getMongoDBDatastore());
        MongoDBDataset mongoDBDataset = new MongoDBDataset();
        mongoDBDataset.setDatastore(mongoDatastore);
        mongoDBDataset.setSchema(dataset.getSchema());
        mongoDBDataset.setCollection(dataset.getMongoCollection());
        return mongoDBDataset;
    }

    public MongoDBDatastore convertToMongoDatastore(MongoApiConnectionConfiguration mongoApiConnectionConfiguration) {
        MongoDBDatastore datastore = new MongoDBDatastore();

        MongoAuthentication mongoAuthentication = new MongoAuthentication();
        mongoAuthentication.setAuthenticationMechanism(
                toMongoAuthMechanism(mongoApiConnectionConfiguration.getMongoAuthentication().getAuthenticationMechanism()));
        mongoAuthentication
                .setAuthDatabaseConfig(mongoApiConnectionConfiguration.getMongoAuthentication().getAuthDatabaseConfig());
        mongoAuthentication
                .setUserPassConfiguration(mongoApiConnectionConfiguration.getMongoAuthentication().getUserPassConfiguration());

        datastore.setAuthentication(mongoApiConnectionConfiguration.isAuthentication());
        datastore.setMongoAuthentication(mongoAuthentication);
        datastore.setDatabase(mongoApiConnectionConfiguration.getDatabase());
        datastore.setUseSSL(mongoApiConnectionConfiguration.isUseSSL());
        datastore.setUseConnectionString(mongoApiConnectionConfiguration.isUseConnectionString());
        datastore.setConnectionString(mongoApiConnectionConfiguration.getConnectionString());
        datastore.setServerAddress(mongoApiConnectionConfiguration.getServerAddress());

        return datastore;
    }

    public MongoAuthentication.AuthenticationMechanism toMongoAuthMechanism(
            MongoApiAuthentication.AuthenticationMechanism apiAuthMechanism) {
        switch (apiAuthMechanism) {
        case NEGOTIATE_MEC:
            return MongoAuthentication.AuthenticationMechanism.NEGOTIATE_MEC;
        default:
            throw new IllegalArgumentException("Unknown authentication mechanism!");
        }
    }

}
