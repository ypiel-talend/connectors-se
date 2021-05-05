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
package org.talend.components.jdbc.input.capture;

import static org.talend.components.jdbc.ErrorFactory.toIllegalStateException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.talend.components.jdbc.configuration.InputCaptureDataChangeConfig;
import org.talend.components.jdbc.dataset.ChangeDataCaptureDataset;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ResultSetGetter implements AutoCloseable {

    private final Connection connection;

    private final InputCaptureDataChangeConfig inputConfig;

    private final boolean commit;

    private ContinuePredicate doContinue = null;

    private Statement statement;

    public ResultSet nextResultSet() {
        log.info("Fetch data attempt, commmit: " + commit);

        try {
            log.info("------------Fetch data------------------");
            final String query = inputConfig.getDataSet().getQuery();
            log.info("Fetch data with query: " + query);

            if (this.doContinue != null && !(this.doContinue.doContinue())) {
                return null;
            }
            if (this.doContinue == null) {
                this.doContinue = new ContinuePredicate();
            }

            connection.setAutoCommit(false);

            // then read from stream table to compute size
            this.close();
            statement = connection.createStatement();
            statement.setFetchSize(inputConfig.getDataSet().getFetchSize());
            final ResultSet resultSet = statement.executeQuery(query);

            connection.commit();
            connection.setAutoCommit(true);

            // move the offset
            if (commit) {
                connection.setAutoCommit(false);
                final ChangeDataCaptureDataset cdcDataset = inputConfig.getDataSet();
                try (Statement statementUpdate = connection.createStatement()) {
                    final String createStreamCounterStatement = cdcDataset.createCounterTableIfNotExist();
                    int resultCreateCounter = statementUpdate.executeUpdate(createStreamCounterStatement);
                    log.debug("Update statement changed records: " + resultCreateCounter);
                }
                final String consumeRecordsStreamStatement = cdcDataset.createStatementConsumeStreamTable();
                try (Statement statementUpdate = connection.createStatement()) {
                    statementUpdate.executeUpdate(consumeRecordsStreamStatement);
                }
                connection.commit();
                connection.setAutoCommit(true);
            } else {
                log.debug("consumption=no");
            }

            return this.doContinue.wrap(resultSet);
        } catch (final SQLException e) {
            log.error("Exception found in next() ", e);
            throw toIllegalStateException(e);
        } catch (final Exception e) {
            log.error("Exception found in next() ", e);
            throw toIllegalStateException(e);
        }
    }

    @Override
    public void close() throws SQLException {
        if (this.statement != null) {
            this.statement.close();
            this.statement = null;
        }
    }

}
