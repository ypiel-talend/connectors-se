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
package org.talend.components.docdb.datastore;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import lombok.Data;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@Version(1)
@Data
@Icon(value = Icon.IconType.CUSTOM, custom = "mongo_db-connector")
@DataStore("DocDBDataStore")
@Checkable("healthCheck")
@GridLayout(names = GridLayout.FormType.MAIN, value = {
        @GridLayout.Row({ "addressType" }),
        @GridLayout.Row({ "address" }),
        @GridLayout.Row({ "replicaSetAddress" }),
        @GridLayout.Row({ "dataBase" }),
        @GridLayout.Row({ "auth" })
})
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {
        @GridLayout.Row({ "connectionParameter" })
})
@Documentation("AWS DocDB connection")
public class DocDBDataStore implements Serializable {

    @Option
    @Documentation("Address type")
    private AddressType addressType = AddressType.STANDALONE;

    @Option
    @ActiveIf(target = "addressType", value = "STANDALONE")
    @Documentation("Address")
    private Address address;

    @Option
    @ActiveIf(target = "addressType", value = "REPLICA_SET")
    @Documentation("Replica set")
    private List<Address> replicaSetAddress = Collections.emptyList();

    @Option
    @Required
    @Documentation("database")
    private String dataBase;

    @Option
    @Documentation("auth page")
    private Auth auth;

    // advanced page
    @Option
    @Documentation("Connection parameter")
    private List<ConnectionParameter> connectionParameter = Collections.emptyList();
}