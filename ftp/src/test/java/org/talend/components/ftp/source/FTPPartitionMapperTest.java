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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.impl.StaticLoggerBinder;
import org.talend.components.ftp.dataset.FTPDataSet;
import org.talend.components.ftp.datastore.FTPDataStore;
import org.talend.components.ftp.service.FTPService;
import org.talend.components.ftp.service.ftpclient.FTPClientFactory;
import org.talend.components.ftp.service.ftpclient.GenericFTPClient;
import org.talend.components.ftp.service.ftpclient.GenericFTPFile;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit.environment.Environment;
import org.talend.sdk.component.junit.environment.EnvironmentConfiguration;
import org.talend.sdk.component.junit.environment.builtin.ContextualEnvironment;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.junit5.environment.EnvironmentalTest;

import javax.inject.Inject;
import java.util.Objects;

@Slf4j
@Environment(ContextualEnvironment.class)
@EnvironmentConfiguration(environment = "Contextual", systemProperties = {})
@WithComponents(value = "org.talend.components.ftp")
public class FTPPartitionMapperTest {

    @Injected
    private BaseComponentsHandler componentsHandler;

    @Service
    private FTPService ftpService;

    private FTPInputConfiguration configuration;

    @BeforeEach
    void buildConfig() {

        final StaticLoggerBinder binder = StaticLoggerBinder.getSingleton();

        componentsHandler.injectServices(this);

        FTPDataStore datastore = new FTPDataStore();
        datastore.setFileProtocol(FTPDataStore.FileProtocol.FTP);
        datastore.setHost("test.rebex.net");
        datastore.setUseCredentials(true);
        datastore.setUsername("demo");
        datastore.setPassword("password");

        FTPDataSet dataset = new FTPDataSet();
        dataset.setDatastore(datastore);
        dataset.setPath("/pub/example");

        configuration = new FTPInputConfiguration();
        configuration.setDataSet(dataset);
        configuration.setDebug(true);
    }

    @EnvironmentalTest
    public void testCheckIsFile() {

        try (GenericFTPClient ftpClient = ftpService.getClient(configuration.getDataSet().getDatastore())) {
            ftpClient.listFiles(configuration.getDataSet().getPath()).stream().filter(Objects::nonNull)
                    .forEach(f -> System.out.println(f.getName() + " " + f.isDirectory()));
        }

    }
}
