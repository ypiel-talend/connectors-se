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
package org.talend.components.workday.dataset.service.input;

import java.io.Serializable;
import java.util.Map;
import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.components.workday.dataset.QueryHelper;

@Data
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("eventTaskDefinitionsIdParameters"),
        @GridLayout.Row("typesIdParameters") })
@Documentation("Custom Business Process Config")
public class CustomBusinessProcessConfigSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({})
    public static class EventTaskDefinitions implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Override
        public String getServiceToCall() {
            return "customBusinessProcessConfig/v1/eventTaskDefinitions";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id") })
    public static class EventTaskDefinitionsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Workday Id of the event task definition")
        private String id;

        @Override
        public String getServiceToCall() {
            return "customBusinessProcessConfig/v1/eventTaskDefinitions/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({})
    public static class Types implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Override
        public String getServiceToCall() {
            return "customBusinessProcessConfig/v1/types";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id") })
    public static class TypesId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Workday Id of the custom business process type")
        private String id;

        @Override
        public String getServiceToCall() {
            return "customBusinessProcessConfig/v1/types/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum CustomBusinessProcessConfigSwaggerServiceChoice {
        EventTaskDefinitions,
        EventTaskDefinitionsId,
        Types,
        TypesId;
    }

    @Option
    @Documentation("selected service")
    private CustomBusinessProcessConfigSwaggerServiceChoice service = CustomBusinessProcessConfigSwaggerServiceChoice.EventTaskDefinitions;

    private EventTaskDefinitions eventTaskDefinitionsParameters = new EventTaskDefinitions();

    @Option
    @ActiveIf(target = "service", value = "EventTaskDefinitionsId")
    @Documentation("parameters")
    private EventTaskDefinitionsId eventTaskDefinitionsIdParameters = new EventTaskDefinitionsId();

    private Types typesParameters = new Types();

    @Option
    @ActiveIf(target = "service", value = "TypesId")
    @Documentation("parameters")
    private TypesId typesIdParameters = new TypesId();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == CustomBusinessProcessConfigSwaggerServiceChoice.EventTaskDefinitions) {
            return this.eventTaskDefinitionsParameters;
        }
        if (this.service == CustomBusinessProcessConfigSwaggerServiceChoice.EventTaskDefinitionsId) {
            return this.eventTaskDefinitionsIdParameters;
        }
        if (this.service == CustomBusinessProcessConfigSwaggerServiceChoice.Types) {
            return this.typesParameters;
        }
        if (this.service == CustomBusinessProcessConfigSwaggerServiceChoice.TypesId) {
            return this.typesIdParameters;
        }

        return null;
    }
}
