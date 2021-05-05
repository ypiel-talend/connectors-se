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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.derby.jdbc.EmbeddedDriver;

public class DerbyHelper {

    private DerbyHelper() {
    }

    static {
        try {
            DriverManager.registerDriver(new EmbeddedDriver());
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't init derby", ex);
        }
    }

    public static Connection newConnection() throws SQLException {
        final String DATABASE = "jdbc:derby:memory:sampledb;create=true";
        final String USERNAME = "sa";
        final String PASSWORD = "sa";
        return DriverManager.getConnection(DATABASE, USERNAME, PASSWORD);
    }

}
