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
import org.talend.components.common.stream.api.output.RecordWriter;
import org.talend.components.common.stream.api.output.TargetFinder;
import org.talend.components.common.stream.format.ContentFormat;
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
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.UUID;

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

    private final RecordIORepository recordIORepository;

    private transient GenericFTPClient ftpClient;

    private transient RecordWriter recordWriter;

    private transient boolean init = false;

    private transient OutputStream currentStream;

    private transient long currentStreamSize = 0l;

    private transient int currentRecords = 0;

    private transient String fileBaseName;

    private transient int fileIndex;


    @ElementListener
    public void onRecord(final Record record) {
        if (!init) {
            init = true;
            String remoteDir = configuration.getDataSet().getPath();
            if (!remoteDir.endsWith("/")) {
                remoteDir += "/";
            }
            fileBaseName = remoteDir + "file_" + UUID.randomUUID();

        }

        checkCurrentStream();
        try {
            recordWriter.add(record);
            currentRecords++;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void checkCurrentStream() {
        if (currentStream == null
                || (configuration.getLimitBy().isLimitedByRecords() && currentRecords >= configuration.getRecordsLimit())
                || (configuration.getLimitBy().isLimitedBySize() && currentStreamSize >= configuration.getSizeLimit())) {
            if (currentStream != null) {
                try {
                    recordWriter.end();
                    recordWriter.close();
                    currentStream.close();
                    if (ftpClient.isConnected()) {
                        getFtpClient().disconnect();
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
            currentRecords = 0;
            // Must create new file
            String path = fileBaseName + "_" + fileIndex++ + "." + configuration.getDataSet().getFormat().getExtension();
            log.debug("Creating remote file " + path);
            currentStream = getFtpClient().storeFileStream(path);
            ContentFormat contentFormat = configuration.getDataSet().getFormatConfiguration();
            recordWriter = recordIORepository.findWriter(contentFormat.getClass()).getWriter(() -> currentStream, contentFormat);
            try {
                recordWriter.init(contentFormat);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        if (currentStream != null) {
            try {
                recordWriter.flush();
                recordWriter.end();
                recordWriter.close();
                currentStream.flush();
                currentStream.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        if (ftpClient != null) {
            ftpClient.disconnect();
        }
    }

}
