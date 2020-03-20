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
package org.talend.components.ftp.output;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.talend.components.common.stream.api.RecordIORepository;
import org.talend.components.ftp.service.FTPService;
import org.talend.components.ftp.service.ftpclient.GenericFTPClient;
import org.talend.components.ftp.service.ftpclient.LogWriter;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.Charset;

@RequiredArgsConstructor
@Version
@Icon(value = Icon.IconType.CUSTOM, custom = "ftp")
@Processor(name = "FTPOutput")
@Documentation("FTP output which will upload files in the server using the `name` string from the incoming record "
        + "(will be the file name in the dataset folder) and `content` payload (either of type string or bytes).")
@Slf4j
public class FTPOutput implements Serializable {

    private final FTPOutputConfiguration configuration;

    private final FTPService ftpService;

    private final RecordIORepository ioRepository;

    private transient GenericFTPClient ftpClient;

    private Charset charset;

    @PostConstruct
    public void init() {

        // charset = Charset.forName(configuration.getDataSet().getEncoding());
    }

    @ElementListener
    public void onRecord(final Record record) {

        final String name = record.getString("name");
        final byte[] content = record.getSchema().getEntries().stream().filter(it -> "content".equals(it.getName())).findFirst()
                .map(entry -> {
                    switch (entry.getType()) {
                    case STRING:
                        return record.getString("content").getBytes(charset);
                    case BYTES:
                        return record.getBytes("content");
                    default:
                        throw new IllegalArgumentException("Unsupported content of type: " + entry.getType());
                    }
                }).orElseThrow(() -> new IllegalArgumentException("No content found"));

        GenericFTPClient currentClient = getFtpClient();

        try (final InputStream stream = new ByteArrayInputStream(content)) {
            String path = configuration.getDataSet().getPath() + (configuration.getDataSet().getPath()
                    .endsWith(configuration.getDataSet().getDatastore().getFileSystemSeparator()) ? ""
                            : configuration.getDataSet().getDatastore().getFileSystemSeparator())
                    + name;
            if (!currentClient.storeFile(path, stream)) {
                throw new IllegalStateException("Can't store: " + name);
            }
        } catch (final IOException e) {
            throw new IllegalStateException("Can't store: " + name, e);
        }
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
