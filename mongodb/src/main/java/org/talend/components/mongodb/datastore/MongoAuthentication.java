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

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Proposable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.condition.ActiveIfs;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

import lombok.Data;

@Data
@GridLayout({ @GridLayout.Row({ "authenticationMechanism" }), @GridLayout.Row({ "kerberosCreds" }),
        @GridLayout.Row({ "userPassConfiguration" }), @GridLayout.Row({ "authDatabaseConfig" }) })
public class MongoAuthentication implements Serializable {

    @Option
    @Documentation("Among the mechanisms listed on the Authentication mechanism drop-down list, the NEGOTIATE one is recommended if you are not using Kerberos, because it automatically select the authentication mechanism the most adapted to the MongoDB version you are using.")
    @DefaultValue("NEGOTIATE_MEC")
    private AuthenticationMechanism authenticationMechanism;

    public enum AuthenticationMechanism {
        NEGOTIATE_MEC,
        PLAIN_MEC,
        SCRAMSHA1_MEC,
        KERBEROS_MEC
    }

    @Option
    @ActiveIf(target = "authenticationMechanism", value = { "NEGOTIATE_MEC", "SCRAMSHA1_MEC" })
    @Documentation("MongoDB authentication Database configuration")
    private MongoAuthDatabaseConfiguration authDatabaseConfig;

    @Option
    @ActiveIf(target = "authenticationMechanism", value = { "NEGOTIATE_MEC", "SCRAMSHA1_MEC", "PLAIN_MEC" })
    @Documentation("")
    private MongoUserPassConfiguration userPassConfiguration;

    @Option
    @Credential
    @ActiveIf(target = "authenticationMechanism", value = { "KERBEROS_MEC" })
    @Documentation("Credentials for Kerberos authentication")
    private KerberosCredentials kerberosCreds;
}
