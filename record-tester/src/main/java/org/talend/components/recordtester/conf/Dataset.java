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
package org.talend.components.recordtester.conf;

import lombok.Data;
import org.talend.components.common.stream.format.json.JsonConfiguration;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.action.Updatable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Code;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@Data
@DataSet("dataset")
@Documentation("")
@GridLayout({ @GridLayout.Row("datastore"), @GridLayout.Row("splits"), @GridLayout.Row("file"), @GridLayout.Row("dsCodingConfig"),
        @GridLayout.Row("showFeedback"), @GridLayout.Row("feedback") })
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {})
public class Dataset implements Serializable {

    @Option
    @Documentation("")
    Datastore datastore;

    @Option
    @Documentation("")
    @Suggestable(value = "LIST_FILES")
    String file = "";

    @Option
    @Documentation("")
    @Updatable(value = "LOAD_FILE", parameters = { "file" }, after = "provider")
    CodingConfig dsCodingConfig = new CodingConfig();

    @Option
    @Documentation("")
    boolean showFeedback = false;

    @Option
    @Documentation("")
    @ActiveIf(target = "showFeedback", value = "true")
    @Updatable(value = "FEEDBACK_DS", parameters = { "dsCodingConfig" }, after = "feedback")
    @Code("")
    private Feedback feedback = new Feedback();

    @Option
    @Documentation("")
    private Integer splits = 1;

}
