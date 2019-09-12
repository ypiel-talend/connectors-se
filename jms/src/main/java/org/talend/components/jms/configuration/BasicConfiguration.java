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
package org.talend.components.jms.configuration;

import lombok.Data;
import org.talend.components.jms.datastore.JmsDataStore;
import org.talend.components.jms.source.DurableSubscriptionConfiguration;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@DataSet("JMSDataSet")
@Data
@GridLayout(value = { @GridLayout.Row({ "connection" }), @GridLayout.Row({ "messageType" }), @GridLayout.Row({ "destination" }),
        @GridLayout.Row({ "subscriptionConfig" }) }, names = GridLayout.FormType.MAIN)
public class BasicConfiguration implements Serializable {

    @Option
    @Documentation("JMS connection information")
    private JmsDataStore connection;

    @Option
    @Documentation("Drop down list for Message Type")
    private MessageType messageType = MessageType.TOPIC;

    @Option
    @Required
    @Documentation("Input for TOPIC/QUEUE Name")
    private String destination;

    @Option
    @Documentation("Durable subscription configuration")
    @ActiveIf(target = "messageType", value = "TOPIC")
    private DurableSubscriptionConfiguration subscriptionConfig;

}
