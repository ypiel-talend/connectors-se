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
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;
import org.slf4j.LoggerFactory;
import org.talend.components.ftp.datastore.FTPDataStore;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class FTPService implements Serializable {

    public static final String ACTION_HEALTH_CHECK = "HEALTH_CHECK";

    @Service
    private I18nMessage i18n;

    @HealthCheck(ACTION_HEALTH_CHECK)
    public HealthCheckStatus validateDataStore(@Option final FTPDataStore dataStore) {
        if (dataStore.getHost() == null || "".equals(dataStore.getHost().trim())) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, i18n.hostRequired());
        }

        FTPClient ftpClient = null;
        try {
            ftpClient = getClient(dataStore);
            if (ftpClient.sendNoOp()) {
                return new HealthCheckStatus(HealthCheckStatus.Status.OK, i18n.successConnection());
            }
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, "NOOP");
        } catch (Exception e) {
            e.printStackTrace();
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, e.getMessage());
        } finally {
            if (ftpClient != null) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public FTPClient getClient(FTPDataStore dataStore) {
        FTPClient ftpClient = null;
        if (!dataStore.isSecure()) {
            ftpClient = new FTPClient();
        } else {
            FTPSClient ftps = new FTPSClient(dataStore.getProtocol(), dataStore.isImplicit());
            switch (dataStore.getTrustType()) {
                case ALL:
                    ftps.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
                    break;
                case VALID:
                    ftps.setTrustManager(TrustManagerUtils.getValidateServerCertificateTrustManager());
                    break;
                case NONE:
                    ftps.setTrustManager(null);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported trust type: " + dataStore.getTrustType());
            }
            ftpClient = ftps;
        }
        try {
            ftpClient.connect(dataStore.getHost(), dataStore.getPort());
            if (dataStore.isUseCredentials()) {
                ftpClient.login(dataStore.getUsername(), dataStore.getPassword());
            }
            if (dataStore.isActive()) {
                ftpClient.enterLocalActiveMode();
            } else {
                ftpClient.enterLocalPassiveMode();
            }
            return ftpClient;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
