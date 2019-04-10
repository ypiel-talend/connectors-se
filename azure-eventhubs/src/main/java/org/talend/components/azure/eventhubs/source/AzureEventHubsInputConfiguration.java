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

package org.talend.components.azure.eventhubs.source;

import java.io.Serializable;

import org.talend.components.azure.eventhubs.dataset.AzureEventHubsDataSet;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@GridLayout({ @GridLayout.Row({ "dataset" }), @GridLayout.Row({ "groupId" }), @GridLayout.Row({ "partitionId" }),
        @GridLayout.Row({ "receiverOptions" }), @GridLayout.Row({ "offset" }),
        @GridLayout.Row({ "sequenceNum", "inclusiveFlag" }), @GridLayout.Row({ "enqueuedDateTime" }) })
@Documentation("TODO fill the documentation for this configuration")
public class AzureEventHubsInputConfiguration implements Serializable {

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private AzureEventHubsDataSet dataset;

    @Option
    @Documentation("Consumer group ID to fetch or create offsets for.")
    private String groupId;

    @Option
    @Suggestable(value = "listPartitionIds", parameters = { "dataset" })
    @Documentation("TODO fill the documentation for this parameter")
    String partitionId;

    @Option
    @Documentation("If offsets don't already exist, where to start reading in the topic.")
    private ReceiverOptions receiverOptions = ReceiverOptions.OFFSET;

    @Option
    @ActiveIf(target = "receiverOptions", value = "OFFSET")
    @Documentation("")
    private String offset;

    @Option
    @ActiveIf(target = "receiverOptions", value = "SEQUENCE")
    @Documentation("the sequence number of the event")
    private Long sequenceNum = 0L;

    @Option
    @ActiveIf(target = "receiverOptions", value = "SEQUENCE")
    @Documentation("will include the specified event when set to true; otherwise, the next event is returned")
    private boolean inclusiveFlag;

    @Option
    @ActiveIf(target = "receiverOptions", value = "DATETIME")
    @Documentation("dateTime is the enqueued time of the event")
    private String enqueuedDateTime;

    public enum ReceiverOptions {
        OFFSET,
        SEQUENCE,
        DATETIME
    }

}