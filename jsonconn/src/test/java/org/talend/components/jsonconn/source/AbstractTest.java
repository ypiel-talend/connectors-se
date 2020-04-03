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
package org.talend.components.jsonconn.source;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.talend.components.jsonconn.conf.Config;
import org.talend.components.jsonconn.conf.Dataset;
import org.talend.components.jsonconn.conf.Datastore;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit.environment.Environment;
import org.talend.sdk.component.junit.environment.EnvironmentConfiguration;
import org.talend.sdk.component.junit.environment.builtin.ContextualEnvironment;
import org.talend.sdk.component.junit.environment.builtin.beam.SparkRunnerEnvironment;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;

import java.io.IOException;

public class AbstractTest {

    @Injected
    protected BaseComponentsHandler handler;

    protected Config config;

    @BeforeEach
    void buildConfig() throws IOException {
        // Inject needed services
        handler.injectServices(this);

        Datastore dso = new Datastore();
        dso.setDso("");

        Dataset dse = new Dataset();
        dse.setDatastore(dso);

        config = new Config();
        config.setDataset(dse);
    }
}
