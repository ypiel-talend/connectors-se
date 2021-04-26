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
package org.talend.components.rejector.component.sink;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.talend.components.rejector.service.I18nMessages;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.AfterGroup;
import org.talend.sdk.component.api.processor.BeforeGroup;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Output;
import org.talend.sdk.component.api.processor.OutputEmitter;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Processor(name = "MultipleInputSink")
@Icon(value = Icon.IconType.CUSTOM, custom = "rejector")
@Version(1)
@Documentation("A connector for testing multiple inputs ina sink.")
@RequiredArgsConstructor
public class MultipleInputSink implements Serializable {

    private final MultipleInputSinkConfiguration configuration;

    private final I18nMessages i18n;

    @PostConstruct
    public void init() {
    }

    @PreDestroy()
    public void release() {
    }

    @BeforeGroup
    public void begin() {
    }

    @ElementListener
    public void bufferize(@Input final Record data
    // ,@Input("input2") final Record data2
    ) {
        log.info(data.toString());
        // log.info(data2.toString());
    }

    @AfterGroup
    public void commit() {
    }
}
