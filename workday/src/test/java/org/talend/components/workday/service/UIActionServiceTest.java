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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.talend.components.workday.WorkdayBaseTest;
import org.talend.components.workday.datastore.WorkdayDataStore;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.junit.http.junit5.HttpApi;
import org.talend.sdk.component.junit5.WithComponents;

@HttpApi(useSsl = true)
@WithComponents("org.talend.components.workday.service")
class UIActionServiceTest extends WorkdayBaseTest {

    @Service
    private UIActionService service;

    @Service
    private AccessTokenProvider client;

    @Test
    void validateConnection() {
        final HealthCheckStatus healthCheckStatus = service.validateConnection(this.buildDataStore(), client);
        Assertions.assertNotNull(healthCheckStatus);
        Assertions.assertEquals(HealthCheckStatus.Status.OK, healthCheckStatus.getStatus());

    }

    @Test
    void validateConnectionKO() {
        final WorkdayDataStore wds = this.buildDataStore();
        wds.setClientSecret("FAUX");
        final HealthCheckStatus healthCheckStatusKO = service.validateConnection(wds, client);
        Assertions.assertNotNull(healthCheckStatusKO);
        Assertions.assertEquals(HealthCheckStatus.Status.KO, healthCheckStatusKO.getStatus());
    }
}