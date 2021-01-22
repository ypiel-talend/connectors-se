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
package org.talend.components.jdbc.output;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.talend.components.jdbc.configuration.OutputConfig;
import org.talend.components.jdbc.output.platforms.Platform;
import org.talend.components.jdbc.output.platforms.PlatformFactory;
import org.talend.components.jdbc.output.statement.QueryManagerFactory;
import org.talend.components.jdbc.output.statement.operations.QueryManagerImpl;
import org.talend.components.jdbc.service.I18nMessage;
import org.talend.components.jdbc.service.JdbcService;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.MigrationHandler;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.AfterGroup;
import org.talend.sdk.component.api.processor.BeforeGroup;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;

import static org.talend.components.jdbc.ErrorFactory.toIllegalStateException;

@Slf4j
@Getter
@Version(value = 2, migrationHandler = SimpleOutput.Migration.class)
@Processor(name = "Output")
@Icon(value = Icon.IconType.DATASTORE)
@Documentation("JDBC Output component")
public class SimpleOutput extends Output implements Serializable {

    private QueryManagerImpl queryManager;

    private Platform platform;

    @Getter
    private transient List<Record> records;

    public SimpleOutput(@Option("configuration") final OutputConfig configuration, final JdbcService jdbcService,
            final I18nMessage i18n) {
        super(configuration, jdbcService, i18n);
        this.platform = PlatformFactory.get(configuration.getDataset().getConnection(), i18n);
        this.queryManager = (QueryManagerImpl) QueryManagerFactory.getQueryManager(platform, i18n, configuration);
    }

    @BeforeGroup
    public void beforeGroup() throws Exception {
        this.records = new ArrayList<>();
    }

    @ElementListener
    public void elementListener(@Input final Record record) throws Exception {
        super.elementListener(record);
        records.add(record);
    }

    @AfterGroup
    public void afterGroup() throws Exception {
        checkForTable();

        // TODO : handle discarded records
        try {
            final List<Reject> discards = getQueryManager().execute(records, getDatasource());
            discards.stream().map(Object::toString).forEach(log::error);
        } catch (final SQLException e) {
            records.stream().map(r -> new Reject(e.getMessage(), r)).map(Reject::toString).forEach(log::error);
            throw toIllegalStateException(e);
        }
    }

    @Slf4j
    public static class Migration implements MigrationHandler {

        @Override
        public Map<String, String> migrate(int incomingVersion, Map<String, String> incomingData) {
            log.debug("Starting JDBC sink component migration");

            if (incomingVersion == 1) {
                final String old_property_path_prefix = "configuration.keys[";
                final String new_property_path_prefix = "configuration.keys.keys[";

                Map<String, String> correct_config = new HashMap<>();
                incomingData.forEach((k, v) -> {
                    if (k.startsWith(old_property_path_prefix)) {
                        correct_config.put(k.replace(old_property_path_prefix, new_property_path_prefix), v);
                    } else {
                        correct_config.put(k, v);
                    }
                });

                return correct_config;
            }

            return incomingData;
        }
    }

    @PreDestroy
    public void preDestroy() {
        super.preDestroy();
    }
}