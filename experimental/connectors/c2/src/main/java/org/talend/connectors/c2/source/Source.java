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
package org.talend.connectors.c2.source;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.talend.poc.testlib.Version;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@org.talend.sdk.component.api.component.Version(1)
@Icon(value = Icon.IconType.STAR)
@Emitter(name = "Input")
@Documentation("Input component")
public class Source implements Serializable {

    private final SourceConfig config;

    private final RecordBuilderFactory factory;

    private int counter;

    public Source(@Option("configuration") final SourceConfig config, final RecordBuilderFactory factory) {
        this.config = config;
        this.factory = factory;
    }

    @PostConstruct
    public void init() {
        this.counter = 0;
    }

    @Producer
    public Record next() {
        if (this.counter < this.config.getDataset().getNbeRecord()) {
            final Version v = new Version();
            log.info("Connector C2, version : " + v.getVersion());
            this.counter++;
            return factory.newRecordBuilder().withInt("count", counter) //
                    .withString("version", v.getVersion()) //
                    .withString("source", "C2") //
                    .build();
        }
        return null;
    }
}
