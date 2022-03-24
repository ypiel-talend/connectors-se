/*
 * Copyright (C) 2006-2022 Talend Inc. - www.talend.com
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
package org.talend.components.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayouts;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@GridLayouts({ @GridLayout({ @GridLayout.Row({ "key" }), @GridLayout.Row({ "value" }) }) })
@Documentation("Connection parameter")
public class ConnectionParameter implements Serializable {

    // TODO make it to a closedlist to choose? not good as not flexable if driver change?
    @Option
    @Documentation("key")
    private String key;

    @Option
    @Documentation("value")
    private String value;

}
