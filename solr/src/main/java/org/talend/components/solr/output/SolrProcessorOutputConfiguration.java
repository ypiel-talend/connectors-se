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
package org.talend.components.solr.output;

import lombok.Data;
import org.talend.components.solr.common.SolrDataset;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@Data
@GridLayout({ @GridLayout.Row({ "dataset" }), @GridLayout.Row({ "action" }) })
@Documentation("Configuration for Solr Output component")
public class SolrProcessorOutputConfiguration implements Serializable {

    @Option
    @Documentation("Connection to Solr Data Collection")
    private SolrDataset dataset;

    @Option
    @Documentation("Solr Action. Allows to choose an action to add or to delete a document from Solr collection")
    private SolrAction action = SolrAction.UPSERT;

}