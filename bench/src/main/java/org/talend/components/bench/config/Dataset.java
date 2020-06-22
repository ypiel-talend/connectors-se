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
package org.talend.components.bench.config;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@DataSet("dataset")
@Documentation("")
@GridLayout({ //
        @GridLayout.Row("datastore"), //
        @GridLayout.Row("objectType"), //
        @GridLayout.Row("objectSize"), //
        @GridLayout.Row("size"), //
})
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {})
public class Dataset implements Serializable {

    private static final long serialVersionUID = 8820412776155217365L;

    public enum ObjectType {
        JAVA_CLASS,
        RECORD,
        JSON;
    }

    public enum ObjectSize {
        SMALL,
        MEDIUM,
        LARGE,
        LARGE_FLAT;
    }

    @Option
    @Documentation("data store")
    private Datastore datastore;

    @Option
    @Documentation("type of record")
    @DefaultValue("JAVA_CLASS")
    private ObjectType objectType = ObjectType.JAVA_CLASS;

    @Option
    @Documentation("size of record")
    @DefaultValue("LARGE")
    private ObjectSize objectSize = ObjectSize.LARGE;

    @Option
    @Documentation("Number of return record")
    @DefaultValue("1_000_000L")
    private long size = 1_000_000L;

}
