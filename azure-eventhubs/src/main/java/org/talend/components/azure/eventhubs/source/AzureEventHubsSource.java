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
import java.net.URI;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.talend.components.azure.eventhubs.service.UiActionService;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import com.microsoft.azure.eventhubs.EventHubRuntimeInformation;
import com.microsoft.azure.eventhubs.EventPosition;
import com.microsoft.azure.eventhubs.PartitionReceiver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Documentation("TODO fill the documentation for this source")
public class AzureEventHubsSource implements Serializable {

    private final AzureEventHubsInputConfiguration configuration;

    private final UiActionService service;

    private final RecordBuilderFactory builderFactory;

    private PartitionReceiver receiver;

    private ScheduledExecutorService executorService;

    private Iterator<EventData> receivedEvents;

    private EventHubClient ehClient;

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private static final String PAYLOAD_COLUMN = "payload";

    public AzureEventHubsSource(@Option("configuration") final AzureEventHubsInputConfiguration configuration,
            final UiActionService service, final RecordBuilderFactory builderFactory) {
        this.configuration = configuration;
        this.service = service;
        this.builderFactory = builderFactory;
    }

    @PostConstruct
    public void init() {
        try {
            executorService = Executors.newScheduledThreadPool(4);
            final ConnectionStringBuilder connStr;//
            connStr = new ConnectionStringBuilder()//
                    .setEndpoint(new URI(configuration.getDataset().getDatastore().getEndpoint()));
            connStr.setSasKeyName(configuration.getDataset().getDatastore().getSasKeyName());
            connStr.setSasKey(configuration.getDataset().getDatastore().getSasKey());
            connStr.setEventHubName(configuration.getDataset().getEventHubName());
            ehClient = EventHubClient.createSync(connStr.toString(), executorService);

            receiver = ehClient.createReceiverSync(configuration.getGroupId(), configuration.getPartitionId(), getPosition());
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

    }

    @Producer
    public Record next() {
        try {
            if (receivedEvents == null || !receivedEvents.hasNext()) {
                Iterable iterable = receiver.receiveSync(100);
                if (iterable == null) {
                    return null;
                } else {
                    receivedEvents = iterable.iterator();
                }
            }
            EventData eventData = receivedEvents.next();
            if (eventData != null) {
                Record.Builder recordBuilder = builderFactory.newRecordBuilder();
                log.info(new String(eventData.getBytes(), DEFAULT_CHARSET));
                recordBuilder.withString(PAYLOAD_COLUMN, new String(eventData.getBytes(), DEFAULT_CHARSET));
                return recordBuilder.build();
            }
        } catch (EventHubException e) {
            e.printStackTrace();
        }
        return null;
    }

    @PreDestroy
    public void release() {
        // cleaning up receivers is paramount;
        // Quota limitation on maximum number of concurrent receivers per consumergroup per partition is 5
        try {
            receiver.close().thenComposeAsync(aVoid -> ehClient.close(), executorService).whenCompleteAsync((t, u) -> {
                if (u != null) {
                    log.warn("closing failed with error:", u.toString());
                }
            }, executorService).get();

            executorService.shutdown();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private EventPosition getPosition() {
        if (AzureEventHubsInputConfiguration.ReceiverOptions.OFFSET.equals(configuration.getReceiverOptions())) {
            return EventPosition.fromOffset(configuration.getOffset());
        }
        if (AzureEventHubsInputConfiguration.ReceiverOptions.SEQUENCE.equals(configuration.getReceiverOptions())) {
            return EventPosition.fromSequenceNumber(configuration.getSequenceNum(), configuration.isInclusiveFlag());
        }
        if (AzureEventHubsInputConfiguration.ReceiverOptions.DATETIME.equals(configuration.getReceiverOptions())) {
            Instant enqueuedDateTime = null;
            if (configuration.getEnqueuedDateTime() == null) {
                // default query from now
                enqueuedDateTime = Instant.now();
            } else {
                enqueuedDateTime = Instant.parse(configuration.getEnqueuedDateTime());
            }
            return EventPosition.fromEnqueuedTime(enqueuedDateTime);
        }
        return EventPosition.fromStartOfStream();
    }
}