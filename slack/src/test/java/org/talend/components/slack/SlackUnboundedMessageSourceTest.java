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
package org.talend.components.slack;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.talend.components.slack.connection.SlackConnection;
import org.talend.components.slack.dataset.SlackUnboundedMessageDataset;
import org.talend.components.slack.input.SlackUnboundedMessageInputConfiguration;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit.SimpleFactory;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.manager.chain.Job;

@Disabled("Streaming manually run")
@WithComponents("org.talend.components.slack")
public class SlackUnboundedMessageSourceTest extends SlackTestBase {

    @Injected
    private BaseComponentsHandler componentsHandler;

    @Test
    void sourceTest() {
        final SlackConnection connection = new SlackConnection();
        connection.setToken(TOKEN);

        final SlackUnboundedMessageDataset dataset = new SlackUnboundedMessageDataset();
        dataset.setConnection(connection);
        // dataset.setChannel("GMTL6QVD1");
        dataset.setChannel("GNCSSN7EX");
        final SlackUnboundedMessageInputConfiguration inputConfiguration = new SlackUnboundedMessageInputConfiguration();
        inputConfiguration.setDataset(dataset);

        final String uriConfig = SimpleFactory.configurationByExample().forInstance(inputConfiguration).configured()
                .toQueryString();
        Job.components().component("Input", "Slack://SlackInput?" + uriConfig).component("collector", "test://collector")
                .connections().from("Input").to("collector").build().run();

        // List<Record> collectedData = componentsHandler.getCollectedData(Record.class);
        // collectedData.stream().forEach(System.out::println);

    }
}
