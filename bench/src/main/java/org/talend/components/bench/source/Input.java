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
package org.talend.components.bench.source;

import java.io.Serializable;
import java.util.Iterator;

import org.talend.components.bench.beans.RepeatIterator;
import org.talend.components.bench.config.Config;
import org.talend.components.bench.config.Dataset;
import org.talend.components.bench.service.GenericService;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Version(1)
@Icon(Icon.IconType.STAR)
@Emitter(name = "Input")
@Documentation("")
public class Input implements Serializable {

    private static final long serialVersionUID = 3527316727433565798L;

    private final Config config;

    private final GenericService service;

    private transient Iterator<Object> inputIterator = null;

    public Input(@Option("configuration") Config config, GenericService service) {
        this.config = config;
        this.service = service;
    }

    @Producer
    public Object next() {
        if (this.inputIterator == null) {
            final Dataset dataset = this.config.getDataset();
            final Object exchangeObject = this.service.generate(dataset.getObjectType(), dataset.getObjectSize());
            this.inputIterator = new RepeatIterator(dataset.getSize(), exchangeObject);
        }
        if (!this.inputIterator.hasNext()) {
            return null;
        }
        return this.inputIterator.next();
    }
}