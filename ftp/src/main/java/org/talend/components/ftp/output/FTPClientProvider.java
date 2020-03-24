package org.talend.components.ftp.output;

import org.talend.components.ftp.service.ftpclient.GenericFTPClient;

@FunctionalInterface
public interface FTPClientProvider {

    GenericFTPClient getClient();
}
