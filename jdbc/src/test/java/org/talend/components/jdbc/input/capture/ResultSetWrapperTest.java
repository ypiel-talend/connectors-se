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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ResultSetWrapperTest {

    @Test
    void next() throws SQLException {

        try (final Connection connection = DerbyHelper.newConnection()) {
            try (final Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE TABLE_TEST(C1 VARCHAR(10))");
                connection.commit();
            }

            try (final PreparedStatement statement = connection.prepareStatement("INSERT INTO TABLE_TEST(C1) VALUES (?)")) {
                statement.setString(1, "V1");
                statement.addBatch();
                statement.setString(1, "V2");
                statement.addBatch();
                statement.setString(1, "V3");
                statement.addBatch();

                statement.executeBatch();
                connection.commit();
            }

            try (final Statement statement = connection.createStatement();
                    final ResultSet resultSet = statement.executeQuery("SELECT C1 FROM TABLE_TEST")) {
                final List<Boolean> result = new ArrayList<>(4);
                final List<String> lines = new ArrayList<>(4);
                ResultSetWrapper wrapper = new ResultSetWrapper(resultSet, result::add);
                while (wrapper.next()) {
                    lines.add(wrapper.getString(1));
                }
                Assertions.assertEquals(4, result.size());
                Assertions.assertEquals(3, lines.size());
                Assertions.assertTrue(result.get(0) && result.get(1) && result.get(2));
                Assertions.assertFalse(result.get(3));
                Assertions.assertEquals("V1", lines.get(0));
                Assertions.assertEquals("V2", lines.get(1));
                Assertions.assertEquals("V3", lines.get(2));
            }
        }

    }
}