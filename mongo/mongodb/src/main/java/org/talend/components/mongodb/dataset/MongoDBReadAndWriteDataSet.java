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
package org.talend.components.mongodb.dataset;

import lombok.Data;
import org.talend.components.mongo.dataset.MongoCommonDataSet;
import org.talend.components.mongodb.datastore.MongoDBDataStore;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@Version(1)
@Data
@Icon(value = Icon.IconType.CUSTOM, custom = "mongo_db-connector")
@DataSet("MongoDBReadAndWriteDataSet")
@GridLayout({ @GridLayout.Row({ "datastore" }) })
// @GridLayout(names = GridLayout.FormType.ADVANCED, value = { @GridLayout.Row({ "todo" }) })
@Documentation("MongoDB DataSet for read and write both")
public class MongoDBReadAndWriteDataSet extends MongoCommonDataSet implements BaseDataSet {

    @Option
    @Documentation("Connection")
    private MongoDBDataStore datastore;
}