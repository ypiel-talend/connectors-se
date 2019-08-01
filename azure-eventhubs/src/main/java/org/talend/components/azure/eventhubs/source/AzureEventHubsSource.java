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

import static java.util.Optional.ofNullable;
import static org.talend.components.azure.eventhubs.common.AzureEventHubsConstant.DEFAULT_CHARSET;
import static org.talend.components.azure.eventhubs.common.AzureEventHubsConstant.PAYLOAD_COLUMN;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import javax.annotation.PreDestroy;

import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import com.microsoft.azure.eventhubs.EventPosition;
import com.microsoft.azure.eventhubs.PartitionReceiver;
import com.microsoft.azure.eventhubs.PartitionRuntimeInformation;
import org.talend.components.azure.eventhubs.service.ClientService;
import org.talend.components.azure.eventhubs.service.Messages;
import org.talend.components.azure.eventhubs.service.UiActionService;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Documentation("Source to consume eventhubs messages")
public class AzureEventHubsSource implements Serializable {

    private final AzureEventHubsInputConfiguration configuration;

    private final UiActionService service;

    private final RecordBuilderFactory builderFactory;

    private final Messages messages;

    private final ClientService clientService;

    private final String partitionId;

    private final AzureEventHubsSource next;

    private transient State state;

    @Producer
    public Record next() {
        if (configuration.isUseMaxNum() && configuration.getMaxNumReceived() <= 0) {
            return null;
        }
        if (state == null) {
            state = new State(clientService.create(configuration.getDataset(), 8));
            try {
                state.receiverManager = new ReceiverManager(
                        state.client.unwrap(),
                        getPosition(state.client.unwrap().getPartitionRuntimeInformation(partitionId).get()));
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (final ExecutionException e) {
                throw new IllegalStateException(e.getCause());
            }
        }
        if (configuration.isUseMaxNum() && state.count >= configuration.getMaxNumReceived()) {
            return null;
        }

        while (true) {
            try {
                if (state.needsMessages()) {
                    log.debug("fetch messages...");
                    state.load();
                }
                if (state.receivedEvents.hasNext()) {
                    final EventData eventData = state.receivedEvents.next();
                    if (eventData != null) {
                        final Record.Builder recordBuilder = mapEvent(eventData);
                        state.count++;
                        return recordBuilder.build();
                    }
                } else if (next != null) {
                    final Record next = this.next.next();
                    if (next != null) {
                        return next;
                    }
                }
            } catch (final Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

    @PreDestroy
    public void release() {
        if (state == null) {
            return;
        }
        state.close();
        if (next != null) {
            next.release();
        }
    }

    private Record.Builder mapEvent(final EventData eventData) {
        final Record.Builder recordBuilder = builderFactory.newRecordBuilder();
        recordBuilder.withString(PAYLOAD_COLUMN, new String(eventData.getBytes(), DEFAULT_CHARSET));
        return recordBuilder;
    }

    private EventPosition getPosition(final PartitionRuntimeInformation partitionRuntimeInfo) {
        if (AzureEventHubsInputConfiguration.ReceiverOptions.OFFSET.equals(configuration.getReceiverOptions())) {
            if (AzureEventHubsInputConfiguration.EventOffsetPosition.START_OF_STREAM.equals(configuration.getOffset())) {
                return EventPosition.fromStartOfStream();
            }
            return EventPosition.fromEndOfStream();
        }
        if (AzureEventHubsInputConfiguration.ReceiverOptions.SEQUENCE.equals(configuration.getReceiverOptions())) {
            if (configuration.getSequenceNum() > partitionRuntimeInfo.getLastEnqueuedSequenceNumber()) {
                throw new IllegalArgumentException(messages.errorWrongSequenceNumber(configuration.getSequenceNum(),
                        partitionRuntimeInfo.getLastEnqueuedSequenceNumber()));
            }
            return EventPosition.fromSequenceNumber(configuration.getSequenceNum(), configuration.isInclusiveFlag());
        }
        if (AzureEventHubsInputConfiguration.ReceiverOptions.DATETIME.equals(configuration.getReceiverOptions())) {
            final Instant enqueuedDateTime;
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

    private class ReceiverManager implements AutoCloseable {
        private final PartitionReceiver activedReceiver;

        private ReceiverManager(final EventHubClient client, final EventPosition position) {
            try {
                activedReceiver = client.createEpochReceiverSync(
                        configuration.getConsumerGroupName(), partitionId,
                        position, Integer.MAX_VALUE);
                activedReceiver.setReceiveTimeout(Duration.ofSeconds(configuration.getReceiveTimeout()));
            } catch (final EventHubException | IllegalArgumentException ehe) {
                throw new IllegalStateException(ehe);
            }
        }

        @Override
        public void close() {
            if (activedReceiver != null) {
                try {
                    activedReceiver.closeSync();
                } catch (final EventHubException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

    }

    @RequiredArgsConstructor
    private class State implements AutoCloseable {
        private final ClientService.AzClient client;

        private ReceiverManager receiverManager;
        private Iterator<EventData> receivedEvents;
        private long count;

        @Override
        public void close() {
            if (receiverManager != null) {
                receiverManager.close();
            }
            client.close();
        }

        private boolean needsMessages() {
            return receivedEvents == null || !receivedEvents.hasNext();
        }

        private void load() throws EventHubException {
            while (true) {
                receivedEvents = ofNullable(receiverManager.activedReceiver.receiveSync(100))
                        .map(Iterable::iterator)
                        .orElse(null);
                if (receivedEvents != null) {
                    break;
                }
                receiverManager.activedReceiver.closeSync();
                receiverManager = new ReceiverManager(client.unwrap(), receiverManager.activedReceiver.getEventPosition());
            }
        }
    }
}