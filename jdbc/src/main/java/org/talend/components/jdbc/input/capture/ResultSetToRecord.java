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

import static org.talend.sdk.component.api.record.Schema.Type.RECORD;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.talend.components.jdbc.service.JdbcService;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResultSetToRecord implements AutoCloseable {

    private final Supplier<ResultSet> resultSetGetter;

    private ResultSet currentResultSet = null;

    private Schema schema = null;

    private final RecordBuilderFactory factory;

    private final JdbcService jdbcDriversService;

    public Record tryNext() throws SQLException {
        if (!this.hasNext()) {
            return null;
        }
        final ResultSetMetaData metaData = this.currentResultSet.getMetaData();
        if (schema == null) {
            final Schema.Builder schemaBuilder = this.factory.newSchemaBuilder(RECORD);
            IntStream.rangeClosed(1, metaData.getColumnCount())
                    .forEach(index -> jdbcDriversService.addField(schemaBuilder, metaData, index));
            schema = schemaBuilder.build();
        }

        final Record.Builder recordBuilder = this.factory.newRecordBuilder(schema);
        IntStream.rangeClosed(1, metaData.getColumnCount())
                .forEach(index -> jdbcDriversService.addColumn(recordBuilder, metaData, index, this.currentResultSet));

        return recordBuilder.build();

    }

    @Override
    public void close() throws SQLException {
        if (this.currentResultSet != null) {
            this.currentResultSet.close();
        }
    }

    private boolean hasNext() throws SQLException {
        if (this.currentResultSet == null) {
            this.currentResultSet = this.resultSetGetter.get();
        }
        return this.currentResultSet != null && this.currentResultSet.next();
    }

}
