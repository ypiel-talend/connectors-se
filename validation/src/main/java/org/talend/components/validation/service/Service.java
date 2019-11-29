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
package org.talend.components.validation.service;

import org.talend.components.validation.configuration.Datastore;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

@org.talend.sdk.component.api.service.Service
public class Service {

    public final static String HEALTHCHECK = "HEALTHCHECK";

    private final static String withQuotes = "There are ' some ' quotes !";

    @org.talend.sdk.component.api.service.Service
    private I18n i18n;

    @HealthCheck(HEALTHCHECK)
    public HealthCheckStatus healthCheck(@Option final Datastore conf) {
        if ("fail".equals(conf.getStringParam())) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, i18n.failHealthCheck(withQuotes));
        }
        return new HealthCheckStatus(HealthCheckStatus.Status.OK, i18n.successHealthCheck(withQuotes));
    }

}
