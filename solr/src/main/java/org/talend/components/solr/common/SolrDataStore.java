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
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.constraint.Pattern;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@Data
@DataStore("SolrDataStore")
@Checkable("checkSolrConnection")
@Documentation("A connection to Solr server")
@GridLayout({ @GridLayout.Row({ "url" }), @GridLayout.Row({ "login" }), @GridLayout.Row({ "password" }) })
public class SolrDataStore implements Serializable {

    @Option
    @Required
    @Pattern("^(http://|https://).*")
    @Documentation("Solr server connection URL")
    private String url;

    @Option
    @Documentation("Solr server user")
    private String login;

    @Option
    @Credential
    @Documentation("Solr server password")
    private String password;

}
