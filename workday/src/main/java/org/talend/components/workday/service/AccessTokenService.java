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
package org.talend.components.workday.service;

import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PreDestroy;

import org.slf4j.LoggerFactory;
import org.talend.components.workday.WorkdayException;
import org.talend.components.workday.datastore.Token;
import org.talend.components.workday.datastore.WorkdayDataStore;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.cache.LocalCache;
import org.talend.sdk.component.api.service.http.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccessTokenService {

    @Service
    private LocalCache cache;

    private final ConcurrentMap<WorkdayDataStore, Token> tokens = new ConcurrentHashMap<>();

    // scheduler we use to evict tokens
    private volatile ScheduledExecutorService evictionThread;

    // the eviction task if active
    private volatile ScheduledFuture<?> evictionTask;

    // here to avoid to stop eviction while creating a token
    private final AtomicInteger pendingRequests = new AtomicInteger();

    @PreDestroy
    public synchronized void release() {
        stopEviction();
    }

    public Token getOrCreateToken(final WorkdayDataStore datastore, final AccessTokenProvider client) {
        // todo add remove in LocaleCache in T.C.K. and drop that concurrent map
        pendingRequests.incrementAndGet();
        try {
            final ConcurrentMap<WorkdayDataStore, Token> cache = getTokens();
            final Token token = cache.computeIfAbsent(datastore, d -> getAccessToken(d, client));
            if (token.getExpireDate().isBefore(Instant.now().minus(30, ChronoUnit.SECONDS))) { // is expired
                cache.remove(datastore);
                return getOrCreateToken(datastore, client);
            }
            return token;
        } finally {
            pendingRequests.decrementAndGet();
        }
    }

    public Token getAccessToken(final WorkdayDataStore ds, final AccessTokenProvider client) {
        client.base(ds.getAuthEndpoint());

        final String payload = "tenant_alias=" + ds.getTenantAlias() + "&grant_type=client_credentials";
        final Response<Token> result = client.getAuthorizationToken(ds.getAuthorizationHeader(), payload);
        if (result.status() / 100 != 2) {
            final String errorLib = result.error(String.class);
            LoggerFactory.getLogger(AccessTokenProvider.class).error("Error while trying get token : HTTP {} : {}",
                    result.status(), errorLib);
            throw new WorkdayException(errorLib);
        }
        final Token json = result.body();
        json.setExpireDate(Instant.now().plus(Integer.parseInt(json.getExpiresIn()), ChronoUnit.SECONDS));
        return json;
    }

    private void stopEviction() {
        if (evictionTask != null) {
            evictionTask.cancel(true);
            evictionThread.shutdownNow();
            try {
                evictionThread.awaitTermination(2, SECONDS); // not a big deal if it does not end completely here
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private ConcurrentMap<WorkdayDataStore, Token> getTokens() {
        if (evictionTask == null) {
            synchronized (this) {
                if (evictionTask == null) {
                    evictionThread = Executors.newSingleThreadScheduledExecutor(r -> {
                        final Thread thread = new Thread(r, AccessTokenService.class.getName() + "-eviction-" + hashCode());
                        thread.setPriority(Thread.NORM_PRIORITY);
                        return thread;
                    });
                    evictionTask = evictionThread.schedule(this::evict, 30, SECONDS);
                }
            }
        }
        return tokens;
    }

    private void evict() {
        final Instant now = Instant.now();
        tokens.entrySet().stream().filter(it -> it.getValue().getExpireDate().isAfter(now)).map(Map.Entry::getKey)
                .collect(toList()) // materialize before actually removing it
                .forEach(tokens::remove);
        if (tokens.isEmpty() && pendingRequests.get() == 0) { // try to stop the useless thread
            synchronized (this) {
                if (tokens.isEmpty() && pendingRequests.get() == 0) {
                    stopEviction();
                }
            }
        }
    }
}
