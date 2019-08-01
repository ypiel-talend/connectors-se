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

import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.talend.components.azure.eventhubs.service.ClientService;
import org.talend.components.azure.eventhubs.service.Messages;
import org.talend.components.azure.eventhubs.service.UiActionService;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.input.Assessor;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.input.PartitionSize;
import org.talend.sdk.component.api.input.Split;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Version
@RequiredArgsConstructor
@Icon(Icon.IconType.DEFAULT)
@PartitionMapper(name = "AzureEventHubsInputMapper")
@Documentation("Mapper to consume mesage from eventhubs")
public class AzureEventHubsInputMapper implements Serializable {

    private static final int ONE_PARTITION_SIZE = 256 * 1024;

    private final AzureEventHubsInputConfiguration configuration;

    private final UiActionService service;

    private final RecordBuilderFactory recordBuilderFactory;

    private final Messages messages;

    private final ClientService clientService;

    private String partition;
    private AzureEventHubsInputMapper next;

    @Assessor
    public long estimateSize() {
        if (configuration.isSpecifyPartitionId()) {
            return 1L;
        }

        try (final ClientService.AzClient client = clientService.create(configuration.getDataset(), 1)) {
            return Stream.of(client.unwrap().getRuntimeInformation().get())
                    .flatMap(partition -> Stream.of(partition.getPartitionIds()))
                    .count() * ONE_PARTITION_SIZE;
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        } catch (final ExecutionException e) {
            throw new IllegalStateException(e.getCause());
        }
    }

    @Split
    public List<AzureEventHubsInputMapper> split(@PartitionSize final long bundles) {
        if (configuration.isSpecifyPartitionId()) {
            return singletonList(this);
        }

        final long numberOfPartitionPerWork = Math.max(1, bundles / ONE_PARTITION_SIZE);
        try (final ClientService.AzClient client = clientService.create(configuration.getDataset(), 1)) {
            final AtomicLong progressionCounter = new AtomicLong(numberOfPartitionPerWork);
            final AtomicInteger groupingKeyGenerator = new AtomicInteger(1);
            return Stream.of(client.unwrap().getRuntimeInformation().get().getPartitionIds())
                    .map(partition -> {
                        final AzureEventHubsInputMapper mapper = new AzureEventHubsInputMapper(configuration, service, recordBuilderFactory, messages, clientService);
                        mapper.partition = partition;
                        return mapper;
                    })
                    .collect(groupingBy(
                            partition -> {
                                if (progressionCounter.addAndGet(ONE_PARTITION_SIZE) > bundles) {
                                    progressionCounter.set(ONE_PARTITION_SIZE);
                                    return groupingKeyGenerator.incrementAndGet();
                                }
                                return groupingKeyGenerator.get();
                            }))
                    .values().stream()
                    .map(this::chain)
                    .collect(toList());
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        } catch (final ExecutionException e) {
            throw new IllegalStateException(e.getCause());
        }
    }

    @Emitter
    public AzureEventHubsSource createWorker() {
        return new AzureEventHubsSource(
                configuration, service, recordBuilderFactory, messages, clientService, partition,
                ofNullable(next).map(AzureEventHubsInputMapper::createWorker).orElse(null));
    }

    private AzureEventHubsInputMapper chain(final List<AzureEventHubsInputMapper> azureEventHubsInputMappers) {
        final Iterator<AzureEventHubsInputMapper> iterator = azureEventHubsInputMappers.iterator();
        AzureEventHubsInputMapper current = iterator.next();
        while (iterator.hasNext()) {
            current.next = iterator.next();
            current = current.next;
        }
        return current;
    }
}