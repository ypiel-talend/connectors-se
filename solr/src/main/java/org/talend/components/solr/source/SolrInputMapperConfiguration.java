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
package org.talend.components.solr.source;

import lombok.Data;
import org.talend.components.solr.common.FilterCriteria;
import org.talend.components.solr.common.SolrDataset;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.constraint.Pattern;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@GridLayout({ @GridLayout.Row({ "dataset" }), @GridLayout.Row({ "filterQuery" }), @GridLayout.Row({ "start" }),
        @GridLayout.Row({ "rows" }) })
@GridLayout(value = { @GridLayout.Row({ "rawQuery" }) }, names = { GridLayout.FormType.ADVANCED })
@Documentation("Configuration for Solr Input component")
public class SolrInputMapperConfiguration implements Serializable {

    @Option
    @Documentation("Solr URL. Including core")
    private SolrDataset dataset;

    @Option
    @Pattern("^[0-9]{0,9}$")
    @Documentation("Start field. Points to a started document")
    private String start = "0";

    @Option
    @Pattern("^[0-9]{0,9}$")
    @Documentation("Rows field. Points to numbers of documents")
    private String rows = "10";

    @Option
    @Documentation("Filter query table. Every row sets a new condition")
    private List<FilterCriteria> filterQuery = new ArrayList<>();

    @Option
    @Documentation("raw query")
    @Suggestable(value = "rawQuery", parameters = { ".." })
    private String rawQuery = "";

}