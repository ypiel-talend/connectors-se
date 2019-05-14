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

package org.talend.components.cosmosdb.input;

import org.talend.components.cosmosdb.configuration.CosmosDBDatastore;
import org.talend.components.cosmosdb.configuration.CosmosDBInputConfiguration;
import org.talend.components.cosmosdb.input.emitters.MongoApiInput;
import org.talend.components.cosmosdb.service.CosmosDBService;
import org.talend.components.mongodb.source.MongoDBInputSource;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.*;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import java.io.Serializable;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import static java.util.Collections.singletonList;

@Slf4j
@Version
@Icon(value = Icon.IconType.DATASTORE)
@PartitionMapper(name = "CosmosDBInputMapper")
@Documentation("CosmosDB input")
public class CosmosDBInputMapper implements Serializable {

    private final CosmosDBInputConfiguration configuration;

    private final CosmosDBService service;

    private final RecordBuilderFactory builderFactory;

    public CosmosDBInputMapper(@Option("configuration") CosmosDBInputConfiguration dataset, CosmosDBService service,
            RecordBuilderFactory builderFactory) {
        this.configuration = dataset;
        this.service = service;
        this.builderFactory = builderFactory;
    }

    @Assessor
    public long estimateSize() {
        return 1L;
    }

    @Split
    public List<CosmosDBInputMapper> split(@PartitionSize final long bundles) {
        return singletonList(this);
    }

    @Emitter
    public CosmosDbInputSource createWorker() {
        if (configuration.getMongoConfig().getDatastore().getApi() == CosmosDBDatastore.SupportedApi.MONGODB) {
            MongoDBInputSource inputSource = new MongoDBInputSource(service.convertToMongoInputConfiguration(configuration),
                    service.getMongoDBService(), builderFactory, service.getMongoDBService().getI18nMessage());
            return new CosmosDbInputSource(new MongoApiInput(inputSource));
        }
        throw new IllegalArgumentException("Unknown API");
    }
}
