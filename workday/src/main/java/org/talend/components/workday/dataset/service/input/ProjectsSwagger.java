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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("adHocProjectTimeTransactionsParameters"),
        @GridLayout.Row("adHocProjectTimeTransactionsIDParameters") })
@Documentation("Projects")
public class ProjectsSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("projectOrProjectHierarchy") })
    public static class adHocProjectTimeTransactions implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Project or Project Hierarchy")
        private String projectOrProjectHierarchy;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "projects/v1/adHocProjectTimeTransactions";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.projectOrProjectHierarchy != null) {
                queryParam.put("projectOrProjectHierarchy", this.projectOrProjectHierarchy);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD") })
    public static class adHocProjectTimeTransactionsID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD;

        @Override
        public String getServiceToCall() {
            return "projects/v1/adHocProjectTimeTransactions/" + this.iD + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum ProjectsSwaggerServiceChoice {
        adHocProjectTimeTransactions,
        adHocProjectTimeTransactionsID;
    }

    @Option
    @Documentation("selected service")
    private ProjectsSwaggerServiceChoice service = ProjectsSwaggerServiceChoice.adHocProjectTimeTransactions;

    @Option
    @ActiveIf(target = "service", value = "adHocProjectTimeTransactions")
    @Documentation("parameters")
    private adHocProjectTimeTransactions adHocProjectTimeTransactionsParameters = new adHocProjectTimeTransactions();

    @Option
    @ActiveIf(target = "service", value = "adHocProjectTimeTransactionsID")
    @Documentation("parameters")
    private adHocProjectTimeTransactionsID adHocProjectTimeTransactionsIDParameters = new adHocProjectTimeTransactionsID();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == ProjectsSwaggerServiceChoice.adHocProjectTimeTransactions) {
            return this.adHocProjectTimeTransactionsParameters;
        }
        if (this.service == ProjectsSwaggerServiceChoice.adHocProjectTimeTransactionsID) {
            return this.adHocProjectTimeTransactionsIDParameters;
        }

        return null;
    }
}
