/*
 * Copyright (C) 2006-2020 Talend Inc. - www.talend.com
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
package org.talend.components.recordtester.source;

import lombok.extern.slf4j.Slf4j;
import org.talend.components.recordtester.conf.Config;
import org.talend.components.recordtester.conf.Datastore;
import org.talend.components.recordtester.service.GenericService;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;
import java.util.Iterator;

@Slf4j
@Version(1)
@Icon(Icon.IconType.STAR)
@org.talend.sdk.component.api.input.Emitter(name = "genericInput")
@Documentation("")
public class GenericEmitter implements Serializable {

    private final Config config;

    private GenericService service;

    private transient boolean done = false;

    private Iterator<Object> records;

    public GenericEmitter(@Option("configuration") final Config config, final GenericService service) {
        this.service = service;
        this.config = config;
    }

    @Producer
    public Object next() {
        if (done) {
            return null;
        }

        if (records == null) {
            records = service.get(config).iterator();
        }

        if (!records.hasNext()) {
            done = true;
            return null;
        }

        return records.next();
    }

}
