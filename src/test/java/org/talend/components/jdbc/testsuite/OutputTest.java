/*
 * Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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
package org.talend.components.jdbc.testsuite;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.talend.components.jdbc.BaseJdbcTest;
import org.talend.components.jdbc.Disabled;
import org.talend.components.jdbc.DisabledDatabases;
import org.talend.components.jdbc.JdbcInvocationContextProvider;
import org.talend.components.jdbc.configuration.OutputConfig;
import org.talend.components.jdbc.containers.JdbcTestContainer;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.junit.environment.Environment;
import org.talend.sdk.component.junit.environment.builtin.ContextualEnvironment;
import org.talend.sdk.component.junit.environment.builtin.beam.DirectRunnerEnvironment;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.manager.chain.Job;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;
import static org.apache.derby.vti.XmlVTI.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.talend.components.jdbc.Database.SNOWFLAKE;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

@DisplayName("Output")
@WithComponents("org.talend.components.jdbc")
@ExtendWith({ JdbcInvocationContextProvider.class })
@Environment(ContextualEnvironment.class)
@Environment(DirectRunnerEnvironment.class)
@DisabledDatabases({ @Disabled(value = SNOWFLAKE, reason = "Snowflake credentials need to be setup on ci") })
class OutputTest extends BaseJdbcTest {

    @TestTemplate
    @DisplayName("Insert - valid use case")
    void insert(final TestInfo testInfo, final JdbcTestContainer container) {
        final OutputConfig configuration = new OutputConfig();
        final String testTableName = getTestTableName(testInfo);
        configuration.setDataset(newTableNameDataset(testTableName, container));
        configuration.setActionOnData(OutputConfig.ActionOnData.INSERT);
        configuration.setCreateTableIfNotExists(true);
        configuration.setKeys(asList("id"));
        final String config = configurationByExample().forInstance(configuration).configured().toQueryString();
        final int rowCount = getRandomRowCount();
        Job.components().component("rowGenerator", "jdbcTest://RowGenerator?" + rowGeneratorConfig(rowCount, false, 0, null))
                .component("jdbcOutput", "Jdbc://Output?" + config).connections().from("rowGenerator").to("jdbcOutput").build()
                .run();
        assertEquals(rowCount, countAll(testTableName, container));
    }

    @TestTemplate
    @DisplayName("Insert - with null values")
    void insertWithNullValues(final TestInfo testInfo, final JdbcTestContainer container) {
        final OutputConfig configuration = new OutputConfig();
        final String testTableName = getTestTableName(testInfo);
        configuration.setDataset(newTableNameDataset(testTableName, container));
        configuration.setActionOnData(OutputConfig.ActionOnData.INSERT);
        configuration.setCreateTableIfNotExists(true);
        configuration.setKeys(asList("id"));
        final String config = configurationByExample().forInstance(configuration).configured().toQueryString();
        final int rowCount = 10;
        Job.components().component("rowGenerator", "jdbcTest://RowGenerator?" + rowGeneratorConfig(rowCount, true, 0, null))
                .component("jdbcOutput", "Jdbc://Output?" + config).connections().from("rowGenerator").to("jdbcOutput").build()
                .run();
        assertEquals(rowCount, countAll(testTableName, container));
    }

    @TestTemplate
    @DisabledDatabases({ @Disabled(value = SNOWFLAKE, reason = "Snowflake database don't enforce PK and UNIQUE constraint") })
    @DisplayName("Insert - duplicate records")
    void insertDuplicateRecords(final TestInfo testInfo, final JdbcTestContainer container) {
        final String testTableName = getTestTableName(testInfo);
        final long rowCount = 5;
        insertRows(testTableName, container, rowCount, false, 0, null);
        assertEquals(rowCount, countAll(testTableName, container));
        insertRows(testTableName, container, rowCount, false, 0, null);
        assertEquals(rowCount, countAll(testTableName, container));
    }

    @TestTemplate
    @DisplayName("Delete - valid query")
    void delete(final TestInfo testInfo, final JdbcTestContainer container) {
        // insert some initial data
        final int rowCount = 10;
        final String testTableName = getTestTableName(testInfo);
        insertRows(testTableName, container, rowCount, false, 0, null);
        // delete the inserted data data
        final OutputConfig deleteConfig = new OutputConfig();
        deleteConfig.setDataset(newTableNameDataset(testTableName, container));
        deleteConfig.setActionOnData(OutputConfig.ActionOnData.DELETE);
        deleteConfig.setKeys(singletonList("id"));
        final String updateConfig = configurationByExample().forInstance(deleteConfig).configured().toQueryString();
        Job.components().component("userGenerator", "jdbcTest://RowGenerator?" + rowGeneratorConfig(rowCount, false, 0, null))
                .component("jdbcOutput", "Jdbc://Output?" + updateConfig).connections().from("userGenerator").to("jdbcOutput")
                .build().run();

        // check the update
        assertEquals(0L, countAll(testTableName, container));
    }

    @TestTemplate
    @DisplayName("Delete - No keys")
    void deleteWithNoKeys(final TestInfo testInfo, final JdbcTestContainer container) {
        final long rowCount = 3;
        final String testTableName = getTestTableName(testInfo);
        insertRows(testTableName, container, rowCount, false, 0, null);
        final Exception error = assertThrows(Exception.class, () -> {
            final OutputConfig deleteConfig = new OutputConfig();
            deleteConfig.setDataset(newTableNameDataset(testTableName, container));
            deleteConfig.setActionOnData(OutputConfig.ActionOnData.DELETE);
            final String updateConfig = configurationByExample().forInstance(deleteConfig).configured().toQueryString();
            Job.components().component("userGenerator", "jdbcTest://RowGenerator?" + rowGeneratorConfig(rowCount, false, 0, null))
                    .component("jdbcOutput", "Jdbc://Output?" + updateConfig).connections().from("userGenerator").to("jdbcOutput")
                    .build().run();
        });
        assertTrue(error.getMessage().contains(getI18nMessage().errorNoKeyForDeleteQuery()));
        assertEquals(rowCount, countAll(testTableName, container));
    }

    @TestTemplate
    @DisplayName("Delete - Missing defined key in incoming record")
    void deleteWithMissingDefinedKeys(final TestInfo testInfo, final JdbcTestContainer container) {
        // 1) insert some data.
        final int rowCount = 2;
        final String testTableName = getTestTableName(testInfo);
        insertRows(testTableName, container, rowCount, false, 0, null);
        // 2) perform delete test with some record with missing delete key (id)
        final OutputConfig deleteConfig = new OutputConfig();
        deleteConfig.setDataset(newTableNameDataset(testTableName, container));
        deleteConfig.setActionOnData(OutputConfig.ActionOnData.DELETE);
        deleteConfig.setKeys(singletonList("id"));
        final String updateConfig = configurationByExample().forInstance(deleteConfig).configured().toQueryString();
        final int missingKeyEvery = 2;
        Job.components()
                .component("userGenerator",
                        "jdbcTest://RowGenerator?" + rowGeneratorConfig(rowCount, false, missingKeyEvery, null))
                .component("jdbcOutput", "Jdbc://Output?configuration.$maxBatchSize=2&" + updateConfig).connections()
                .from("userGenerator").to("jdbcOutput").build().run();

        // 3) check the remaining records
        assertEquals(IntStream.rangeClosed(1, rowCount).filter(r -> r % missingKeyEvery == 0).count(),
                readAll(testTableName, container).size());
    }

    @TestTemplate
    @DisplayName("Update - valid query")
    void update(final TestInfo testInfo, final JdbcTestContainer container) {
        // insert some initial data
        final int rowCount = 10;
        final String testTableName = getTestTableName(testInfo);
        insertRows(testTableName, container, rowCount, false, 0, null);
        // update the inserted data data
        final OutputConfig configuration = new OutputConfig();
        configuration.setDataset(newTableNameDataset(testTableName, container));
        configuration.setActionOnData(OutputConfig.ActionOnData.UPDATE);
        configuration.setKeys(singletonList("id"));
        final String updateConfig = configurationByExample().forInstance(configuration).configured().toQueryString();
        Job.components()
                .component("userGenerator", "jdbcTest://RowGenerator?" + rowGeneratorConfig(rowCount, false, 0, "updated"))
                .component("jdbcOutput", "Jdbc://Output?" + updateConfig).connections().from("userGenerator").to("jdbcOutput")
                .build().run();

        // check the update
        final List<Record> users = readAll(testTableName, container);
        assertEquals(rowCount, users.size());
        assertEquals(IntStream.rangeClosed(1, rowCount).mapToObj(i -> "updated" + i).collect(toSet()), users.stream()
                .map(r -> ofNullable(r.getString("T_STRING")).orElseGet(() -> r.getString("t_string"))).collect(toSet()));
    }

    @TestTemplate
    @DisplayName("Update - no keys")
    void updateWithNoKeys(final TestInfo testInfo, final JdbcTestContainer container) {
        final Exception error = assertThrows(Exception.class, () -> {
            final OutputConfig updateConfiguration = new OutputConfig();
            updateConfiguration.setDataset(newTableNameDataset(getTestTableName(testInfo), container));
            updateConfiguration.setActionOnData(OutputConfig.ActionOnData.UPDATE);
            final String updateConfig = configurationByExample().forInstance(updateConfiguration).configured().toQueryString();
            Job.components().component("userGenerator", "jdbcTest://RowGenerator?" + rowGeneratorConfig(1, false, 0, "updated"))
                    .component("jdbcOutput", "Jdbc://Output?" + updateConfig).connections().from("userGenerator").to("jdbcOutput")
                    .build().run();
        });
        assertTrue(error.getMessage().contains(getI18nMessage().errorNoKeyForUpdateQuery()));
    }

    @TestTemplate
    @DisplayName("Upsert - valid query")
    void upsert(final TestInfo testInfo, final JdbcTestContainer container) {
        // insert some initial data
        final int existingRecords = 40;
        final String testTableName = getTestTableName(testInfo);
        insertRows(testTableName, container, existingRecords, false, 0, null);
        // update the inserted data data
        final OutputConfig configuration = new OutputConfig();
        configuration.setDataset(newTableNameDataset(testTableName, container));
        configuration.setActionOnData(OutputConfig.ActionOnData.UPSERT);
        configuration.setKeys(singletonList("id"));
        final String updateConfig = configurationByExample().forInstance(configuration).configured().toQueryString();
        final int newRecords = existingRecords * 2;
        Job.components()
                .component("rowGenerator", "jdbcTest://RowGenerator?" + rowGeneratorConfig(newRecords, false, 0, "updated"))
                .component("jdbcOutput", "Jdbc://Output?" + updateConfig).connections().from("rowGenerator").to("jdbcOutput")
                .build().run();

        // check the update
        final List<Record> users = readAll(testTableName, container);
        assertEquals(newRecords, users.size());
        assertEquals(IntStream.rangeClosed(1, newRecords).mapToObj(i -> "updated" + i).collect(toSet()), users.stream()
                .map(r -> ofNullable(r.getString("t_string")).orElseGet(() -> r.getString("T_STRING"))).collect(toSet()));
    }
}
