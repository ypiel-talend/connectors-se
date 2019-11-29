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
package org.talend.components.validation.configuration;

import lombok.Data;
import org.talend.components.validation.service.Service;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@DataStore("ValidationDatastore")
@GridLayout({ //
        @GridLayout.Row("stringParam"), @GridLayout.Row("withquote"), @GridLayout.Row("withTwoQuotes"),
        @GridLayout.Row("withTwoSeparatedQuotes"), })
@Documentation("")
@Checkable(Service.HEALTHCHECK)
@Data
public class Datastore {

    @Option
    @Required
    @Documentation("")
    private String stringParam;

    @Option
    @Documentation("That ' is a simple quote")
    private String withquote;

    @Option
    @Documentation("Those '' are two quotes")
    private String withTwoQuotes;

    @Option
    @Documentation("This ' is a quote and ' another one")
    private String withTwoSeparatedQuotes;
}
