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
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.impl.StaticLoggerBinder;
import org.talend.components.ftp.dataset.FTPDataSet;
import org.talend.components.ftp.datastore.FTPDataStore;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit.SimpleFactory;
import org.talend.sdk.component.junit.environment.Environment;
import org.talend.sdk.component.junit.environment.EnvironmentConfiguration;
import org.talend.sdk.component.junit.environment.builtin.ContextualEnvironment;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.junit5.environment.EnvironmentalTest;
import org.talend.sdk.component.runtime.manager.chain.Job;

import java.util.List;

@Slf4j
@Environment(ContextualEnvironment.class)
@EnvironmentConfiguration(environment = "Contextual", systemProperties = {})
@WithComponents(value = "org.talend.components.ftp")
public class FTPInputTest {

    @Injected
    private BaseComponentsHandler componentsHandler;

    private FTPInputConfiguration configuration;

    @BeforeEach
    void buildConfig() {

        final StaticLoggerBinder binder = StaticLoggerBinder.getSingleton();

        componentsHandler.injectServices(this);

        FTPDataStore datastore = new FTPDataStore();
        datastore.setHost("test.rebex.net");
        datastore.setUseCredentials(true);
        datastore.setUsername("demo");
        datastore.setPassword("password");

        FTPDataSet dataset = new FTPDataSet();
        dataset.setDatastore(datastore);
        dataset.setFolder("/pub/example");

        configuration = new FTPInputConfiguration();
        configuration.setDataSet(dataset);
        configuration.setDebug(true);
    }

    @EnvironmentalTest
    public void testFTP() {
        configuration.getDataSet().getDatastore().setPort(21);
        final String configStr = SimpleFactory.configurationByExample().forInstance(configuration).configured().toQueryString();

        Job.components().component("source", "FTP://FTPInput?" + configStr).component("target", "test://collector").connections()
                .from("source").to("target").build().run();

        List<Record> records = componentsHandler.getCollectedData(Record.class);

        Assertions.assertNotNull(records);
        Assertions.assertNotEquals(0, records.size(), "No record in result");
        log.info(records.toString());
    }

    @EnvironmentalTest
    @Ignore
    public void testFTPS() {
        configuration.getDataSet().getDatastore().setImplicit(true);
        configuration.getDataSet().getDatastore().setProtocol("SSL");
        configuration.getDataSet().getDatastore().setTrustType(FTPDataStore.TrustType.ALL);
        configuration.getDataSet().getDatastore().setPort(990);
        configuration.getDataSet().getDatastore().setActive(false);
        final String configStr = SimpleFactory.configurationByExample().forInstance(configuration).configured().toQueryString();

        Job.components().component("source", "FTP://FTPInput?" + configStr).component("target", "test://collector").connections()
                .from("source").to("target").build().run();

        List<Record> records = componentsHandler.getCollectedData(Record.class);

        Assertions.assertNotNull(records);
        Assertions.assertNotEquals(0, records.size(), "No record in result");
        log.info(records.toString());
    }
}
