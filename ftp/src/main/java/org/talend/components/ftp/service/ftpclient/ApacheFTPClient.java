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
package org.talend.components.ftp.service.ftpclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;
import org.slf4j.Logger;
import org.talend.components.ftp.datastore.FTPDataStore;
import org.talend.components.ftp.output.FTPOutputConfiguration;
import org.talend.components.ftp.source.FTPInputConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.function.Predicate;

@Slf4j
@RequiredArgsConstructor
/**
 * FTP client for FTP and FTPS
 */
public class ApacheFTPClient extends GenericFTPClient {

    private FTPClient ftpClient;

    public static ApacheFTPClient createFTP(FTPDataStore dataStore) {
        FTPClient ftpClient = ftpClient = new FTPClient();
        if (dataStore.getDateFormat() != null && !"".equals(dataStore.getDateFormat().trim())
                && dataStore.getRecentDateFormat() != null && !"".equals(dataStore.getRecentDateFormat().trim())) {
            final FTPClientConfig config = new FTPClientConfig();
            config.setDefaultDateFormatStr(dataStore.getDateFormat());
            config.setDefaultDateFormatStr(dataStore.getRecentDateFormat());
            ftpClient.configure(config);
        }

        ftpClient.setControlKeepAliveTimeout(dataStore.getKeepAliveTimeout());
        ftpClient.setControlKeepAliveReplyTimeout(dataStore.getKeepAliveReplyTimeout());

        ApacheFTPClient result = new ApacheFTPClient();
        result.ftpClient = ftpClient;

        return result;
    }

    public static ApacheFTPClient createFTPS(FTPDataStore dataStore) {

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
        FTPClient ftpClient = ftps;

        if (dataStore.getDateFormat() != null && !"".equals(dataStore.getDateFormat().trim())
                && dataStore.getRecentDateFormat() != null && !"".equals(dataStore.getRecentDateFormat().trim())) {
            final FTPClientConfig config = new FTPClientConfig();
            config.setDefaultDateFormatStr(dataStore.getDateFormat());
            config.setDefaultDateFormatStr(dataStore.getRecentDateFormat());
            ftpClient.configure(config);
        }

        ftpClient.setControlKeepAliveTimeout(dataStore.getKeepAliveTimeout());
        ftpClient.setControlKeepAliveReplyTimeout(dataStore.getKeepAliveReplyTimeout());

        ApacheFTPClient result = new ApacheFTPClient();
        result.ftpClient = ftpClient;

        return result;
    }

    @Override
    public void connect(String host, int port) {
        try {
            ftpClient.connect(host, port);
        } catch (Exception e) {
            log.error(getI18n().errorConnection(e.getMessage()), e);
        }
    }

    @Override
    public boolean auth(String username, String password) {
        try {
            return ftpClient.login(username, password);
        } catch (IOException ioe) {
            log.error(getI18n().errorConnection(ioe.getMessage()));
            return false;
        }
    }

    @Override
    public void afterAuth(FTPDataStore dataStore) {
        if (dataStore.isActive()) {
            ftpClient.enterLocalActiveMode();
        } else {
            ftpClient.enterLocalPassiveMode();
        }
    }

    @Override
    public boolean isConnected() {
        return ftpClient == null ? false : ftpClient.isConnected();
    }

    @Override
    public void disconnect() {
        if (ftpClient != null) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                log.warn(getI18n().warnCannotDisconnect(e.getMessage()));
            }
        }
    }

    @Override
    public void enableDebug(Logger alog) {
        ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(new LogWriter(alog)), true));
    }

    @Override
    public FTPFile[] listFiles(String path, Predicate<FTPFile> filter) {
        try {
            return ftpClient.listFiles(path, f -> filter.test(f));
        } catch (IOException ioe) {
            log.error(getI18n().errorListFiles(ioe.getMessage()));
            return null;
        }
    }

    @Override
    public void retrieveFile(String path, OutputStream out) {
        try {
            ftpClient.retrieveFile(path, out);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void configure(FTPInputConfiguration configuration) {
        ftpClient.setControlEncoding(configuration.getDataSet().getEncoding());
        ftpClient.setListHiddenFiles(configuration.getDataSet().isListHiddenFiles());
    }

    @Override
    public void configure(FTPOutputConfiguration configuration) {

    }

    @Override
    public boolean storeFile(String path, InputStream stream) {
        try {
            return ftpClient.storeFile(path, stream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

}
