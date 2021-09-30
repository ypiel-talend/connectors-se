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
package org.talend.components.mongo.dataset;

import lombok.Data;
import org.talend.components.mongo.Mode;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@GridLayout({ @GridLayout.Row({ "collection" }), @GridLayout.Row({ "mode" }) })
@Documentation("Mongo common dataSet for mongodb and docdb")
public class MongoCommonDataSet {

    @Option
    @Required
    @Documentation("Collection")
    private String collection;

    @Option
    @Required
    @Documentation("Mode")
    private Mode mode = Mode.JSON;
}