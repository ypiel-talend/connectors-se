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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("feedbackBadgesIDParameters"),
        @GridLayout.Row("workersIDanytimeFeedbackEntriesParameters") })
@Documentation("Performance Management (v1)")
public class PerformanceManagementV1Swagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({})
    public static class FeedbackBadges implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "performanceManagement/v1/feedbackBadges";
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
    public static class FeedbackBadgesID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD;

        @Override
        public String getServiceToCall() {
            return "performanceManagement/v1/feedbackBadges/" + this.iD + "";
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
    public static class WorkersIDanytimeFeedbackEntries implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "performanceManagement/v1/workers/" + this.iD + "/anytimeFeedbackEntries";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum PerformanceManagementV1SwaggerServiceChoice {
        FeedbackBadges,
        FeedbackBadgesID,
        WorkersIDanytimeFeedbackEntries;
    }

    @Option
    @Documentation("selected service")
    private PerformanceManagementV1SwaggerServiceChoice service = PerformanceManagementV1SwaggerServiceChoice.FeedbackBadges;

    private FeedbackBadges feedbackBadgesParameters = new FeedbackBadges();

    @Option
    @ActiveIf(target = "service", value = "FeedbackBadgesID")
    @Documentation("parameters")
    private FeedbackBadgesID feedbackBadgesIDParameters = new FeedbackBadgesID();

    @Option
    @ActiveIf(target = "service", value = "WorkersIDanytimeFeedbackEntries")
    @Documentation("parameters")
    private WorkersIDanytimeFeedbackEntries workersIDanytimeFeedbackEntriesParameters = new WorkersIDanytimeFeedbackEntries();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == PerformanceManagementV1SwaggerServiceChoice.FeedbackBadges) {
            return this.feedbackBadgesParameters;
        }
        if (this.service == PerformanceManagementV1SwaggerServiceChoice.FeedbackBadgesID) {
            return this.feedbackBadgesIDParameters;
        }
        if (this.service == PerformanceManagementV1SwaggerServiceChoice.WorkersIDanytimeFeedbackEntries) {
            return this.workersIDanytimeFeedbackEntriesParameters;
        }

        return null;
    }
}
