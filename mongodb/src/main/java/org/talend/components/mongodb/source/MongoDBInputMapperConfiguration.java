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

package org.talend.components.mongodb.source;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import org.talend.components.mongodb.dataset.MongoDBDataset;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayouts;
import org.talend.sdk.component.api.configuration.ui.widget.TextArea;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@GridLayouts({
        @GridLayout(value = { @GridLayout.Row({ "dataset" }),
                @GridLayout.Row({ "configurationExtension" }) }, names = GridLayout.FormType.MAIN),
        @GridLayout(value = { @GridLayout.Row({ "configurationExtension" }) }, names = GridLayout.FormType.ADVANCED), })
@Documentation("Configuration for MongoDB input component")
public class MongoDBInputMapperConfiguration implements Serializable {

    @Option
    @Documentation("Dataset to use for MongoDB input component")
    private MongoDBDataset dataset;

    @Option
    @Documentation("Input configuration properties not included in dataset")
    private MongoDBInputConfigurationExtension configurationExtension;

}