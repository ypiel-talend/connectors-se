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
package org.talend.components.jdbc.output.statement.operations.mariadb;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

import org.talend.components.jdbc.configuration.OutputConfig;
import org.talend.components.jdbc.output.Reject;
import org.talend.components.jdbc.output.platforms.Platform;
import org.talend.components.jdbc.output.statement.QueryManager;
import org.talend.components.jdbc.service.I18nMessage;
import org.talend.components.jdbc.service.JdbcService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MariaDBBulkUpload implements QueryManager<Path> {

    private final Platform platform;

    private final OutputConfig configuration;

    private final I18nMessage i18n;

    @Override
    public List<Reject> execute(Path source, JdbcService.JdbcDatasource dataSource) throws SQLException, IOException {
        try (final Connection connection = dataSource.getConnection()) {
            String query = "LOAD DATA LOCAL INFILE '" + source + "' INTO TABLE " + configuration.getDataset().getTableName()
                    + ";";
            try (Statement stmnt = connection.createStatement()) {
                stmnt.executeUpdate(query);
            }
        }
        return Collections.emptyList();
    }

}
