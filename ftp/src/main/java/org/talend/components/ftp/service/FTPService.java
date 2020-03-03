/*
 * Copyright (C) 2006-2020 Talend Inc. - www.talend.com
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
package org.talend.components.ftp.service;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.talend.components.ftp.datastore.FTPDataStore;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FTPService {

    public static final String ACTION_HEALTH_CHECK = "HEALTH_CHECK";

    @Service
    private I18nMessage i18n;

    @HealthCheck(ACTION_HEALTH_CHECK)
    public HealthCheckStatus validateDataStore(@Option final FTPDataStore dataStore) {
        if (dataStore.getHost() == null || "".equals(dataStore.getHost().trim())) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, i18n.hostRequired());
        }
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(dataStore.getHost(), dataStore.getPort());
            log.warn("after connect: {}.", ftp.getReplyString());
            if (!ftp.login(dataStore.getUsername(), dataStore.getPassword())) {
                log.error("after login: {}.", ftp.getReplyString());
                return new HealthCheckStatus(HealthCheckStatus.Status.KO, ftp.getReplyString());
            }
            log.warn("after login: {}.", ftp.getReplyString());
            ftp.logout();
            log.warn("after logout: {}.", ftp.getReplyString());
            ftp.disconnect();
            log.warn("after disconnect: {}.", ftp.getReplyString());
            return new HealthCheckStatus(HealthCheckStatus.Status.OK, i18n.successConnection());
        } catch (IOException e) {
            log.error("Connection failed: {}.", e.getMessage());
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, e.getMessage());
        }
    }

}
