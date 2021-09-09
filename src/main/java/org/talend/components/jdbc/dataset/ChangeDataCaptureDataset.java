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
package org.talend.components.jdbc.dataset;

import lombok.Data;
import lombok.experimental.Delegate;

import org.talend.components.jdbc.datastore.JdbcConnection;
import org.talend.components.jdbc.output.platforms.Platform;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.action.Updatable;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.util.HashMap;
import java.util.Map;

import static org.talend.components.jdbc.service.UIActionService.ACTION_DEFAULT_VALUES;
import static org.talend.components.jdbc.service.UIActionService.ACTION_SUGGESTION_TABLE_NAMES;
import static org.talend.sdk.component.api.configuration.ui.layout.GridLayout.FormType.ADVANCED;

@Data
@DataSet("ChangeDataCaptureDataset")
@GridLayout({ @GridLayout.Row("connection"), @GridLayout.Row("tableName"), @GridLayout.Row("streamTableName") })
@GridLayout(names = ADVANCED, value = { @GridLayout.Row("connection"), @GridLayout.Row("advancedCommon") })
@Documentation("This configuration define a dataset using a from a Snowflake stream table.\n")
public class ChangeDataCaptureDataset implements BaseDataSet {

    /**
     * For now only Snowflake
     */

    @Option
    @Documentation("the connection information to execute the query")
    @Updatable(value = ACTION_DEFAULT_VALUES, parameters = { "." }, after = "setRawUrl")
    private JdbcConnection connection;

    @Option
    @Required
    @Documentation("The table name")
    @Suggestable(value = ACTION_SUGGESTION_TABLE_NAMES, parameters = "connection")
    private String tableName;

    @Option
    @Required
    @Documentation("The stream table name")
    private String streamTableName;

    @Option
    @Delegate
    @Documentation("common input configuration")
    private AdvancedCommon advancedCommon = new AdvancedCommon();

    @Override
    public String getQuery(final Platform platform) {
        // No need for the i18n service for this instance
        return "select * from " + platform.identifier(getStreamTableName());
    }

    // Snowflake CDC specific !!!
    public String createStreamTableIfNotExist(final Platform platform) {
        return "create stream if not exists " + getQN(streamTableName, platform) + " on table "
                + getQN(tableName, platform);
    }

    // Snowflake CDC specific !!!
    public String createCounterTableIfNotExist(final Platform platform) {
        return "create table if not exists " + getQN(getCounterTableName(streamTableName), platform) + "(c number(8))";
    }

    public String createStatementConsumeStreamTable(final Platform platform) {
        return "insert into " + getQN(getCounterTableName(streamTableName), platform) + "(c) "
                + " select count(*) from "
                + getQN(streamTableName, platform);
    }

    private String getCounterTableName(String streamTableName) {
        return streamTableName + "_COUNTER";
    }

    private String getQN(String table, final Platform platform) {
        String jdbcUrl = platform.buildUrl(connection);
        String[] splitParts = jdbcUrl.split("\\?");
        if (splitParts.length == 1)
            return table;
        else {
            String queryParamsAsString = splitParts[1];
            String[] queryParamsStringSplit = queryParamsAsString.split("&");
            Map<String, String> queryParamsMap = new HashMap<String, String>();
            for (String part : queryParamsStringSplit) {
                String[] keyAndValue = part.split("=");
                if (keyAndValue.length >= 2) {
                    String key = keyAndValue[0];
                    String value = keyAndValue[1];
                    queryParamsMap.put(key, value);
                }
            }

            String db = queryParamsMap.get("db");
            String schema = queryParamsMap.get("schema");
            String qn = table;
            if (schema != null && !schema.isEmpty())
                qn = schema + "." + table;
            if (db != null && !db.isEmpty())
                qn = db + "." + qn;

            return qn;
        }

    }

}
