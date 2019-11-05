/*
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
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
package org.talend.components.bigquery.input;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.talend.components.bigquery.dataset.TableDataSet;
import org.talend.components.bigquery.datastore.BigQueryConnection;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.junit.environment.Environment;
import org.talend.sdk.component.junit.environment.EnvironmentConfiguration;
import org.talend.sdk.component.junit.environment.EnvironmentConfigurations;
import org.talend.sdk.component.junit.environment.Environments;
import org.talend.sdk.component.junit.environment.builtin.beam.SparkRunnerEnvironment;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.junit5.environment.EnvironmentalTest;
import org.talend.sdk.component.runtime.beam.TalendIO;
import org.talend.sdk.component.runtime.input.Mapper;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

@Environments({ @Environment(SparkRunnerEnvironment.class) })
@WithComponents("org.talend.components.bigquery")
public class BigQueryTableExtractInputTest {

    public static class Counter extends DoFn<Record, Record> {

        private static AtomicLong counter = new AtomicLong();

        @ProcessElement
        public void processElement(ProcessContext c) {
            if (counter.incrementAndGet() % 10000 == 0) {
                System.out.println(counter.get());
            }
        }

        public static long getCounter() {
            return counter.get();
        }

        public static void reset() {
            counter.set(0l);
        }
    }

    @Injected
    private BaseComponentsHandler handler;

    @Rule
    public final SimpleComponentRule COMPONENTS = new SimpleComponentRule("org.talend.sdk.component.mycomponent");

    @BeforeEach
    void buildConfig() throws IOException {
        // Inject needed services
        handler.injectServices(this);

    }

    @EnvironmentalTest
    // @Test
    public void justLoop() {
        OptionalDouble avg = IntStream.range(0, 1).mapToLong(i -> {
            try {
                Counter.reset();
                return run();
            } catch (Exception e) {
                e.printStackTrace();
                ;
                return 0l;
            }
        }).average();

        avg.ifPresent(System.out::println);
    }

    // @Test
    public long run() {

        COMPONENTS.resetState();

        String jsonCredentials = "";
        try (FileInputStream in = new FileInputStream("C:\\Users\\rlecomte\\Documents\\Engineering-4e7ac6cf93f4.json");
                BufferedInputStream bIn = new BufferedInputStream(in)) {
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = bIn.read(buffer)) > 0) {
                jsonCredentials += new String(buffer, 0, read, StandardCharsets.UTF_8);
            }
            jsonCredentials = jsonCredentials.replace("\n", " ").trim();
        } catch (IOException ioe) {
            Assertions.fail(ioe);
        }

        BigQueryConnection connection = new BigQueryConnection();
        connection.setProjectName("engineering-152721");
        connection.setJSonCredentials(jsonCredentials);

        TableDataSet dataset = new TableDataSet();
        dataset.setConnection(connection);
        dataset.setBqDataset("dataset_rlecomte");
        dataset.setTableName("TableWithData");
        dataset.setGsBucket("tdi_rlecomte");

        BigQueryTableExtractInputConfig config = new BigQueryTableExtractInputConfig();
        config.setTableDataset(dataset);

        // String configURI = configurationByExample().forInstance(config).configured().toQueryString();
        // System.out.println(configURI);

        Counter counter = new Counter();

        long start = System.currentTimeMillis();
        Mapper mapper = handler.createMapper(BigQueryTableExtractMapper.class, config);
        Pipeline.create(
                PipelineOptionsFactory.fromArgs("--runner=org.apache.beam.runners.spark.SparkRunner", "--filesToStage=").create())
                .apply(TalendIO.read(mapper)).apply(ParDo.of(counter)).getPipeline().run().waitUntilFinish();

        long end = System.currentTimeMillis();

        List<Record> records = COMPONENTS.getCollectedData(Record.class);

        // records.stream().limit(1000).forEach(System.out::println);

        // Assertions.assertNotNull(records);
        System.out.println(counter.getCounter() + " in " + (end - start) + "ms");
        // Assertions.assertNotEquals(0, records.size());

        return end - start;

    }
}
