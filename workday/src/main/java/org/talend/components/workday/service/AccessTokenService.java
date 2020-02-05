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

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.slf4j.LoggerFactory;
import org.talend.components.workday.WorkdayException;
import org.talend.components.workday.datastore.Token;
import org.talend.components.workday.datastore.WorkdayDataStore;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.cache.Cached;
import org.talend.sdk.component.api.service.http.Response;

@Service
public class AccessTokenService {

    @Cached(timeout = 30000)
    public Token getOrCreateToken(final WorkdayDataStore datastore, final AccessTokenProvider service) {
        return getAccessToken(datastore, service);
    }

    public Token getAccessToken(final WorkdayDataStore ds, final AccessTokenProvider service) {
        service.base(ds.getAuthEndpoint());

        final String payload = "tenant_alias=" + ds.getTenantAlias() + "&grant_type=client_credentials";
        final Response<Token> result = service.getAuthorizationToken(ds.getAuthorizationHeader(), payload);
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
}
