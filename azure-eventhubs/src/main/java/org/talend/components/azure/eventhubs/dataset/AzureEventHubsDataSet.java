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
 *
 */

package org.talend.components.azure.eventhubs.dataset;

import java.io.Serializable;

import org.talend.components.azure.eventhubs.datastore.AzureEventHubsDataStore;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.action.Validable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@DataSet("AzureEventHubsDataSet")
@GridLayout({ @GridLayout.Row({ "datastore" }), @GridLayout.Row({ "eventHubName" }), @GridLayout.Row({ "consumerGroupName" }),
        @GridLayout.Row({ "partitionId" }), @GridLayout.Row({ "receiverOptions" }), @GridLayout.Row({ "offset" }),
        @GridLayout.Row({ "sequenceNum", "inclusiveFlag" }), @GridLayout.Row({ "enqueuedDateTime" }) })
@Documentation("The dataset consume message in eventhubs")
public class AzureEventHubsDataSet implements Serializable {

    @Option
    @Documentation("Connection information to eventhubs")
    private AzureEventHubsDataStore datastore;

    @Option
    @Required
    @Validable(value = "checkEventHub", parameters = { "datastore", "." })
    @Documentation("The name of the event hub connect to")
    private String eventHubName;

    @Option
    @Required
    @Documentation("The consumer group name that this receiver should be grouped under")
    private String consumerGroupName;

    @Option
    @Required
    @Suggestable(value = "listPartitionIds", parameters = { "datastore", "eventHubName" })
    @Documentation("The partition Id that the receiver belongs to. All data received will be from this partition only")
    String partitionId;

    @Option
    @Required
    @Documentation("If offsets don't already exist, where to start reading in the topic.")
    private ReceiverOptions receiverOptions = ReceiverOptions.OFFSET;

    @Option
    @DefaultValue("-1")
    @ActiveIf(target = "receiverOptions", value = "OFFSET")
    @Documentation("The byte offset of the event.\n" + " \"-1\" is the start of a partition stream in EventHub.\n"
            + "\"@latest\" current end of a partition stream in EventHub")
    private String offset;

    @Option
    @DefaultValue("0")
    @ActiveIf(target = "receiverOptions", value = "SEQUENCE")
    @Documentation("The sequence number of the event")
    private Long sequenceNum;

    @Option
    @ActiveIf(target = "receiverOptions", value = { "OFFSET", "SEQUENCE" })
    @Documentation("Will include the specified event when set to true; otherwise, the next event is returned")
    private boolean inclusiveFlag;

    @Option
    @ActiveIf(target = "receiverOptions", value = "DATETIME")
    @Documentation("DateTime is the enqueued time of the event")
    private String enqueuedDateTime;

    public enum ReceiverOptions {
        OFFSET,
        SEQUENCE,
        DATETIME
    }
}