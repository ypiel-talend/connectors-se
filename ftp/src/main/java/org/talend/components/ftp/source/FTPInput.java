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
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.talend.components.ftp.service.FTPService;
import org.talend.components.ftp.service.I18nMessage;
import org.talend.components.ftp.service.LogWriter;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

@Slf4j
@RequiredArgsConstructor
public class FTPInput implements Serializable {

    protected final FTPInputConfiguration configuration;

    protected final FTPService ftpService;

    protected final RecordBuilderFactory recordBuilderFactory;

    protected final I18nMessage i18n;

    private transient Iterator<FTPFile> fileIterator;

    @Producer
    public Object next() {
        if (fileIterator == null) {
            FTPClient ftpClient = ftpService.getClient(configuration.getDataSet().getDatastore());
            if (configuration.isDebug()) {
                ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(new LogWriter(log)), true));
            }
            try {
                FTPFile[] files = ftpClient.listFiles(configuration.getDataSet().getFolder());
                fileIterator = Arrays.asList(files).iterator();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (fileIterator.hasNext()) {
            FTPFile file = fileIterator.next();
            return recordBuilderFactory.newRecordBuilder().withString("name", file.getName()).build();
        }

        return null;
    }


}
