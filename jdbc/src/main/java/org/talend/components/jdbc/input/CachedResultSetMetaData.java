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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CachedResultSetMetaData implements ResultSetMetaData {

    final ResultSetMetaData metaData;

    private final Map<Integer, Integer> ct = new HashMap<>();

    private final Map<Integer, Integer> mp = new HashMap<>();

    private final Map<Integer, String> cn = new HashMap<>();

    @Override
    public String toString() {
        return ct.toString() + mp.toString() + cn.toString();
    }

    @Override
    public int getColumnCount() throws SQLException {
        return metaData.getColumnCount();
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        throw new IllegalArgumentException();
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        throw new IllegalArgumentException();
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        throw new IllegalArgumentException();
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        throw new IllegalArgumentException();
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return mp.computeIfAbsent(column, c -> {
            try {
                return metaData.isNullable(c);
            } catch (SQLException e) {
                throw new IllegalArgumentException(e);
            }
        });
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        throw new IllegalArgumentException();
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        throw new IllegalArgumentException();
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        throw new IllegalArgumentException();
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return cn.computeIfAbsent(column, c -> {
            try {
                return metaData.getColumnName(c);
            } catch (SQLException e) {
                throw new IllegalArgumentException(e);
            }
        });
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        throw new IllegalArgumentException();
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        throw new IllegalArgumentException();
    }

    @Override
    public int getScale(int column) throws SQLException {
        throw new IllegalArgumentException();
    }

    @Override
    public String getTableName(int column) throws SQLException {
        throw new IllegalArgumentException();
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        throw new IllegalArgumentException();
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        return ct.computeIfAbsent(column, c -> {
            try {
                return metaData.getColumnType(c);
            } catch (SQLException e) {
                throw new IllegalArgumentException(e);
            }
        });
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        throw new IllegalArgumentException();
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        throw new IllegalArgumentException();
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        throw new IllegalArgumentException();
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        throw new IllegalArgumentException();
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        return metaData.getColumnClassName(column);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new IllegalArgumentException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new IllegalArgumentException();
    }
}
