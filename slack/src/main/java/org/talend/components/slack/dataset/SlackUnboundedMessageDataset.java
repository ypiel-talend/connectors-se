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
package org.talend.components.slack.dataset;

import lombok.Data;
import lombok.ToString;
import org.talend.components.slack.connection.SlackConnection;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@DataSet("SlackUnboundedDataset")
@Documentation("Slack Unbounded Dataset")
@ToString
@GridLayout({ @GridLayout.Row("connection"), //
        @GridLayout.Row("channelType"), //
        @GridLayout.Row("channel") //
})
public class SlackUnboundedMessageDataset implements SlackDataset {

    @Option
    @Documentation("Connection")
    private SlackConnection connection;

    @Option
    @Documentation("Channel type")
    private ChannelType channelType = ChannelType.PRIVATE_CHANNEL;

    @Option
    @Suggestable(value = "listChannelNames", parameters = { "connection", "channelType" })
    @Documentation("Channel")
    private String channel;

    public enum ChannelType {
        PUBLIC_CHANNEL("public_channel"),
        PRIVATE_CHANNEL("private_channel");
        // MULTIPLE_PERSON_IM("mpim"),
        // IM("im");

        ChannelType(String value) {
            this.value = value;
        }

        private String value;

        public String getValue() {
            return value;
        }
    }

}
