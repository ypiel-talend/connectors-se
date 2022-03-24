/*
 * Copyright (C) 2006-2022 Talend Inc. - www.talend.com
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
package org.talend.components.couchbase.datastore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.talend.components.couchbase.configuration.ConnectionConfiguration;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Version(value = 3, migrationHandler = CouchbaseDataStoreMigrationHandler.class)
@Data
@DataStore("CouchbaseDataStore")
@Checkable("healthCheck")
@GridLayout(names = GridLayout.FormType.MAIN,
        value = { @GridLayout.Row({ "bootstrapNodes" }), @GridLayout.Row({ "username" }),
                @GridLayout.Row({ "password" }) })
@GridLayout(names = GridLayout.FormType.ADVANCED, value = { @GridLayout.Row({ "useConnectionParameters" }),
        @GridLayout.Row({ "connectionParametersList" }) })
@Documentation("Couchbase connection.")
public class CouchbaseDataStore implements Serializable {

    @Option
    @Required
    @Documentation("Bootstrap nodes.")
    private String bootstrapNodes;

    @Option
    @Required
    @Documentation("Username.")
    private String username;

    @Option
    @Required
    @Credential
    @Documentation("Password.")
    private String password;

    @Option
    @Documentation("Define custom connection parameters.")
    private boolean useConnectionParameters = false;

    @Option
    @Documentation("List of defined connection parameters.")
    @ActiveIf(target = "useConnectionParameters", value = "true")
    private List<ConnectionConfiguration> connectionParametersList = new ArrayList<>();

}