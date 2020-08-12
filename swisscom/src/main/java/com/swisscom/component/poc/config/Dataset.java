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
package com.swisscom.component.poc.config;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Structure;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;
import java.util.List;

@Data
@DataSet("Dataset")
@GridLayout({ @GridLayout.Row({ "connection" }), @GridLayout.Row({ "nb" }), @GridLayout.Row({ "cols" }) })
@Documentation("")
public class Dataset implements Serializable {

    @Option
    @Required
    @Documentation("")
    @DefaultValue("1")
    private Integer nb = 1;

    @Option
    @Structure(type = Structure.Type.OUT, discoverSchema = "discover")
    @Documentation("")
    private List<String> cols;

    @Option
    @Documentation("")
    private Connection connection;

}
