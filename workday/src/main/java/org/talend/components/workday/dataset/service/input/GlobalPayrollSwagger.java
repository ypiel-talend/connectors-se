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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("payGroupsPaygroupIdParameters"),
        @GridLayout.Row("payGroupsPaygroupIdPeriodsParameters"),
        @GridLayout.Row("payGroupsPaygroupIdPeriodsPeriodIdParameters") })
@Documentation("Global Payroll")
public class GlobalPayrollSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({})
    public static class PayGroups implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "globalPayroll/v1/payGroups";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("paygroupId") })
    public static class PayGroupsPaygroupId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String paygroupId;

        @Override
        public String getServiceToCall() {
            return "globalPayroll/v1/payGroups/" + this.paygroupId + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("paygroupId"), @GridLayout.Row("periodEndDate"), @GridLayout.Row("showMostRecentOnly") })
    public static class PayGroupsPaygroupIdPeriods implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String paygroupId;

        @Option
        @Documentation("Period end date defined on the payroll period. This should not be used with the showMostRecentOnly query parameter. If both the periodEndDate and showMostRecentOnly query parameters are provided, the API will only consider the periodEndDate query parameter.")
        private String periodEndDate;

        @Option
        @Documentation("Used as to determine whether to show only the recent payroll periods (true) or all payroll periods (false). This should not be used with the periodEndDate query parameter. If both the showMostRecentOnly and periodEndDate query parameters are provided, the API will ignore the showMostRecentOnly query parameter.")
        private Boolean showMostRecentOnly = Boolean.TRUE;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "globalPayroll/v1/payGroups/" + this.paygroupId + "/periods";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.periodEndDate != null) {
                queryParam.put("periodEndDate", this.periodEndDate);
            }
            if (this.showMostRecentOnly != null) {
                queryParam.put("showMostRecentOnly", this.showMostRecentOnly);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("paygroupId"), @GridLayout.Row("periodId") })
    public static class PayGroupsPaygroupIdPeriodsPeriodId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of parent instance")
        private String paygroupId;

        @Option
        @Documentation("Id of child instance")
        private String periodId;

        @Override
        public String getServiceToCall() {
            return "globalPayroll/v1/payGroups/" + this.paygroupId + "/periods/" + this.periodId + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum queryView {
        periodSummary
    }

    public enum GlobalPayrollSwaggerServiceChoice {
        PayGroups,
        PayGroupsPaygroupId,
        PayGroupsPaygroupIdPeriods,
        PayGroupsPaygroupIdPeriodsPeriodId;
    }

    @Option
    @Documentation("selected service")
    private GlobalPayrollSwaggerServiceChoice service = GlobalPayrollSwaggerServiceChoice.PayGroups;

    private PayGroups payGroupsParameters = new PayGroups();

    @Option
    @ActiveIf(target = "service", value = "PayGroupsPaygroupId")
    @Documentation("parameters")
    private PayGroupsPaygroupId payGroupsPaygroupIdParameters = new PayGroupsPaygroupId();

    @Option
    @ActiveIf(target = "service", value = "PayGroupsPaygroupIdPeriods")
    @Documentation("parameters")
    private PayGroupsPaygroupIdPeriods payGroupsPaygroupIdPeriodsParameters = new PayGroupsPaygroupIdPeriods();

    @Option
    @ActiveIf(target = "service", value = "PayGroupsPaygroupIdPeriodsPeriodId")
    @Documentation("parameters")
    private PayGroupsPaygroupIdPeriodsPeriodId payGroupsPaygroupIdPeriodsPeriodIdParameters = new PayGroupsPaygroupIdPeriodsPeriodId();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == GlobalPayrollSwaggerServiceChoice.PayGroups) {
            return this.payGroupsParameters;
        }
        if (this.service == GlobalPayrollSwaggerServiceChoice.PayGroupsPaygroupId) {
            return this.payGroupsPaygroupIdParameters;
        }
        if (this.service == GlobalPayrollSwaggerServiceChoice.PayGroupsPaygroupIdPeriods) {
            return this.payGroupsPaygroupIdPeriodsParameters;
        }
        if (this.service == GlobalPayrollSwaggerServiceChoice.PayGroupsPaygroupIdPeriodsPeriodId) {
            return this.payGroupsPaygroupIdPeriodsPeriodIdParameters;
        }

        return null;
    }
}
