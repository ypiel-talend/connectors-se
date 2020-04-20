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
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.action.Updatable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@Data
@GridLayout({ @GridLayout.Row("dataset"), @GridLayout.Row("overwriteDataset"), @GridLayout.Row("file"),
        @GridLayout.Row("justLoadFile"), @GridLayout.Row("codingConfig"), @GridLayout.Row("showFeedback"),
        @GridLayout.Row("feedback") })
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {})
public class Config implements Serializable {

    @Option
    @Documentation("")
    Dataset dataset;

    @Option
    @Documentation("")
    @ActiveIf(target = "overwriteDataset", value = "true")
    @Suggestable(value = "LIST_FILES")
    String file = "";

    @Option
    @Updatable(value = "COPY", parameters = { "file", "justLoadFile" }, after = "provider")
    @Documentation("")
    @ActiveIf(target = "overwriteDataset", value = "true")
    CodingConfig codingConfig = new CodingConfig();

    @Option
    @Documentation("")
    boolean overwriteDataset = false;

    @Option
    @Documentation("")
    @ActiveIf(target = "overwriteDataset", value = "true")
    boolean justLoadFile = false;

    @Option
    @Documentation("")
    @ActiveIf(target = "overwriteDataset", value = "true")
    boolean showFeedback = false;

    @Option
    @Documentation("")
    @ActiveIf(target = "showFeedback", value = "true")
    @ActiveIf(target = "overwriteDataset", value = "true")
    @Updatable(value = "FEEDBACK", parameters = { "overwriteDataset", "codingConfig" }, after = "feedback")
    private Feedback feedback = new Feedback();

    public void afac(final String param)

    {

        System.out.println("sdsdsd" + "fff" + "dfdf" + "ffff" + "mmm");
    }

}
