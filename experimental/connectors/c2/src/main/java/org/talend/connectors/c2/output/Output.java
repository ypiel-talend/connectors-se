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
package org.talend.connectors.c2.output;

import java.io.Serializable;

import org.talend.poc.testlib.Version;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@org.talend.sdk.component.api.component.Version(1)
@Icon(value = Icon.IconType.STAR)
@Processor(name = "Output")
@Documentation("Output component")
public class Output implements Serializable {

    private final OutputConfig config;

    public Output(@Option("configuration") final OutputConfig config) {
        this.config = config;
    }

    @ElementListener
    public void process(final Record input) {
        final Version v = new Version();
        log.info("C2 output, Record with version '{}' for version '{}'", input.getString("version"), v.getVersion());
    }
}
