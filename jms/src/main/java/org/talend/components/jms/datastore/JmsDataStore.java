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
package org.talend.components.jms.datastore;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.action.Proposable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Pattern;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

import static org.talend.components.jms.service.ActionService.ACTION_BASIC_HEALTH_CHECK;
import static org.talend.components.jms.service.ActionService.ACTION_LIST_SUPPORTED_BROKER;

@Data
@GridLayout({ @GridLayout.Row({ "moduleList" }), @GridLayout.Row("url"), @GridLayout.Row("userIdentity"),
        @GridLayout.Row({ "userName", "password" }) })
@DataStore("basic")
@Checkable(ACTION_BASIC_HEALTH_CHECK)
@Documentation("A connection to a data base")
public class JmsDataStore implements Serializable {

    @Option
    @Required
    @Documentation("Data type from the supported jms providers list")
    @Proposable(ACTION_LIST_SUPPORTED_BROKER)
    private String moduleList;

    @Option
    @Required
    @Pattern("^(tcp|ssl)://")
    @Documentation("Input for JMS server URL")
    private String url;

    @Option
    @Documentation("Checkbox for User login/password checking")
    private boolean userIdentity = true;

    @Option
    @Documentation("Input for User Name")
    @ActiveIf(target = "userIdentity", value = "true")
    private String userName;

    @Option
    @Credential
    @Documentation("Input for password")
    @ActiveIf(target = "userIdentity", value = "true")
    private String password;

}
