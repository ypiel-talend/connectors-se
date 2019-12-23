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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("organizationTypesID_PATH_PARAMETERParameters"),
        @GridLayout.Row("organizationsParameters"), @GridLayout.Row("organizationsID_PATH_PARAMETERParameters") })
@Documentation("Organizations")
public class OrganizationsSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 0L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({})
    public static class OrganizationTypes implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "common/v1/organizationTypes";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD_PATH_PARAMETER") })
    public static class OrganizationTypesID_PATH_PARAMETER implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD_PATH_PARAMETER;

        @Override
        public String getServiceToCall() {
            return "common/v1/organizationTypes/" + this.iD_PATH_PARAMETER + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("organizationType") })
    public static class Organizations implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Reference to an Organization Type. Example: Organization Type WID. Requests must provide an Organization Type.")
        private String organizationType;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "common/v1/organizations";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.organizationType != null) {
                queryParam.put("organizationType", this.organizationType);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD_PATH_PARAMETER") })
    public static class OrganizationsID_PATH_PARAMETER implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD_PATH_PARAMETER;

        @Override
        public String getServiceToCall() {
            return "common/v1/organizations/" + this.iD_PATH_PARAMETER + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum queryView {
        organizationTypeSummary,
        customObjectTypeTG
    }

    public enum OrganizationsSwaggerServiceChoice {
        OrganizationTypes,
        OrganizationTypesID_PATH_PARAMETER,
        Organizations,
        OrganizationsID_PATH_PARAMETER;
    }

    @Option
    @Documentation("selected service")
    private OrganizationsSwaggerServiceChoice service = OrganizationsSwaggerServiceChoice.OrganizationTypes;

    private OrganizationTypes organizationTypesParameters = new OrganizationTypes();

    @Option
    @ActiveIf(target = "service", value = "OrganizationTypesID_PATH_PARAMETER")
    @Documentation("parameters")
    private OrganizationTypesID_PATH_PARAMETER organizationTypesID_PATH_PARAMETERParameters = new OrganizationTypesID_PATH_PARAMETER();

    @Option
    @ActiveIf(target = "service", value = "Organizations")
    @Documentation("parameters")
    private Organizations organizationsParameters = new Organizations();

    @Option
    @ActiveIf(target = "service", value = "OrganizationsID_PATH_PARAMETER")
    @Documentation("parameters")
    private OrganizationsID_PATH_PARAMETER organizationsID_PATH_PARAMETERParameters = new OrganizationsID_PATH_PARAMETER();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == OrganizationsSwaggerServiceChoice.OrganizationTypes) {
            return this.organizationTypesParameters;
        }
        if (this.service == OrganizationsSwaggerServiceChoice.OrganizationTypesID_PATH_PARAMETER) {
            return this.organizationTypesID_PATH_PARAMETERParameters;
        }
        if (this.service == OrganizationsSwaggerServiceChoice.Organizations) {
            return this.organizationsParameters;
        }
        if (this.service == OrganizationsSwaggerServiceChoice.OrganizationsID_PATH_PARAMETER) {
            return this.organizationsID_PATH_PARAMETERParameters;
        }

        return null;
    }
}
