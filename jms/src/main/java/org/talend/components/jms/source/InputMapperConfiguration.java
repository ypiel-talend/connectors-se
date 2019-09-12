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
package org.talend.components.jms.source;

import lombok.Data;
import org.talend.components.jms.configuration.BasicConfiguration;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Min;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@GridLayout(value = { @GridLayout.Row({ "basicConfig" }), @GridLayout.Row({ "timeout" }),
        @GridLayout.Row({ "messageSelector" }) }, names = GridLayout.FormType.MAIN)
@Documentation("Main configuration class for JMSInput component")
@Data
public class InputMapperConfiguration implements Serializable {

    @Option
    @Documentation("Common basicConfig")
    private BasicConfiguration basicConfig;

    @Option
    @Min(0)
    @Documentation("JMS receive message timeout. A timeout of zero never expires, and the call blocks indefinitely.")
    private Integer timeout = 0;

    @Option
    @Documentation("Message Selector Expression used to receive only messages whose headers and properties match the selector")
    private String messageSelector;

    private boolean doAcknowledge = true;

}