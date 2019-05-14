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
import org.talend.components.cosmosdb.configuration.CosmosDBDatastore;
import org.talend.components.cosmosdb.configuration.mongoapi.MongoApiConnectionConfiguration;
import org.talend.components.mongodb.service.MongoDBService;
import org.talend.components.mongodb.service.UIMongoDBService;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Suggestions;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UICosmosDBService {

    public static final String HEALTH_CHECK = "HEALTH_CHECK";

    @Service
    private MongoDBService mongoDBService;

    @Service
    private UIMongoDBService uiMongoDBService;

    @Service
    private CosmosDBService cosmosDBService;

    @HealthCheck(HEALTH_CHECK)
    public HealthCheckStatus testConnection(CosmosDBDatastore datastore) {
        if (datastore.getApi() == CosmosDBDatastore.SupportedApi.MONGODB) {
            mongoApiHealthCheck(datastore.getMongoDBDatastore());
        }
        return new HealthCheckStatus(HealthCheckStatus.Status.KO, "Unknown api");
    }

    private HealthCheckStatus mongoApiHealthCheck(MongoApiConnectionConfiguration mongoDBDatastore) {
        return uiMongoDBService.testConnection(cosmosDBService.convertToMongoDatastore(mongoDBDatastore));
    }

    @Suggestions(UIMongoDBService.GET_SCHEMA_FIELDS)
    public SuggestionValues getFields(CosmosDBDataset dataset) {
        if (dataset.getSchema() == null || dataset.getSchema().isEmpty()) {
            return new SuggestionValues(false, Collections.emptyList());
        }
        List<SuggestionValues.Item> items = dataset.getSchema().stream().map(s -> new SuggestionValues.Item(s, s))
                .collect(Collectors.toList());
        return new SuggestionValues(false, items);
    }

}
