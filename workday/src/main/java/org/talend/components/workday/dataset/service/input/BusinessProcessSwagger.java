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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("eventStepsIDParameters"), @GridLayout.Row("eventsParameters"),
        @GridLayout.Row("eventsIDParameters"), @GridLayout.Row("eventsIDCommentsParameters"),
        @GridLayout.Row("eventsIDCompletedStepsParameters"), @GridLayout.Row("eventsIDInProgressStepsParameters"),
        @GridLayout.Row("eventsIDRemainingStepsParameters") })
@Documentation("Business Process")
public class BusinessProcessSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD") })
    public static class EventStepsID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Workday ID of the step in a business process event.")
        private String iD;

        @Override
        public String getServiceToCall() {
            return "businessProcess/v1/eventSteps/" + this.iD + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("worker"), @GridLayout.Row("status"), @GridLayout.Row("initiator"),
            @GridLayout.Row("businessProcess"), @GridLayout.Row("initiatedOnOrAfter"), @GridLayout.Row("initiatedOnOrBefore"),
            @GridLayout.Row("completedOnOrAfter"), @GridLayout.Row("completedOnOrBefore") })
    public static class Events implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The worker for the event. Use the WID of the worker.")
        private String worker;

        @Option
        @Documentation("The workflow status of the event. Use the WID of the workflow status.")
        private String status;

        @Option
        @Documentation("The worker who initiated the event. Use the WID of the worker.")
        private String initiator;

        @Option
        @Documentation("The business process type of the event. Use the WID of the business process type.")
        private String businessProcess;

        @Option
        @Documentation("The date the event is initiated on or after using the yyyy-mm-dd format.")
        private String initiatedOnOrAfter;

        @Option
        @Documentation("The date the event is initiated on or before using the yyyy-mm-dd format.")
        private String initiatedOnOrBefore;

        @Option
        @Documentation("The date the event is completed on or after using the yyyy-mm-dd format.")
        private String completedOnOrAfter;

        @Option
        @Documentation("The date the event is completed on or before using the yyyy-mm-dd format.")
        private String completedOnOrBefore;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "businessProcess/v1/events";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.worker != null) {
                queryParam.put("worker", this.worker);
            }
            if (this.status != null) {
                queryParam.put("status", this.status);
            }
            if (this.initiator != null) {
                queryParam.put("initiator", this.initiator);
            }
            if (this.businessProcess != null) {
                queryParam.put("businessProcess", this.businessProcess);
            }
            if (this.initiatedOnOrAfter != null) {
                queryParam.put("initiatedOnOrAfter", this.initiatedOnOrAfter);
            }
            if (this.initiatedOnOrBefore != null) {
                queryParam.put("initiatedOnOrBefore", this.initiatedOnOrBefore);
            }
            if (this.completedOnOrAfter != null) {
                queryParam.put("completedOnOrAfter", this.completedOnOrAfter);
            }
            if (this.completedOnOrBefore != null) {
                queryParam.put("completedOnOrBefore", this.completedOnOrBefore);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD") })
    public static class EventsID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Workday ID of the business process event")
        private String iD;

        @Override
        public String getServiceToCall() {
            return "businessProcess/v1/events/" + this.iD + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD") })
    public static class EventsIDComments implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Workday ID of the business process event")
        private String iD;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "businessProcess/v1/events/" + this.iD + "/comments";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD") })
    public static class EventsIDCompletedSteps implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Workday ID of the business process event.")
        private String iD;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "businessProcess/v1/events/" + this.iD + "/completedSteps";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD") })
    public static class EventsIDInProgressSteps implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Workday ID of the business process event.")
        private String iD;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "businessProcess/v1/events/" + this.iD + "/inProgressSteps";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD") })
    public static class EventsIDRemainingSteps implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Workday ID of the business process event.")
        private String iD;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "businessProcess/v1/events/" + this.iD + "/remainingSteps";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum BusinessProcessSwaggerServiceChoice {
        EventStepsID,
        Events,
        EventsID,
        EventsIDComments,
        EventsIDCompletedSteps,
        EventsIDInProgressSteps,
        EventsIDRemainingSteps;
    }

    @Option
    @Documentation("selected service")
    private BusinessProcessSwaggerServiceChoice service = BusinessProcessSwaggerServiceChoice.EventStepsID;

    @Option
    @ActiveIf(target = "service", value = "EventStepsID")
    @Documentation("parameters")
    private EventStepsID eventStepsIDParameters = new EventStepsID();

    @Option
    @ActiveIf(target = "service", value = "Events")
    @Documentation("parameters")
    private Events eventsParameters = new Events();

    @Option
    @ActiveIf(target = "service", value = "EventsID")
    @Documentation("parameters")
    private EventsID eventsIDParameters = new EventsID();

    @Option
    @ActiveIf(target = "service", value = "EventsIDComments")
    @Documentation("parameters")
    private EventsIDComments eventsIDCommentsParameters = new EventsIDComments();

    @Option
    @ActiveIf(target = "service", value = "EventsIDCompletedSteps")
    @Documentation("parameters")
    private EventsIDCompletedSteps eventsIDCompletedStepsParameters = new EventsIDCompletedSteps();

    @Option
    @ActiveIf(target = "service", value = "EventsIDInProgressSteps")
    @Documentation("parameters")
    private EventsIDInProgressSteps eventsIDInProgressStepsParameters = new EventsIDInProgressSteps();

    @Option
    @ActiveIf(target = "service", value = "EventsIDRemainingSteps")
    @Documentation("parameters")
    private EventsIDRemainingSteps eventsIDRemainingStepsParameters = new EventsIDRemainingSteps();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == BusinessProcessSwaggerServiceChoice.EventStepsID) {
            return this.eventStepsIDParameters;
        }
        if (this.service == BusinessProcessSwaggerServiceChoice.Events) {
            return this.eventsParameters;
        }
        if (this.service == BusinessProcessSwaggerServiceChoice.EventsID) {
            return this.eventsIDParameters;
        }
        if (this.service == BusinessProcessSwaggerServiceChoice.EventsIDComments) {
            return this.eventsIDCommentsParameters;
        }
        if (this.service == BusinessProcessSwaggerServiceChoice.EventsIDCompletedSteps) {
            return this.eventsIDCompletedStepsParameters;
        }
        if (this.service == BusinessProcessSwaggerServiceChoice.EventsIDInProgressSteps) {
            return this.eventsIDInProgressStepsParameters;
        }
        if (this.service == BusinessProcessSwaggerServiceChoice.EventsIDRemainingSteps) {
            return this.eventsIDRemainingStepsParameters;
        }

        return null;
    }
}
