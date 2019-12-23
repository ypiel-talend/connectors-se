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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("eventsParameters"), @GridLayout.Row("eventsIdParameters") })
@Documentation("Custom Business Process")
public class CustomBusinessProcessSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("customBusinessProcessType"), @GridLayout.Row("for0"), @GridLayout.Row("status"),
            @GridLayout.Row("initiatedOnOrAfter"), @GridLayout.Row("initiatedOnOrBefore") })
    public static class Events implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The ID of the Custom Business Process type. Custom Business Process type values can be found through the Types resource in the Custom Business Process Config service.")
        private String customBusinessProcessType;

        @Option
        @Documentation("The worker these events are about. Worker IDs can be found through the Workers resource in the Human Resource Management service.")
        private String for0;

        @Option
        @Documentation("The workflow status of the event. Use value service to find all possible options.")
        private String status;

        @Option
        @Documentation("Field to filter event initiated on or after this date")
        private String initiatedOnOrAfter;

        @Option
        @Documentation("Field to filter event initiated on or before this date")
        private String initiatedOnOrBefore;

        @Override
        public String getServiceToCall() {
            return "customBusinessProcess/v1/events";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.customBusinessProcessType != null) {
                queryParam.put("customBusinessProcessType", this.customBusinessProcessType);
            }
            if (this.for0 != null) {
                queryParam.put("for0", this.for0);
            }
            if (this.status != null) {
                queryParam.put("status", this.status);
            }
            if (this.initiatedOnOrAfter != null) {
                queryParam.put("initiatedOnOrAfter", this.initiatedOnOrAfter);
            }
            if (this.initiatedOnOrBefore != null) {
                queryParam.put("initiatedOnOrBefore", this.initiatedOnOrBefore);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id") })
    public static class EventsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Workday ID of the Custom Business Process event")
        private String id;

        @Override
        public String getServiceToCall() {
            return "customBusinessProcess/v1/events/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum CustomBusinessProcessSwaggerServiceChoice {
        Events,
        EventsId;
    }

    @Option
    @Documentation("selected service")
    private CustomBusinessProcessSwaggerServiceChoice service = CustomBusinessProcessSwaggerServiceChoice.Events;

    @Option
    @ActiveIf(target = "service", value = "Events")
    @Documentation("parameters")
    private Events eventsParameters = new Events();

    @Option
    @ActiveIf(target = "service", value = "EventsId")
    @Documentation("parameters")
    private EventsId eventsIdParameters = new EventsId();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == CustomBusinessProcessSwaggerServiceChoice.Events) {
            return this.eventsParameters;
        }
        if (this.service == CustomBusinessProcessSwaggerServiceChoice.EventsId) {
            return this.eventsIdParameters;
        }

        return null;
    }
}
