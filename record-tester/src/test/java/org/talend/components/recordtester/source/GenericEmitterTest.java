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
package org.talend.components.recordtester.source;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.talend.components.recordtester.conf.CodingConfig;
import org.talend.components.recordtester.conf.Config;
import org.talend.components.recordtester.conf.Dataset;
import org.talend.components.recordtester.conf.Datastore;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit.environment.Environment;
import org.talend.sdk.component.junit.environment.EnvironmentConfiguration;
import org.talend.sdk.component.junit.environment.builtin.ContextualEnvironment;
import org.talend.sdk.component.junit.environment.builtin.beam.SparkRunnerEnvironment;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.junit5.environment.EnvironmentalTest;
import org.talend.sdk.component.runtime.manager.chain.Job;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

@Slf4j
@Environment(ContextualEnvironment.class)
@EnvironmentConfiguration(environment = "Contextual", systemProperties = {})

@Environment(SparkRunnerEnvironment.class)
@EnvironmentConfiguration(environment = "Spark", systemProperties = {
        @EnvironmentConfiguration.Property(key = "talend.beam.job.runner", value = "org.apache.beam.runners.spark.SparkRunner"),
        @EnvironmentConfiguration.Property(key = "talend.beam.job.filesToStage", value = ""),
        @EnvironmentConfiguration.Property(key = "spark.ui.enabled", value = "false") })

@WithComponents(value = "org.talend.components.recordtester")
class GenericEmitterTest {

    @Injected
    protected BaseComponentsHandler handler;

    protected Config config;

    @BeforeEach
    void buildConfig() throws IOException {
        // Inject needed services
        handler.injectServices(this);

        Datastore dso = new Datastore();
        Dataset dse = new Dataset();
        dse.setDatastore(dso);
        config = new Config();
        config.setDataset(dse);
    }

    @EnvironmentalTest
    void testBeanShellWithSplit() {
        final List<Record> records = _testBeanShellWithSplit(false);
        assertEquals(50, records.size());
    }

    @EnvironmentalTest
    void testBeanShellWithSplitWithrewrite() {
        final List<Record> records = _testBeanShellWithSplit(true);
        assertEquals(20, records.size());
    }

    @EnvironmentalTest
    List<Record> _testBeanShellWithSplit(boolean rewrite) {
        config.getDataset().setSplits(5);
        config.getDataset().getDsCodingConfig().setProvider(CodingConfig.RECORD_TYPE.BEANSHELL);
        config.getDataset().getDsCodingConfig()
                .setBeanShellCode("for(int i = 1; i <= 10; i++){\n" + " record = provider.getRecordBuilderFactory()\n"
                        + " .newRecordBuilder()\n" + " .withString(\"foo\", \"bar_\"+i);\n" + " records.add(record.build());\n"
                        + "}");

        config.getCodingConfig().setProvider(CodingConfig.RECORD_TYPE.BEANSHELL);
        config.getCodingConfig()
                .setBeanShellCode("for(int i = 1; i <= 5; i++){\n" + " record = provider.getRecordBuilderFactory()\n"
                        + " .newRecordBuilder()\n" + " .withString(\"foo\", \"bar_\"+i);\n" + " records.add(record.build());\n"
                        + "}");
        config.setSplits(4);
        config.setOverwriteDataset(rewrite);

        return getRecords();
    }

    @EnvironmentalTest
    void testJsonWithSplit() {
        List<Record> records = _testJsonWithWithSplit(false);
        assertEquals(6, records.size());
    }

    @EnvironmentalTest
    void testJsonWithSplitWithRewrite() {
        List<Record> records = _testJsonWithWithSplit(true);
        assertEquals(10, records.size());
    }

    List<Record> _testJsonWithWithSplit(boolean rewrite) {
        config.getDataset().setSplits(2);
        config.getDataset().getDsCodingConfig().setProvider(CodingConfig.RECORD_TYPE.JSON);
        config.getDataset().getDsCodingConfig().setJsonPointer("/arr_b");
        config.getDataset().getDsCodingConfig()
                .setJson("{\n" + "\t\"att_a\" : \"val_a\",\n" + "\t\"arr_b\" : [\n"
                        + "\t\t{\"att_c\": 1, \"att_d\": \"val_d1\"},\n" + "\t\t{\"att_c\": 2, \"att_d\": \"val_d2\"},\n"
                        + "\t\t{\"att_c\": 3, \"att_d\": \"val_d3\"}\n" + "\t]\n" + "}");

        config.getCodingConfig().setProvider(CodingConfig.RECORD_TYPE.JSON);
        config.getCodingConfig().setJsonPointer("/arr_b");
        config.getCodingConfig().setJson("{\n" + "\t\"att_a\" : \"val_a\",\n" + "\t\"arr_b\" : [\n"
                + "\t\t{\"att_c\": 1, \"att_d\": \"val_d1\"},\n" + "\t\t{\"att_c\": 2, \"att_d\": \"val_d2\"}\n" + "\t]\n" + "}");
        config.setOverwriteDataset(rewrite);
        config.setSplits(5);

        return getRecords();
    }

    @EnvironmentalTest
    void testJsonFile() {
        config.getDataset().getDsCodingConfig().setProvider(CodingConfig.RECORD_TYPE.JSON);
        config.getDataset().setFile("fd.json");

        final List<Record> records = getRecords();
        assertEquals(1, records.size());
    }

    @EnvironmentalTest
    void testEmptyProvider() {
        final List<Record> records = _testEmptyProvider(false);
        assertEquals(0, records.size());
    }

    @EnvironmentalTest
    void testEmptyProviderDatasetWithRewrite() {
        final List<Record> records = _testEmptyProvider(true);
        assertEquals(0, records.size());
    }

    @EnvironmentalTest
    void testFixedRecord() {
        config.setOverwriteDataset(true);
        config.getCodingConfig().setProvider(CodingConfig.RECORD_TYPE.FIXED);
        config.getCodingConfig().setNbRecord(500);
        final List<Record> records = getRecords();
        assertEquals(500, records.size());
    }

    List<Record> _testEmptyProvider(boolean rewrite) {
        if (!rewrite) {
            config.getDataset().getDsCodingConfig().setProvider(CodingConfig.RECORD_TYPE.EMPTY);
            config.getCodingConfig().setProvider(CodingConfig.RECORD_TYPE.JSON);
            config.setFile("fd.json");
        } else {
            config.getCodingConfig().setProvider(CodingConfig.RECORD_TYPE.EMPTY);
            config.getDataset().getDsCodingConfig().setProvider(CodingConfig.RECORD_TYPE.JSON);
            config.getDataset().setFile("fd.json");
        }

        config.setOverwriteDataset(rewrite);

        return getRecords();
    }

    private List<Record> getRecords() {
        final String configStr = configurationByExample().forInstance(config).configured().toQueryString();

        Job.components() //
                .component("emitter", "Tester://genericInput?" + configStr) //
                .component("out", "test://collector") //
                .connections() //
                .from("emitter") //
                .to("out") //
                .build() //
                .run();

        return handler.getCollectedData(Record.class);
    }

}