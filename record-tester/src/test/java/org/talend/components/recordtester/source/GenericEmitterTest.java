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
    void testSchemaWithANull() {
        config.getCodingConfig().setProvider(CodingConfig.RECORD_TYPE.FIXED_SCHEMA_WITH_A_NULL_VALUE);

        final List<Record> records = getRecords();

        assertEquals(10, records.size());
        final Record record = records.get(0);

        System.out.println("End.");
    }

    @EnvironmentalTest
    void testSchemaWithMissing() {
        config.getCodingConfig().setProvider(CodingConfig.RECORD_TYPE.FIXED_SCHEMA_WITH_A_MISSING_VALUE);

        final List<Record> records = getRecords();

        assertEquals(10, records.size());
        final Record record = records.get(0);

        System.out.println("End.");
    }

    @EnvironmentalTest
    void testJsonWithNull() {
        config.getCodingConfig().setProvider(CodingConfig.RECORD_TYPE.JSON_WITH_NULL);

        final List<Record> records = getRecords();

        assertEquals(10, records.size());
        final Record record = records.get(0);

        System.out.println("End.");
    }

    @EnvironmentalTest
    void testJsonWithArrayWithNull() {
        config.getCodingConfig().setProvider(CodingConfig.RECORD_TYPE.JSON_WITH_ARRAY_WITH_NULL);

        final List<Record> records = getRecords();

        assertEquals(1, records.size());
        final Record record = records.get(0);

        System.out.println("End.");
    }

    @EnvironmentalTest
    void testBeanShell() {
        config.getCodingConfig().setProvider(CodingConfig.RECORD_TYPE.BEANSHELL);
        config.getCodingConfig()
                .setBeanShellCode("for(int i = 1; i <= 10; i++){\n" + " record = provider.getRecordBuilderFactory()\n"
                        + " .newRecordBuilder()\n" + " .withString(\"foo\", \"bar_\"+i);\n" + " records.add(record.build());\n"
                        + "}");

        final List<Record> records = getRecords();

        assertEquals(10, records.size());
        final Record record = records.get(0);

        System.out.println("End.");
    }

    @EnvironmentalTest
    void testJson() {
        config.getCodingConfig().setProvider(CodingConfig.RECORD_TYPE.JSON);
        config.getCodingConfig().setJsonPointer("/");
        config.getCodingConfig()
                .setJson("{\n" + "  \"aaa\" : \"aaaaa\",\n" + "  \"bbb\" : 12.5,\n" + "  \"ccc\" : true,\n" + "  \"ddd\" : {\n"
                        + "    \"eee\" : \"eeee\"\n" + "  },\n" + "  \"fff\" : [\n"
                        + "  \t{\"ggg\" : \"ggg1\", \"hhh\" : \"hhh1\"},\n" + "  \t{\"ggg\" : \"ggg2\", \"hhh\" : \"hhh2\"}\n"
                        + "  ]\n" + "}");

        final List<Record> records = getRecords();

        assertEquals(1, records.size());
        final Record record = records.get(0);

        System.out.println("End.");
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