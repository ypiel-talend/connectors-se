/*
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
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
package org.talend.components.jdbc.testsuite;

import org.junit.jupiter.api.Test;
import org.talend.components.jdbc.configuration.OutputConfig;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.talend.components.jdbc.configuration.OutputConfig.ActionOnData.BULK_LOAD;
import static org.talend.components.jdbc.configuration.OutputConfig.ActionOnData.DELETE;
import static org.talend.components.jdbc.configuration.OutputConfig.ActionOnData.INSERT;
import static org.talend.components.jdbc.configuration.OutputConfig.ActionOnData.UPDATE;

class ConfigurationTest {

    @Test
    void createTableActivation() {
        final OutputConfig config = new OutputConfig();
        config.setCreateTableIfNotExists(true);
        config.setActionOnData(DELETE.name());
        assertFalse(config.isCreateTableIfNotExists());
        config.setActionOnData(UPDATE.name());
        assertFalse(config.isCreateTableIfNotExists());
        config.setActionOnData(INSERT.name());
        assertTrue(config.isCreateTableIfNotExists());
        config.setActionOnData(BULK_LOAD.name());
        assertTrue(config.isCreateTableIfNotExists());
    }
}
