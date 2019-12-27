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
package org.talend.components.solr.common;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@Data
@DataSet("SolrDataSet")
@GridLayout({ @GridLayout.Row({ "dataStore" }), @GridLayout.Row({ "core" }) })
@Documentation("Solr dataSet. Provide connection to Solr Data Collection")

public class SolrDataset implements Serializable {

    @Option
    @Documentation("Solr dataStore. Connection for Solr server")
    private SolrDataStore dataStore;

    @Option
    @Required
    @Documentation("List of Solr data collection")
    @Suggestable(value = "coreList", parameters = { "dataStore" })
    private String core;

    public String getFullUrl() {
        String solrUrl = dataStore.getUrl();
        boolean addSlash = !solrUrl.endsWith("/") && !solrUrl.endsWith("\\");
        return (addSlash ? solrUrl + "/" : solrUrl) + core;
    }

    // @Option
    // @Structure(type = Structure.Type.OUT, discoverSchema = "discoverSchema")
    // @Documentation("Schema of a Solr Document")
    // private List<String> schema;
}
