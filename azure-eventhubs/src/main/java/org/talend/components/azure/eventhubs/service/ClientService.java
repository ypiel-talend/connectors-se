/**
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.talend.components.azure.eventhubs.service;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import org.talend.components.azure.eventhubs.dataset.AzureEventHubsDataSet;
import org.talend.sdk.component.api.service.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
public class ClientService {
    public AzClient create(final AzureEventHubsDataSet dataSet, final int poolSize) {
        final ConnectionStringBuilder connStr = new ConnectionStringBuilder()//
                .setEndpoint(URI.create(dataSet.getConnection().getEndpoint()));
        connStr.setSasKeyName(dataSet.getConnection().getSasKeyName());
        connStr.setSasKey(dataSet.getConnection().getSasKey());
        connStr.setEventHubName(dataSet.getEventHubName());

        // todo: store pools here? would break bulkhead but allow more scalability, to refine
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(poolSize, new ThreadFactory() {
            private final AtomicInteger idx = new AtomicInteger();

            @Override
            public Thread newThread(final Runnable workerTask) {
                final Thread thread = new Thread(workerTask,
                        ClientService.class.getName() + '-' + idx.incrementAndGet() + "_" + hashCode());
                thread.setDaemon(false);
                thread.setPriority(Thread.NORM_PRIORITY);
                return thread;
            }
        });
        try {
            return new AzClient(
                    executor, EventHubClient.createSync(connStr.toString(), executor));
        } catch (final EventHubException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    public static class AzClient implements AutoCloseable {
        private final ScheduledExecutorService scheduledExecutorService;

        private final EventHubClient client;

        public EventHubClient unwrap() {
            return client;
        }

        @Override
        public void close() {
            if (client != null) {
                try {
                    client.closeSync();
                } catch (final Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            if (scheduledExecutorService != null) {
                scheduledExecutorService.shutdownNow();
                try {
                    if (!scheduledExecutorService.awaitTermination(2, SECONDS)) {
                        log.warn("Waited 2s for executor shutdown, giving up");
                    }
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
