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
package org.talend.components.rejector.component.processor;

import java.io.Serializable;
import java.util.Random;

import org.talend.components.rejector.service.I18nMessages;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Output;
import org.talend.sdk.component.api.processor.OutputEmitter;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Processor(name = "RejectorProcessorTest")
@Icon(value = Icon.IconType.CUSTOM, custom = "rejector")
@Version(1)
@Documentation("A connector for testing rejects in Studio.")
@RequiredArgsConstructor
public class TestProcessor implements Serializable {

    private final TestProcessorConfiguration configuration;

    private final I18nMessages i18n;

    @ElementListener
    public void bufferize(final Record data, @Output("main") OutputEmitter<Record> main
    // ,@Output("REJECT") OutputEmitter<Record> reject
    ) {
        main.emit(data);
    }

}
