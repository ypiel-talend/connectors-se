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
@Documentation("Performance Enablement (v3)")
public class PerformanceEnablementV3Swagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 3L;

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
            return "performanceEnablement/v3/feedbackBadges";
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
            return "performanceEnablement/v3/feedbackBadges/" + this.iD + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("relatesTo"), @GridLayout.Row("iD") })
    public static class WorkersIDanytimeFeedbackEntries implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The talent tag that relates to the feedback response for feedback given.")
        private String relatesTo;

        @Option
        @Documentation("Id of specified instance")
        private String iD;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "performanceEnablement/v3/workers/" + this.iD + "/anytimeFeedbackEntries";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.relatesTo != null) {
                queryParam.put("relatesTo", this.relatesTo);
            }
            return queryParam;
        }
    }

    public enum PerformanceEnablementV3SwaggerServiceChoice {
        FeedbackBadges,
        FeedbackBadgesID,
        WorkersIDanytimeFeedbackEntries;
    }

    @Option
    @Documentation("selected service")
    private PerformanceEnablementV3SwaggerServiceChoice service = PerformanceEnablementV3SwaggerServiceChoice.FeedbackBadges;

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
        if (this.service == PerformanceEnablementV3SwaggerServiceChoice.FeedbackBadges) {
            return this.feedbackBadgesParameters;
        }
        if (this.service == PerformanceEnablementV3SwaggerServiceChoice.FeedbackBadgesID) {
            return this.feedbackBadgesIDParameters;
        }
        if (this.service == PerformanceEnablementV3SwaggerServiceChoice.WorkersIDanytimeFeedbackEntries) {
            return this.workersIDanytimeFeedbackEntriesParameters;
        }

        return null;
    }
}
