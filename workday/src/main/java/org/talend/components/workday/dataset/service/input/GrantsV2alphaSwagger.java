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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("awardsParameters"), @GridLayout.Row("awardsawardIdParameters"),
        @GridLayout.Row("awardsawardIdLinesParameters") })
@Documentation("Grants (v2Alpha)")
public class GrantsV2alphaSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 2L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("awardStatus"), @GridLayout.Row("effective") })
    public static class awards implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Status of an award. Values - Approved 91b0d382d50848e898733757caa9f84a, Approval in Process 3d46fe27ed464f9aaaddcd510731c5f0")
        private String awardStatus;

        @Option
        @Documentation("Date that signifies which version of the award is effective. Default to current date if not provided")
        private String effective;

        @Override
        public String getServiceToCall() {
            return "grants/v2Alpha/awards/";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.awardStatus != null) {
                queryParam.put("awardStatus", this.awardStatus);
            }
            if (this.effective != null) {
                queryParam.put("effective", this.effective);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("awardId") })
    public static class awardsawardId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Reference-ID(Award_Reference_ID)/WID of an award. You can retrieve the WID of an award by querying the GET/awards API.")
        private String awardId;

        @Override
        public String getServiceToCall() {
            return "grants/v2Alpha/awards/" + this.awardId + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("awardId") })
    public static class awardsawardIdLines implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Reference-ID(Award_Reference_ID)/WID of an award. You can retrieve the WID of an award by querying the GET/awards API.")
        private String awardId;

        @Override
        public String getServiceToCall() {
            return "grants/v2Alpha/awards/" + this.awardId + "/lines";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum GrantsV2alphaSwaggerServiceChoice {
        awards,
        awardsawardId,
        awardsawardIdLines;
    }

    @Option
    @Documentation("selected service")
    private GrantsV2alphaSwaggerServiceChoice service = GrantsV2alphaSwaggerServiceChoice.awards;

    @Option
    @ActiveIf(target = "service", value = "awards")
    @Documentation("parameters")
    private awards awardsParameters = new awards();

    @Option
    @ActiveIf(target = "service", value = "awardsawardId")
    @Documentation("parameters")
    private awardsawardId awardsawardIdParameters = new awardsawardId();

    @Option
    @ActiveIf(target = "service", value = "awardsawardIdLines")
    @Documentation("parameters")
    private awardsawardIdLines awardsawardIdLinesParameters = new awardsawardIdLines();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == GrantsV2alphaSwaggerServiceChoice.awards) {
            return this.awardsParameters;
        }
        if (this.service == GrantsV2alphaSwaggerServiceChoice.awardsawardId) {
            return this.awardsawardIdParameters;
        }
        if (this.service == GrantsV2alphaSwaggerServiceChoice.awardsawardIdLines) {
            return this.awardsawardIdLinesParameters;
        }

        return null;
    }
}
