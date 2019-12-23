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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("timeClockEventsParameters"),
        @GridLayout.Row("timeClockEventsIDParameters") })
@Documentation("Time Tracking")
public class TimeTrackingSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("fromDate"), @GridLayout.Row("toDate"), @GridLayout.Row("worker") })
    public static class TimeClockEvents implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Start date of the time event.")
        private String fromDate;

        @Option
        @Documentation("End date of the time event.")
        private String toDate;

        @Option
        @Documentation("~Worker~ for the Time Block or Clock Event.")
        private java.util.List<String> worker;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "timeTracking/v1/timeClockEvents";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.fromDate != null) {
                queryParam.put("fromDate", this.fromDate);
            }
            if (this.toDate != null) {
                queryParam.put("toDate", this.toDate);
            }
            if (this.worker != null) {
                queryParam.put("worker", this.worker);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD") })
    public static class TimeClockEventsID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD;

        @Override
        public String getServiceToCall() {
            return "timeTracking/v1/timeClockEvents/" + this.iD + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum queryView {
        timeClockEventView
    }

    public enum TimeTrackingSwaggerServiceChoice {
        TimeClockEvents,
        TimeClockEventsID;
    }

    @Option
    @Documentation("selected service")
    private TimeTrackingSwaggerServiceChoice service = TimeTrackingSwaggerServiceChoice.TimeClockEvents;

    @Option
    @ActiveIf(target = "service", value = "TimeClockEvents")
    @Documentation("parameters")
    private TimeClockEvents timeClockEventsParameters = new TimeClockEvents();

    @Option
    @ActiveIf(target = "service", value = "TimeClockEventsID")
    @Documentation("parameters")
    private TimeClockEventsID timeClockEventsIDParameters = new TimeClockEventsID();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == TimeTrackingSwaggerServiceChoice.TimeClockEvents) {
            return this.timeClockEventsParameters;
        }
        if (this.service == TimeTrackingSwaggerServiceChoice.TimeClockEventsID) {
            return this.timeClockEventsIDParameters;
        }

        return null;
    }
}
