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
import org.apache.beam.sdk.transforms.ParDo;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.talend.components.bigquery.dataset.QueryDataSet;
import org.talend.components.bigquery.dataset.TableDataSet;
import org.talend.components.bigquery.datastore.BigQueryConnection;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.junit.environment.Environment;
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

@Environment(SparkRunnerEnvironment.class)
@WithComponents(value = "org.talend.components.bigquery")
public class BigQueryQueryInputTest {

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
    public void justTest() {
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

        QueryDataSet dataset = new QueryDataSet();
        dataset.setConnection(connection);
        dataset.setUseLegacySql(true);
        dataset.setQuery("SELECT COUNT(ID) FROM dataset_rlecomte.TableWithData WHERE IS_TRUE = true");

        BigQueryQueryInputConfig config = new BigQueryQueryInputConfig();
        config.setQueryDataset(dataset);

        BigQueryTableExtractInputTest.Counter counter = new BigQueryTableExtractInputTest.Counter();

        BigQueryTableExtractInputTest.Counter.reset();

        long start = System.currentTimeMillis();
        Mapper mapper = handler.createMapper(BigQueryQueryInput.class, config);
        Pipeline.create(
                PipelineOptionsFactory.fromArgs("--runner=org.apache.beam.runners.spark.SparkRunner", "--filesToStage=").create())
                .apply(TalendIO.read(mapper)).apply(ParDo.of(counter)).getPipeline().run().waitUntilFinish();

        long end = System.currentTimeMillis();

        // List<Record> records = COMPONENTS.getCollectedData(Record.class);

        // records.stream().limit(1000).forEach(System.out::println);

        // Assertions.assertNotNull(records);
        System.out.println(counter.getCounter() + " in " + (end - start) + "ms");
        // Assertions.assertNotEquals(0, records.size());
    }
}
