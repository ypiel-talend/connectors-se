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

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.talend.components.ftp.datastore.FTPDataStore;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpProtocolException;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

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

        InetSocketAddress inetSocketAddress = new InetSocketAddress(dataStore.getHost(), dataStore.getPort());
        try (FtpClient client = FtpClient.create(inetSocketAddress)) {
            client.login(dataStore.getUsername(), dataStore.getPassword().toCharArray());
            client.getStatus(null);
            return new HealthCheckStatus(HealthCheckStatus.Status.OK, i18n.successConnection());
        } catch (Exception e) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, e.getMessage());
        }
    }

}
