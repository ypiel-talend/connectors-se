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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("customersIdParameters"),
        @GridLayout.Row("customersIdactivitiesParameters"), @GridLayout.Row("customersIdactivitiesactivityIdParameters") })
@Documentation("Customer Activities")
public class CustomerActivitiesSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id") })
    public static class CustomersId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The unique Customer identifier.")
        private String id;

        @Override
        public String getServiceToCall() {
            return "common/v1/customers/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("fromDate"), @GridLayout.Row("toDate") })
    public static class CustomersIdactivities implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The unique Customer identifier.")
        private String id;

        @Option
        @Documentation("The earliest date for which to retrieve Activity, inclusive of the date.")
        private String fromDate;

        @Option
        @Documentation("The latest date for which to retrieve Activity, inclusive of the date.")
        private String toDate;

        @Override
        public String getServiceToCall() {
            return "common/v1/customers/" + this.id + "/activities";
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
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("activityId") })
    public static class CustomersIdactivitiesactivityId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The unique Customer identifier.")
        private String id;

        @Option
        @Documentation("The unique Activity identifier.")
        private String activityId;

        @Override
        public String getServiceToCall() {
            return "common/v1/customers/" + this.id + "/activities/" + this.activityId + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum CustomerActivitiesSwaggerServiceChoice {
        CustomersId,
        CustomersIdactivities,
        CustomersIdactivitiesactivityId;
    }

    @Option
    @Documentation("selected service")
    private CustomerActivitiesSwaggerServiceChoice service = CustomerActivitiesSwaggerServiceChoice.CustomersId;

    @Option
    @ActiveIf(target = "service", value = "CustomersId")
    @Documentation("parameters")
    private CustomersId customersIdParameters = new CustomersId();

    @Option
    @ActiveIf(target = "service", value = "CustomersIdactivities")
    @Documentation("parameters")
    private CustomersIdactivities customersIdactivitiesParameters = new CustomersIdactivities();

    @Option
    @ActiveIf(target = "service", value = "CustomersIdactivitiesactivityId")
    @Documentation("parameters")
    private CustomersIdactivitiesactivityId customersIdactivitiesactivityIdParameters = new CustomersIdactivitiesactivityId();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == CustomerActivitiesSwaggerServiceChoice.CustomersId) {
            return this.customersIdParameters;
        }
        if (this.service == CustomerActivitiesSwaggerServiceChoice.CustomersIdactivities) {
            return this.customersIdactivitiesParameters;
        }
        if (this.service == CustomerActivitiesSwaggerServiceChoice.CustomersIdactivitiesactivityId) {
            return this.customersIdactivitiesactivityIdParameters;
        }

        return null;
    }
}
