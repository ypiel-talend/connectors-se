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
package com.swisscom.component.poc.input;

import com.swisscom.component.poc.config.Config;
import com.swisscom.component.poc.service.SwisscomService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.record.Record;

import java.io.Serializable;

@Version(1)
@Icon(Icon.IconType.STAR)
@Emitter(name = "Input")
@Documentation("")
public class Input implements Serializable {

    private Config config;

    private SwisscomService service;

    private boolean done = false;

    public Input(@Option("configuration") final Config config, final SwisscomService service) {
        this.config = config;
        this.service = service;
    }

    @Producer
    public Entry next() {
        if (done) {
            return null;
        }
        done = true;

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        this.config.getDataset().getCols().stream().forEach(c -> sb.append(c).append(", "));
        sb.append("]");

        return new Entry(0, this.config.getMyconfig() + " : " + this.config.getDataset().getCols().size() + " : " + sb);
    }

    @Data
    @AllArgsConstructor
    public final static class Entry {

        @NonNull
        private int id;

        @NonNull
        private String content;
    }

}
