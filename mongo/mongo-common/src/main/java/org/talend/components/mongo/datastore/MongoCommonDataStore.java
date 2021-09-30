/*
 * Copyright (C) 2006-2021 Talend Inc. - www.talend.com
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
package org.talend.components.mongo.datastore;

import lombok.Data;
import org.talend.components.mongo.Address;
import org.talend.components.mongo.AddressType;
import org.talend.components.mongo.Auth;
import org.talend.components.mongo.ConnectionParameter;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@GridLayout(names = GridLayout.FormType.MAIN,
        value = { @GridLayout.Row({ "addressType" }), @GridLayout.Row({ "address" }),
                @GridLayout.Row({ "replicaSetAddress" }), @GridLayout.Row({ "database" }),
                @GridLayout.Row({ "auth" }) })
@GridLayout(names = GridLayout.FormType.ADVANCED, value = { @GridLayout.Row({ "connectionParameter" }) })
@Documentation("MongoDB connection")
public class MongoCommonDataStore implements Serializable {

    @Option
    @Required
    @Documentation("https://docs.mongodb.com/manual/reference/connection-string/")
    private AddressType addressType = AddressType.STANDALONE;

    @Option
    @ActiveIf(target = "addressType", value = "STANDALONE")
    @Documentation("https://docs.mongodb.com/manual/reference/connection-string/")
    private Address address;

    @Option
    @ActiveIf(target = "addressType", value = "REPLICA_SET")
    @Documentation("https://docs.mongodb.com/manual/reference/connection-string/")
    private List<Address> replicaSetAddress = Collections.emptyList();

    @Option
    @Required
    @Documentation("Database")
    private String database;

    @Option
    @Documentation("auth page")
    private Auth auth;

    // advanced page
    @Option
    @Documentation("Connection parameter")
    private List<ConnectionParameter> connectionParameter = Collections.emptyList();
}