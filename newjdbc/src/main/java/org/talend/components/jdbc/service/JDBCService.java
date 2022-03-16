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
package org.talend.components.jdbc.service;

import lombok.extern.slf4j.Slf4j;
import org.talend.components.jdbc.datastore.JDBCDataStore;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.connection.CloseConnection;
import org.talend.sdk.component.api.service.connection.CloseConnectionObject;
import org.talend.sdk.component.api.service.connection.CreateConnection;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Slf4j
@Service
public class JDBCService implements Serializable {

    @HealthCheck("CheckConnection")
    public HealthCheckStatus validateBasicDataStore(@Option final JDBCDataStore datastore) {
        return new HealthCheckStatus(HealthCheckStatus.Status.OK, "success message, TODO, i18n");
    }

    @CreateConnection
    public Connection createConnection(@Option final JDBCDataStore datastore) {
        // TODO create jdbc connection
        return null;
    }

    @CloseConnection
    public CloseConnectionObject closeConnection() {
        // TODO create jdbc connection
        return new CloseConnectionObject() {

            public boolean close() {
                // TODO close connection here
                Optional.ofNullable(this.getConnection())
                        .map(Connection.class::cast)
                        .ifPresent(conn -> {
                            try {
                                conn.close();
                            } catch (SQLException e) {
                                // TODO
                            }
                        });
                return true;
            }

        };
    }

}
