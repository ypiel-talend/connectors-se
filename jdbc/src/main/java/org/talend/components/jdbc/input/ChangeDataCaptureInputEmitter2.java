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

import static org.talend.components.jdbc.ErrorFactory.toIllegalStateException;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.talend.components.jdbc.configuration.InputCaptureDataChangeConfig;
import org.talend.components.jdbc.input.capture.ResultSetGetter;
import org.talend.components.jdbc.input.capture.ResultSetToRecord;
import org.talend.components.jdbc.service.I18nMessage;
import org.talend.components.jdbc.service.JdbcService;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j

@Documentation("JDBC input using stream table name")
public class ChangeDataCaptureInputEmitter2 implements Serializable {

    private final InputCaptureDataChangeConfig inputConfig;

    private final RecordBuilderFactory recordBuilderFactory;

    private final JdbcService jdbcDriversService;

    private final I18nMessage i18n;

    protected Connection connection;

    private JdbcService.JdbcDatasource dataSource;

    private transient ResultSetGetter resultSetGetter;

    private transient ResultSetToRecord toRecord;

    ChangeDataCaptureInputEmitter2(@Option("configuration") final InputCaptureDataChangeConfig config,
            final JdbcService jdbcDriversService, final RecordBuilderFactory recordBuilderFactory,
            final I18nMessage i18nMessage) {
        this.inputConfig = config;
        this.recordBuilderFactory = recordBuilderFactory;
        this.jdbcDriversService = jdbcDriversService;
        this.i18n = i18nMessage;
    }

    @PostConstruct
    public void init() {
        log.debug("Init is called");
        final String query = inputConfig.getDataSet().getQuery();
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException(i18n.errorEmptyQuery());
        }
        if (jdbcDriversService.isNotReadOnlySQLQuery(query)) {
            throw new IllegalArgumentException(i18n.errorUnauthorizedQuery());
        }

        try {
            dataSource = jdbcDriversService.createDataSource(inputConfig.getDataSet().getConnection());
            connection = dataSource.getConnection();
            boolean commit = (inputConfig.getChangeOffsetOnRead() == InputCaptureDataChangeConfig.ChangeOffsetOnReadStrategy.YES);
            this.resultSetGetter = new ResultSetGetter(connection, inputConfig, commit);
            this.toRecord = new ResultSetToRecord(this.resultSetGetter::nextResultSet, this.recordBuilderFactory,
                    this.jdbcDriversService);

            final String createStreamStatement = this.inputConfig.getDataSet().createStreamTableIfNotExist();
            // first statement to create stream table if needed
            try (final Statement statementUpdate = connection.createStatement()) {
                statementUpdate.executeUpdate(createStreamStatement);
            }
        } catch (final SQLException e) {
            throw toIllegalStateException(e);
        }
    }

    @Producer
    public Record next() {
        try {
            final Record record = this.toRecord.tryNext();
            return record;
        } catch (final SQLException e) {
            log.error("Exception found in next() ", e);
            throw toIllegalStateException(e);
        }
    }

    @PreDestroy
    public void release() {
        try {
            if (this.toRecord != null) {
                this.toRecord.close();
            }
        } catch (SQLException e) {
            log.warn(i18n.warnStatementCantBeClosed(), e);
        }
        try {
            if (this.resultSetGetter != null) {
                this.resultSetGetter.close();
            }
        } catch (SQLException e) {
            log.warn(i18n.warnStatementCantBeClosed(), e);
        }

        if (connection != null) {
            try {
                connection.commit();
            } catch (final SQLException e) {
                log.error(i18n.errorSQL(e.getErrorCode(), e.getMessage()), e);
                try {
                    connection.rollback();
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