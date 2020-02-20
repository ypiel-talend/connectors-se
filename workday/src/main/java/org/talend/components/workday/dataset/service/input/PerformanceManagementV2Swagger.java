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
        @GridLayout.Row("workersIDanytimeFeedbackEntriesParameters"),
        @GridLayout.Row("workersIDanytimeFeedbackEntriesSubresourceIDParameters") })
@Documentation("Performance Management (v2)")
public class PerformanceManagementV2Swagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 2L;

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
            return "performanceManagement/v2/feedbackBadges";
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
        @Documentation("The Workday ID of the Feedback Badge.")
        private String iD;

        @Override
        public String getServiceToCall() {
            return "performanceManagement/v2/feedbackBadges/" + this.iD + "";
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
        @Documentation("The Workday ID of the Worker.")
        private String iD;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "performanceManagement/v2/workers/" + this.iD + "/anytimeFeedbackEntries";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD"), @GridLayout.Row("subresourceID") })
    public static class WorkersIDanytimeFeedbackEntriesSubresourceID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Workday ID of the Worker.")
        private String iD;

        @Option
        @Documentation("The Workday ID of the Feedback Given instance.")
        private String subresourceID;

        @Override
        public String getServiceToCall() {
            return "performanceManagement/v2/workers/" + this.iD + "/anytimeFeedbackEntries/" + this.subresourceID + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum PerformanceManagementV2SwaggerServiceChoice {
        FeedbackBadges,
        FeedbackBadgesID,
        WorkersIDanytimeFeedbackEntries,
        WorkersIDanytimeFeedbackEntriesSubresourceID;
    }

    @Option
    @Documentation("selected service")
    private PerformanceManagementV2SwaggerServiceChoice service = PerformanceManagementV2SwaggerServiceChoice.FeedbackBadges;

    private FeedbackBadges feedbackBadgesParameters = new FeedbackBadges();

    @Option
    @ActiveIf(target = "service", value = "FeedbackBadgesID")
    @Documentation("parameters")
    private FeedbackBadgesID feedbackBadgesIDParameters = new FeedbackBadgesID();

    @Option
    @ActiveIf(target = "service", value = "WorkersIDanytimeFeedbackEntries")
    @Documentation("parameters")
    private WorkersIDanytimeFeedbackEntries workersIDanytimeFeedbackEntriesParameters = new WorkersIDanytimeFeedbackEntries();

    @Option
    @ActiveIf(target = "service", value = "WorkersIDanytimeFeedbackEntriesSubresourceID")
    @Documentation("parameters")
    private WorkersIDanytimeFeedbackEntriesSubresourceID workersIDanytimeFeedbackEntriesSubresourceIDParameters = new WorkersIDanytimeFeedbackEntriesSubresourceID();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == PerformanceManagementV2SwaggerServiceChoice.FeedbackBadges) {
            return this.feedbackBadgesParameters;
        }
        if (this.service == PerformanceManagementV2SwaggerServiceChoice.FeedbackBadgesID) {
            return this.feedbackBadgesIDParameters;
        }
        if (this.service == PerformanceManagementV2SwaggerServiceChoice.WorkersIDanytimeFeedbackEntries) {
            return this.workersIDanytimeFeedbackEntriesParameters;
        }
        if (this.service == PerformanceManagementV2SwaggerServiceChoice.WorkersIDanytimeFeedbackEntriesSubresourceID) {
            return this.workersIDanytimeFeedbackEntriesSubresourceIDParameters;
        }

        return null;
    }
}
