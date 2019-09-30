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
package org.talend.components.slack.input;

import lombok.extern.slf4j.Slf4j;
import org.talend.components.slack.service.SlackService;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Icon.IconType;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.*;
import org.talend.sdk.component.api.meta.Documentation;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Slf4j
@Version
@Icon(value = IconType.CUSTOM, custom = "SlackInput")
@PartitionMapper(family = "Slack", name = "SlackUserInput", infinite = true)
@Documentation("Slack Input Component")
public class SlackUserInputMapper implements Serializable {

    private SlackUserInputConfiguration configuration;

    private SlackService service;

    public SlackUserInputMapper(@Option("configuration") final SlackUserInputConfiguration configuration, //
            final SlackService service) {
        this.configuration = configuration;
        this.service = service;
        log.debug("[SlackUserInputMapper] {}", configuration);
    }

    @PostConstruct
    public void init() {
        // NOOP
    }

    @Assessor
    public long estimateSize() {
        return 300;
    }

    @Split
    public List<SlackUserInputMapper> split(@PartitionSize final long bundles) {
        return Collections.singletonList(this);
    }

    @Emitter
    public SlackUserSource createWorker() {
        return new SlackUserSource(configuration, service);
    }

}
