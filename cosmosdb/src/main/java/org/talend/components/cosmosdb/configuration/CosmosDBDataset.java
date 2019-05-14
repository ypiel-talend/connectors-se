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

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Structure;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
@DataSet("CosmosDBDataset")
@GridLayout({
        // the generated layout put one configuration entry per line,
        // customize it as much as needed
        @GridLayout.Row({ "datastore" }), @GridLayout.Row({ "mongoCollection" }), @GridLayout.Row({ "schema" }) })
@Documentation("Common dataset for MongoDB components")
public class CosmosDBDataset implements Serializable {

    @Option
    @Documentation("CosmosDB Datastore")
    private CosmosDBDatastore datastore;

    @Option
    @ActiveIf(target = "datastore.api", value = "MONGODB")
    @Documentation("Collection name to use")
    private String mongoCollection;

    @Option
    @Structure
    @Documentation("Schema to use")
    private List<String> schema;

}
