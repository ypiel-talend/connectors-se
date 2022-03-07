/*
 * Copyright (C) 2006-2022 Talend Inc. - www.talend.com
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
package org.talend.components.jdbc.service;

import lombok.extern.slf4j.Slf4j;
import org.talend.components.jdbc.datastore.JDBCDataStore;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

@Slf4j
@Service
public class JDBCService {

    @HealthCheck("CheckConnection")
    public HealthCheckStatus validateBasicDataStore(@Option final JDBCDataStore datastore) {
        return new HealthCheckStatus(HealthCheckStatus.Status.OK, "success message, TODO, i18n");
    }

}
