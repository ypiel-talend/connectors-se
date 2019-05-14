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

package org.talend.components.mongodb.datastore;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import org.talend.components.mongodb.service.UIMongoDBService;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.condition.ActiveIfs;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@DataStore("MongoDBDatastore")
@Checkable(UIMongoDBService.HEALTH_CHECK)
@GridLayout({ @GridLayout.Row({ "useConnectionString" }), @GridLayout.Row({ "connectionString" }),
        @GridLayout.Row({ "useReplicaSetAddress" }), @GridLayout.Row({ "replicaAddresses" }),
        @GridLayout.Row({ "serverAddress" }), @GridLayout.Row({ "database" }), @GridLayout.Row({ "useSSL" }),
        @GridLayout.Row({ "authentication" }), @GridLayout.Row({ "mongoAuthentication" }) })
@Documentation("Connection for MongoDB components")
public class MongoDBDatastore implements Serializable {

    @Option
    @Documentation("Use connection string")
    @DefaultValue("false")
    private boolean useConnectionString;

    @Option
    @Documentation("Database connection string")
    @DefaultValue("mongodb://")
    @ActiveIf(target = "useConnectionString", value = "true")
    private String connectionString;

    @Option
    @Documentation("Use replica addresses instead of single server")
    @ActiveIf(target = "useConnectionString", value = "false")
    private boolean useReplicaSetAddress;

    @Option
    @Documentation("Replica addresses set")
    @ActiveIfs({ @ActiveIf(target = "useReplicaSetAddress", value = "true"),
            @ActiveIf(target = "useConnectionString", value = "false") })
    private List<MongoServerAddress> replicaAddresses;

    @Option
    @Documentation("Server address")
    @ActiveIfs({ @ActiveIf(target = "useReplicaSetAddress", value = "false"),
            @ActiveIf(target = "useConnectionString", value = "false") })
    private MongoServerAddress serverAddress;

    @Option
    @Documentation("Database to use")
    @ActiveIf(target = "useConnectionString", value = "false")
    private String database;

    @Option
    @Documentation("Enable the SSL or TLS encrypted connection.")
    private boolean useSSL;

    @Option
    @Documentation("Enable the database authentication.")
    @ActiveIf(target = "useConnectionString", value = "false")
    private boolean authentication;

    @Option
    @Documentation("Authentication configuration")
    @ActiveIfs({ @ActiveIf(target = "authentication", value = "true"),
            @ActiveIf(target = "useConnectionString", value = "false") })
    private MongoAuthentication mongoAuthentication;

}