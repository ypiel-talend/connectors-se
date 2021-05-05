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
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ContinuePredicateTest {

    @Test
    void doContinue() throws InterruptedException {
        AtomicLong start = new AtomicLong(1_000L);
        final ContinuePredicate predicate = new ContinuePredicate(start::get);
        Assertions.assertFalse(predicate.doContinue());

        start.addAndGet(4_000L);
        Assertions.assertTrue(predicate.doContinue());

        predicate.onNext(true);
        Assertions.assertTrue(predicate.doContinue());
    }

    @Test
    void withResultSet() throws SQLException {
        try (final Connection cnx = DerbyHelper.newConnection()) {
            try (final Statement statement = cnx.createStatement()) {
                statement.execute("CREATE TABLE CONTINUE_TEST(C1 VARCHAR(10))");
                cnx.commit();
            }

            try (final PreparedStatement statement = cnx.prepareStatement("INSERT INTO CONTINUE_TEST(C1) VALUES (?)")) {
                statement.setString(1, "V1");
                statement.addBatch();
                statement.setString(1, "V2");
                statement.addBatch();
                statement.setString(1, "V3");
                statement.addBatch();

                statement.executeBatch();
                cnx.commit();
            }

            final ContinuePredicate ctn = new ContinuePredicate();
            Assertions.assertFalse(ctn.doContinue());
            try (Statement stmt = cnx.createStatement()) {
                final ResultSet resultSet = stmt.executeQuery("SELECT C1 FROM CONTINUE_TEST");

                final ResultSet wrap = ctn.wrap(resultSet);

                final List<String> results = new ArrayList<>(3);
                while (wrap.next()) {
                    results.add(wrap.getString(1));
                }
                Assertions.assertEquals(3, results.size());
                Assertions.assertTrue(ctn.doContinue());
            }
        }
    }


}