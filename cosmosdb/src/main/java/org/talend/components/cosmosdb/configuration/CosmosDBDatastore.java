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

package org.talend.components.cosmosdb.configuration;

import org.talend.components.cosmosdb.configuration.mongoapi.MongoApiConnectionConfiguration;
import org.talend.components.cosmosdb.service.UICosmosDBService;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayouts;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

import lombok.Data;

@Data
@DataStore("CosmosDBDatastore")
@Checkable(UICosmosDBService.HEALTH_CHECK)
@GridLayouts({
        @GridLayout(value = { @GridLayout.Row({ "api" }),
                @GridLayout.Row({ "mongoDBDatastore" }) }, names = GridLayout.FormType.MAIN),
        @GridLayout(value = { @GridLayout.Row({ "mongoDBDatastore" }) }, names = GridLayout.FormType.ADVANCED) })
@Documentation("Connection for MongoDB components")
public class CosmosDBDatastore implements Serializable {

    public enum SupportedApi {
        MONGODB;
    }

    @Option
    @DefaultValue("MONGODB")
    @Documentation("")
    private SupportedApi api;

    @Option
    @ActiveIf(target = "api", value = "MONGODB")
    @Documentation("MongoDB Api datastore")
    private MongoApiConnectionConfiguration mongoDBDatastore;

}
