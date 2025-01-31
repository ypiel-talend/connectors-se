/*
 * Copyright (C) 2006-2022 Talend Inc. - www.talend.com
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

import static org.talend.components.jdbc.ErrorFactory.toIllegalStateException;
import static org.talend.sdk.component.api.record.Schema.Type.RECORD;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.talend.components.jdbc.configuration.InputConfig;
import org.talend.components.jdbc.service.I18nMessage;
import org.talend.components.jdbc.service.JdbcService;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractInputEmitter implements Serializable {

    private final InputConfig inputConfig;

    private final RecordBuilderFactory recordBuilderFactory;

    private final JdbcService jdbcDriversService;

    private final I18nMessage i18n;

    protected Connection connection;

    private Statement statement;

    private ResultSet resultSet;

    private JdbcService.JdbcDatasource dataSource;

    private transient Schema schema;

    private transient JdbcService.ColumnInfo[] columnInfoList;

    AbstractInputEmitter(final InputConfig inputConfig, final JdbcService jdbcDriversService,
            final RecordBuilderFactory recordBuilderFactory, final I18nMessage i18nMessage) {
        this.inputConfig = inputConfig;
        this.recordBuilderFactory = recordBuilderFactory;
        this.jdbcDriversService = jdbcDriversService;
        this.i18n = i18nMessage;
    }

    @PostConstruct
    public void init() {
        String query = inputConfig.getDataSet()
                .getQuery(
                        jdbcDriversService.getPlatformService().getPlatform(inputConfig.getDataSet().getConnection()));
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException(i18n.errorEmptyQuery());
        }
        if (jdbcDriversService.isNotReadOnlySQLQuery(query)) {
            throw new IllegalArgumentException(i18n.errorUnauthorizedQuery());
        }

        try {
            dataSource = jdbcDriversService.createDataSource(inputConfig.getDataSet().getConnection());
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            statement.setFetchSize(inputConfig.getDataSet().getFetchSize());
            resultSet = statement.executeQuery(query);

            ResultSetMetaData metaData = resultSet.getMetaData();
            Schema.Builder schemaBuilder = recordBuilderFactory.newSchemaBuilder(RECORD);
            columnInfoList = IntStream.rangeClosed(1, metaData.getColumnCount())
                    .mapToObj(index -> jdbcDriversService.addField(schemaBuilder, metaData, index))
                    .toArray(JdbcService.ColumnInfo[]::new);
            schema = schemaBuilder.build();

            log.debug("Input schema: {}", schema);
            log.debug("SchemaRaw: {}",
                    schema.getEntries().stream().map(Schema.Entry::getRawName).collect(Collectors.joining(",")));
        } catch (final SQLException e) {
            throw toIllegalStateException(e);
        }
    }

    @Producer
    public Record next() {
        try {
            if (!resultSet.next()) {
                return null;
            }
            final Record.Builder recordBuilder = recordBuilderFactory.newRecordBuilder(schema);
            for (int index = 0; index < columnInfoList.length; index++) {
                jdbcDriversService.addColumn(recordBuilder, columnInfoList[index], resultSet.getObject(index + 1));
            }
            return recordBuilder.build();
        } catch (final SQLException e) {
            throw toIllegalStateException(e);
        }
    }

    @PreDestroy
    public void release() {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.warn(i18n.warnResultSetCantBeClosed(), e);
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.warn(i18n.warnStatementCantBeClosed(), e);
            }
        }
        if (connection != null) {
            try {
                if (!connection.getAutoCommit()) {
                    connection.commit();
                }
            } catch (final SQLException e) {
                log.error(i18n.errorSQL(e.getErrorCode(), e.getMessage()), e);
                try {
                    if (!connection.getAutoCommit()) {
                        connection.rollback();
                    }
                } catch (final SQLException rollbackError) {
                    log.error(i18n.errorSQL(rollbackError.getErrorCode(), rollbackError.getMessage()), rollbackError);
                }
            }
            try {
                connection.close();
            } catch (SQLException e) {
                log.warn(i18n.warnConnectionCantBeClosed(), e);
            }
        }
        if (dataSource != null) {
            dataSource.close();
        }
    }

}
