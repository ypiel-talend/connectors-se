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
package org.talend.components.jdbc.suite.containers;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.talend.components.jdbc.configuration.OutputConfig;
import org.talend.components.jdbc.containers.JdbcTestContainer;
import org.talend.components.jdbc.containers.MariaDBTestContainer;
import org.talend.components.jdbc.suite.AbstractBaseJDBC;
import org.talend.components.jdbc.suite.JDBCBaseContainerTest;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.manager.chain.Job;

import static org.apache.derby.vti.XmlVTI.asList;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

@Tag("IT")
public class MariaContainerTest extends JDBCBaseContainerTest {

    @Override
    public JdbcTestContainer buildContainer() {
        MariaDBTestContainer container = new MariaDBTestContainer();
        container.addParameter("allowLocalInfile", "true");
        container.addEnv("local-infile", "1");
        return container;
    }

    public class MariaPlatform extends PlatformTests {
    }

    public class MariaInput extends InputTest {
    }

    public class MariaOutput extends OutputTest {
    }

    public class MariaUI extends UIActionServiceTest {
    }

    @Nested
    @DisplayName("BulkOutput")
    @WithComponents("org.talend.components.jdbc")
    public class MariaBulk extends AbstractBaseJDBC {

        private boolean withBoolean;

        private boolean withBytes;

        @BeforeEach
        void beforeEach() {
            withBoolean = !MariaContainerTest.this.getContainer().getDatabaseType().equalsIgnoreCase("oracle");
            withBytes = !MariaContainerTest.this.getContainer().getDatabaseType().equalsIgnoreCase("redshift");
        }

        @Override
        public JdbcTestContainer getContainer() {
            return MariaContainerTest.this.getContainer();
        }

        // @Test
        @DisplayName("Bulk Insert - valid use case")
        void bulkInsert(final TestInfo testInfo) {
            final OutputConfig configuration = new OutputConfig();
            final String testTableName = getTestTableName(testInfo);
            configuration.setDataset(newTableNameDataset(testTableName));
            String url = configuration.getDataset().getConnection().getJdbcUrl();
            System.out.println(url);
            url = url + "?allowLocalInfile=true";
            configuration.getDataset().getConnection().setJdbcUrl(url);
            configuration.setActionOnData(OutputConfig.ActionOnData.BULK_LOAD.name());
            configuration.setCreateTableIfNotExists(true);
            configuration.setKeys(asList("id"));
            final String config = configurationByExample().forInstance(configuration).configured().toQueryString();

            final int rowCount = 50;
            Job.components()
                    .component("rowGenerator",
                            "jdbcTest://RowGenerator?" + rowGeneratorConfig(rowCount, false, null, withBoolean, withBytes))
                    .component("jdbcBulkOutput", "Jdbc://BulkOutput?" + config).connections().from("rowGenerator")
                    .to("jdbcBulkOutput").build().run();
            Assert.assertEquals(rowCount, countAll(testTableName));
        }

    }

}
