/*
 * Copyright (C) 2006-2021 Talend Inc. - www.talend.com
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
package org.talend.components.jdbc.input;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.talend.components.jdbc.configuration.InputCaptureDataChangeConfig;
import org.talend.components.jdbc.configuration.InputCaptureDataChangeConfig.ChangeOffsetOnReadStrategy;
import org.talend.components.jdbc.dataset.ChangeDataCaptureDataset;
import org.talend.components.jdbc.datastore.AuthenticationType;
import org.talend.components.jdbc.datastore.JdbcConnection;
import org.talend.components.jdbc.service.I18nMessage;
import org.talend.components.jdbc.service.JdbcService;
import org.talend.components.jdbc.service.JdbcService.JdbcDatasource;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.junit5.WithComponents;

@WithComponents("org.talend.components.jdbc")
class ChangeDataCaptureInputEmitterTest {

    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    @Service
    private I18nMessage messages;

    @Service
    private RecordBuilderFactory factory;

    @Service
    private JdbcService services;

    // @Test
    void next() throws SQLException, ExecutionException, InterruptedException {

        final ChangeDataCaptureDataset dataset = this.buildDataset("TABLE_TEST");
        final JdbcDatasource source = this.services.createDataSource(dataset.getConnection());

        this.dropTable(source, "TABLE_TEST");
        this.dropTable(source, "TABLE_TEST1");

        this.createTable(source, "TABLE_TEST");
        this.insert(source, "TABLE_TEST");

        this.createTable(source, "TABLE_TEST1");
        this.insert(source, "TABLE_TEST1");

        final Future<Integer> result1 = this.runTestFor("TABLE_TEST");
        final Future<Integer> result2 = this.runTestFor("TABLE_TEST1");

        final Integer value1 = result1.get();
        final Integer value2 = result2.get();

        Assertions.assertEquals(40, value1);
        Assertions.assertEquals(40, value2);

        this.dropTable(source, "TABLE_TEST");
        this.dropTable(source, "TABLE_TEST1");
    }

    private Future<Integer> runTestFor(final String name) {
        final InputCaptureDataChangeConfig config = new InputCaptureDataChangeConfig();
        config.setChangeOffsetOnRead(ChangeOffsetOnReadStrategy.YES);
        final ChangeDataCaptureDataset dataset = this.buildDataset(name);
        config.setDataSet(dataset);

        // final ChangeDataCaptureInputEmitter2 emitter = new ChangeDataCaptureInputEmitter2(config, this.services, this.factory,
        // this.messages);
        final ChangeDataCaptureInputEmitter2 emitter = new ChangeDataCaptureInputEmitter2(config, this.services, this.factory,
                this.messages);

        return executor.submit(() -> {
            emitter.init();
            ChangeDataCaptureInputEmitterTest.sleep();
            Record record = emitter.next();
            int nbe = 0;
            while (record != null) {
                nbe++;
                ChangeDataCaptureInputEmitterTest.sleep();
                // System.out.println("C1 : " + record.getString("C1"));
                record = emitter.next();
            }
            emitter.release();
            return nbe;
        });
    }

    private static Random r = new Random();

    private static void sleep() {
        try {
            Thread.sleep(Math.abs(r.nextInt(15)) + 1L);
        } catch (InterruptedException e) {
        }
    }

    private void createTable(final JdbcDatasource source, final String name) throws SQLException {
        try (final Connection cnx = source.getConnection()) {
            final String create = "CREATE TABLE IF NOT EXISTS COMPTEST_DB.PUBLIC." + name + "(C1 VARCHAR(10), C2 VARCHAR(10))";
            cnx.createStatement().execute(create);

            String streamCreate = "create stream if not exists COMPTEST_DB.PUBLIC." + name + "_STREAM on table PUBLIC." + name;
            cnx.createStatement().execute(streamCreate);
            cnx.commit();

        }
    }

    private void insert(final JdbcDatasource source, final String name) throws SQLException {
        String insert = "INSERT INTO COMPTEST_DB.PUBLIC." + name + "(C1, C2) values (?, ?)";
        try (final Connection cnx = source.getConnection(); final PreparedStatement statement = cnx.prepareStatement(insert)) {
            for (int i = 1; i < 41; i++) {
                if (i % 13 == 0) {
                    System.out.println("insert new " + i);
                    cnx.commit();
                }

                statement.setString(1, "v1_" + i);
                statement.setString(2, "v2_" + i);
                statement.execute();
            }
            System.out.println("commit");
            cnx.commit();
        }
    }

    private void dropTable(final JdbcDatasource source, final String name) throws SQLException {
        System.out.println("DROP TABLE " + name);
        try (final Connection cnx = source.getConnection()) {
            cnx.createStatement().execute("DROP STREAM IF EXISTS COMPTEST_DB.PUBLIC." + name + "_STREAM");

            final String dropTable = "DROP TABLE IF EXISTS COMPTEST_DB.PUBLIC." + name;
            cnx.createStatement().execute(dropTable);
        }
    }

    private ChangeDataCaptureDataset buildDataset(final String name) {
        final ChangeDataCaptureDataset dataset = new ChangeDataCaptureDataset();
        dataset.setTableName(name);
        dataset.setStreamTableName(name + "_STREAM");
        dataset.setFetchSize(10);

        JdbcConnection connection = new JdbcConnection();
        dataset.setConnection(connection);

        connection.setAuthenticationType(AuthenticationType.BASIC);
        connection.setDbType("Snowflake");
        connection.setUserId("");
        connection.setPassword("");
        connection.setJdbcUrl("");

        return dataset;
    }
}