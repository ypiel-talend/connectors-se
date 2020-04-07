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
package org.talend.components.jsonconn.conf;

import lombok.Data;
import org.talend.components.common.stream.format.json.JsonConfiguration;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@DataSet("dataset")
@Documentation("")
@Data
@GridLayout({ @GridLayout.Row("datastore"), @GridLayout.Row("format"), @GridLayout.Row("json"), @GridLayout.Row("jsonPointer") })
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {})
public class Dataset extends JsonConfiguration implements Serializable {

    public enum FORMAT {
        RECORD,
        JSON_OBJECT
    }

    @Option
    @Documentation("")
    Datastore datastore;

    @Option
    @Documentation("")
    @Required
    FORMAT format;

    @Option
    @Documentation("")
    @Required
    String json;

}
