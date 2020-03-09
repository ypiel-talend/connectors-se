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
package org.talend.components.ftp.source;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.talend.components.ftp.service.FTPService;
import org.talend.components.ftp.service.I18nMessage;
import org.talend.components.ftp.service.ftpclient.GenericFTPClient;
import org.talend.components.ftp.service.ftpclient.GenericFTPFile;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import javax.annotation.PreDestroy;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FTPInput implements Serializable {

    protected final FTPInputConfiguration configuration;

    protected final FTPService ftpService;

    protected final RecordBuilderFactory recordBuilderFactory;

    protected final I18nMessage i18n;

    private transient Iterator<GenericFTPFile> fileIterator;

    private transient GenericFTPClient ftpClient;

    @Producer
    public Object next() {
        if (fileIterator == null) {
            GenericFTPClient currentClient = getFtpClient();
            try {
                String filePrefix = configuration.getDataSet().getFilePrefix() != null
                        ? configuration.getDataSet().getFilePrefix()
                        : "";
                List<GenericFTPFile> files = currentClient.listFiles(configuration.getDataSet().getFolder(),
                        f -> !(((GenericFTPFile) f).isDirectory()) && f.getName().startsWith(filePrefix));
                fileIterator = files.iterator();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                currentClient.disconnect();
            }
        }

        if (fileIterator.hasNext()) {
            GenericFTPFile file = fileIterator.next();
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            String path = configuration.getDataSet().getFolder() + (configuration.getDataSet().getFolder()
                    .endsWith(configuration.getDataSet().getDatastore().getFileSystemSeparator()) ? ""
                            : configuration.getDataSet().getDatastore().getFileSystemSeparator())
                    + file.getName();
            getFtpClient().retrieveFile(path, buffer);
            return recordBuilderFactory.newRecordBuilder().withString("name", file.getName()).withLong("size", file.getSize())
                    .withBytes("content", buffer.toByteArray()).build();

        }

        return null;
    }

    private GenericFTPClient getFtpClient() {
        if (ftpClient == null || !ftpClient.isConnected()) {
            log.debug("Creating new client");
            ftpClient = ftpService.getClient(configuration.getDataSet().getDatastore());
            if (configuration.isDebug()) {
                ftpClient.enableDebug(log);
            }

            ftpClient.configure(configuration);
        }

        return ftpClient;
    }

    @PreDestroy
    public void release() {
        if (ftpClient != null) {
            ftpClient.disconnect();
        }
    }

}
