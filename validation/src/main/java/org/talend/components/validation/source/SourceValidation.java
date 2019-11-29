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
package org.talend.components.validation.source;

import org.talend.components.validation.configuration.Configuration;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.record.Record;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.Serializable;

@Version
@Icon(value = Icon.IconType.CUSTOM, custom = "validation")
@Emitter(name = "Input")
@Documentation("Validation source")
public class SourceValidation implements Serializable {

    private Configuration config;

    public SourceValidation(@Option("configuration") final Configuration configuration) {
        this.config = configuration;
    }

    @PostConstruct
    public void init() throws IOException {
        // Here we can init connections
    }

    @Producer
    public Record next() {
        // provide a record every time it called. return null if there is no more data
        return null;
    }

    @PreDestroy
    public void release() {
        // clean and release any resources
    }
}