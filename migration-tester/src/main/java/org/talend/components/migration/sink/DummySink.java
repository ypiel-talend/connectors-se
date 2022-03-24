/*
 * Copyright (C) 2006-2022 Talend Inc. - www.talend.com
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
package org.talend.components.migration.sink;

import lombok.extern.slf4j.Slf4j;
import org.talend.components.migration.conf.AbstractConfig;
import org.talend.components.migration.conf.SinkConfig;
import org.talend.components.migration.migration.SinkMigrationHandler;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;

import java.io.Serializable;

@Slf4j
@Version(value = AbstractConfig.VERSION, migrationHandler = SinkMigrationHandler.class)
@Icon(Icon.IconType.STAR)
@Processor(name = "DummySink")
@Documentation("")
public class DummySink implements Serializable {

    private final SinkConfig config;

    public DummySink(@Option("configuration") final SinkConfig config) {
        this.config = config;
    }

    @ElementListener
    public void doNothing(final Record in) {
        try {
            config.getDse().getDso().getDso_shouldNotBeEmpty().trim();
        } catch (Exception e) {
            throw new IllegalArgumentException("The dso_shouldNotBeEmpty property is not set !", e);
        }

        log.info("Migration tester sink configuration :\n" + config);
    }

}
