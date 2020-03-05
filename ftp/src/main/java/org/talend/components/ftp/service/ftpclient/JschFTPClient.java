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

import com.jcraft.jsch.Session;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.talend.components.ftp.datastore.FTPDataStore;
import org.talend.components.ftp.output.FTPOutputConfiguration;
import org.talend.components.ftp.source.FTPInputConfiguration;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Predicate;

/**
 * FTP client for SFTP connection
 */
public class JschFTPClient extends GenericFTPClient {

    private Session session;

    public static JschFTPClient create(FTPDataStore dataStore) {
        return null;
    }

    @Override
    public void connect(String host, int port) {

    }

    @Override
    public boolean auth(String username, String password) {
        return false;
    }

    @Override
    public void afterAuth(FTPDataStore dataStore) {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void enableDebug(Logger log) {

    }

    @Override
    public FTPFile[] listFiles(String path, Predicate<FTPFile> filter) {
        return null;
    }

    @Override
    public void retrieveFile(String path, OutputStream out) {

    }

    @Override
    public void configure(FTPInputConfiguration configuration) {

    }

    @Override
    public void configure(FTPOutputConfiguration configuration) {

    }

    @Override
    public boolean storeFile(String path, InputStream stream) {
        return false;
    }
}
