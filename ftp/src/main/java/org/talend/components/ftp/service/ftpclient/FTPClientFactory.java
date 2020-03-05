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

import org.talend.components.ftp.datastore.FTPDataStore;
import org.talend.components.ftp.service.FTPConnectorException;
import org.talend.components.ftp.service.I18nMessage;
import org.talend.sdk.component.api.service.Service;

@Service
public class FTPClientFactory {

    @Service
    private I18nMessage i18n;

    public GenericFTPClient getClient(FTPDataStore datastore) {
        GenericFTPClient ftpClient = null;
        switch (datastore.getFileProtocol()) {
        case FTP:
            ftpClient = ApacheFTPClient.createFTP(datastore);
            break;
        case FTPS:
            ftpClient = ApacheFTPClient.createFTPS(datastore);
            break;
        case SFTP:
            ftpClient = JschFTPClient.create(datastore);
            break;
        default:
            throw new FTPConnectorException("Unknown File protocol : " + datastore.getFileProtocol());
        }

        ftpClient.setI18n(i18n);
        return ftpClient;
    }
}
