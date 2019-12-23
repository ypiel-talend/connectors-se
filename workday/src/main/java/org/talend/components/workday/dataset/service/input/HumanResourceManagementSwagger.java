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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("jobChangeReasonsID_PATH_PARAMETERParameters"),
        @GridLayout.Row("supervisoryOrganizationsID_PATH_PARAMETERParameters"),
        @GridLayout.Row("supervisoryOrganizationsID_PATH_PARAMETERWorkersParameters"),
        @GridLayout.Row("timeOffRequestID_PATH_PARAMETERParameters"), @GridLayout.Row("workersID_PATH_PARAMETERParameters"),
        @GridLayout.Row("workersID_PATH_PARAMETERBusinessTitleChangesParameters"),
        @GridLayout.Row("workersID_PATH_PARAMETERDirectReportsParameters"),
        @GridLayout.Row("workersID_PATH_PARAMETERHistoryParameters"),
        @GridLayout.Row("workersID_PATH_PARAMETERInboxTasksParameters"),
        @GridLayout.Row("workersID_PATH_PARAMETEROrganizationsParameters"),
        @GridLayout.Row("workersID_PATH_PARAMETERPaySlipsParameters"),
        @GridLayout.Row("workersID_PATH_PARAMETERSupervisoryOrganizationsManagedParameters"),
        @GridLayout.Row("workersID_PATH_PARAMETERTimeOffEntriesParameters"),
        @GridLayout.Row("workersID_PATH_PARAMETERTimeOffPlansParameters") })
@Documentation("Human Resource Management")
public class HumanResourceManagementSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({})
    public static class JobChangeReasons implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "common/v1/jobChangeReasons";
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
    public static class JobChangeReasonsID_PATH_PARAMETER implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD_PATH_PARAMETER;

        @Override
        public String getServiceToCall() {
            return "common/v1/jobChangeReasons/" + this.iD_PATH_PARAMETER + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({})
    public static class SupervisoryOrganizations implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "common/v1/supervisoryOrganizations";
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
    public static class SupervisoryOrganizationsID_PATH_PARAMETER implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD_PATH_PARAMETER;

        @Override
        public String getServiceToCall() {
            return "common/v1/supervisoryOrganizations/" + this.iD_PATH_PARAMETER + "";
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
    public static class SupervisoryOrganizationsID_PATH_PARAMETERWorkers implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD_PATH_PARAMETER;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "common/v1/supervisoryOrganizations/" + this.iD_PATH_PARAMETER + "/workers";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({})
    public static class TimeOffRequest implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "common/v1/timeOffRequest";
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
    public static class TimeOffRequestID_PATH_PARAMETER implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD_PATH_PARAMETER;

        @Override
        public String getServiceToCall() {
            return "common/v1/timeOffRequest/" + this.iD_PATH_PARAMETER + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({})
    public static class Workers implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "common/v1/workers";
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
    public static class WorkersID_PATH_PARAMETER implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD_PATH_PARAMETER;

        @Override
        public String getServiceToCall() {
            return "common/v1/workers/" + this.iD_PATH_PARAMETER + "";
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
    public static class WorkersID_PATH_PARAMETERBusinessTitleChanges implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD_PATH_PARAMETER;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "common/v1/workers/" + this.iD_PATH_PARAMETER + "/businessTitleChanges";
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
    public static class WorkersID_PATH_PARAMETERDirectReports implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD_PATH_PARAMETER;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "common/v1/workers/" + this.iD_PATH_PARAMETER + "/directReports";
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
    public static class WorkersID_PATH_PARAMETERHistory implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD_PATH_PARAMETER;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "common/v1/workers/" + this.iD_PATH_PARAMETER + "/history";
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
    public static class WorkersID_PATH_PARAMETERInboxTasks implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD_PATH_PARAMETER;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "common/v1/workers/" + this.iD_PATH_PARAMETER + "/inboxTasks";
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
    public static class WorkersID_PATH_PARAMETEROrganizations implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD_PATH_PARAMETER;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "common/v1/workers/" + this.iD_PATH_PARAMETER + "/organizations";
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
    public static class WorkersID_PATH_PARAMETERPaySlips implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD_PATH_PARAMETER;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "common/v1/workers/" + this.iD_PATH_PARAMETER + "/paySlips";
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
    public static class WorkersID_PATH_PARAMETERSupervisoryOrganizationsManaged implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD_PATH_PARAMETER;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "common/v1/workers/" + this.iD_PATH_PARAMETER + "/supervisoryOrganizationsManaged";
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
    public static class WorkersID_PATH_PARAMETERTimeOffEntries implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD_PATH_PARAMETER;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "common/v1/workers/" + this.iD_PATH_PARAMETER + "/timeOffEntries";
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
    public static class WorkersID_PATH_PARAMETERTimeOffPlans implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD_PATH_PARAMETER;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "common/v1/workers/" + this.iD_PATH_PARAMETER + "/timeOffPlans";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum queryView {
        inboxTaskApproval,
        inboxTaskSummary,
        eventRecordAssigneeView
    }

    public enum HumanResourceManagementSwaggerServiceChoice {
        JobChangeReasons,
        JobChangeReasonsID_PATH_PARAMETER,
        SupervisoryOrganizations,
        SupervisoryOrganizationsID_PATH_PARAMETER,
        SupervisoryOrganizationsID_PATH_PARAMETERWorkers,
        TimeOffRequest,
        TimeOffRequestID_PATH_PARAMETER,
        Workers,
        WorkersID_PATH_PARAMETER,
        WorkersID_PATH_PARAMETERBusinessTitleChanges,
        WorkersID_PATH_PARAMETERDirectReports,
        WorkersID_PATH_PARAMETERHistory,
        WorkersID_PATH_PARAMETERInboxTasks,
        WorkersID_PATH_PARAMETEROrganizations,
        WorkersID_PATH_PARAMETERPaySlips,
        WorkersID_PATH_PARAMETERSupervisoryOrganizationsManaged,
        WorkersID_PATH_PARAMETERTimeOffEntries,
        WorkersID_PATH_PARAMETERTimeOffPlans;
    }

    @Option
    @Documentation("selected service")
    private HumanResourceManagementSwaggerServiceChoice service = HumanResourceManagementSwaggerServiceChoice.JobChangeReasons;

    private JobChangeReasons jobChangeReasonsParameters = new JobChangeReasons();

    @Option
    @ActiveIf(target = "service", value = "JobChangeReasonsID_PATH_PARAMETER")
    @Documentation("parameters")
    private JobChangeReasonsID_PATH_PARAMETER jobChangeReasonsID_PATH_PARAMETERParameters = new JobChangeReasonsID_PATH_PARAMETER();

    private SupervisoryOrganizations supervisoryOrganizationsParameters = new SupervisoryOrganizations();

    @Option
    @ActiveIf(target = "service", value = "SupervisoryOrganizationsID_PATH_PARAMETER")
    @Documentation("parameters")
    private SupervisoryOrganizationsID_PATH_PARAMETER supervisoryOrganizationsID_PATH_PARAMETERParameters = new SupervisoryOrganizationsID_PATH_PARAMETER();

    @Option
    @ActiveIf(target = "service", value = "SupervisoryOrganizationsID_PATH_PARAMETERWorkers")
    @Documentation("parameters")
    private SupervisoryOrganizationsID_PATH_PARAMETERWorkers supervisoryOrganizationsID_PATH_PARAMETERWorkersParameters = new SupervisoryOrganizationsID_PATH_PARAMETERWorkers();

    private TimeOffRequest timeOffRequestParameters = new TimeOffRequest();

    @Option
    @ActiveIf(target = "service", value = "TimeOffRequestID_PATH_PARAMETER")
    @Documentation("parameters")
    private TimeOffRequestID_PATH_PARAMETER timeOffRequestID_PATH_PARAMETERParameters = new TimeOffRequestID_PATH_PARAMETER();

    private Workers workersParameters = new Workers();

    @Option
    @ActiveIf(target = "service", value = "WorkersID_PATH_PARAMETER")
    @Documentation("parameters")
    private WorkersID_PATH_PARAMETER workersID_PATH_PARAMETERParameters = new WorkersID_PATH_PARAMETER();

    @Option
    @ActiveIf(target = "service", value = "WorkersID_PATH_PARAMETERBusinessTitleChanges")
    @Documentation("parameters")
    private WorkersID_PATH_PARAMETERBusinessTitleChanges workersID_PATH_PARAMETERBusinessTitleChangesParameters = new WorkersID_PATH_PARAMETERBusinessTitleChanges();

    @Option
    @ActiveIf(target = "service", value = "WorkersID_PATH_PARAMETERDirectReports")
    @Documentation("parameters")
    private WorkersID_PATH_PARAMETERDirectReports workersID_PATH_PARAMETERDirectReportsParameters = new WorkersID_PATH_PARAMETERDirectReports();

    @Option
    @ActiveIf(target = "service", value = "WorkersID_PATH_PARAMETERHistory")
    @Documentation("parameters")
    private WorkersID_PATH_PARAMETERHistory workersID_PATH_PARAMETERHistoryParameters = new WorkersID_PATH_PARAMETERHistory();

    @Option
    @ActiveIf(target = "service", value = "WorkersID_PATH_PARAMETERInboxTasks")
    @Documentation("parameters")
    private WorkersID_PATH_PARAMETERInboxTasks workersID_PATH_PARAMETERInboxTasksParameters = new WorkersID_PATH_PARAMETERInboxTasks();

    @Option
    @ActiveIf(target = "service", value = "WorkersID_PATH_PARAMETEROrganizations")
    @Documentation("parameters")
    private WorkersID_PATH_PARAMETEROrganizations workersID_PATH_PARAMETEROrganizationsParameters = new WorkersID_PATH_PARAMETEROrganizations();

    @Option
    @ActiveIf(target = "service", value = "WorkersID_PATH_PARAMETERPaySlips")
    @Documentation("parameters")
    private WorkersID_PATH_PARAMETERPaySlips workersID_PATH_PARAMETERPaySlipsParameters = new WorkersID_PATH_PARAMETERPaySlips();

    @Option
    @ActiveIf(target = "service", value = "WorkersID_PATH_PARAMETERSupervisoryOrganizationsManaged")
    @Documentation("parameters")
    private WorkersID_PATH_PARAMETERSupervisoryOrganizationsManaged workersID_PATH_PARAMETERSupervisoryOrganizationsManagedParameters = new WorkersID_PATH_PARAMETERSupervisoryOrganizationsManaged();

    @Option
    @ActiveIf(target = "service", value = "WorkersID_PATH_PARAMETERTimeOffEntries")
    @Documentation("parameters")
    private WorkersID_PATH_PARAMETERTimeOffEntries workersID_PATH_PARAMETERTimeOffEntriesParameters = new WorkersID_PATH_PARAMETERTimeOffEntries();

    @Option
    @ActiveIf(target = "service", value = "WorkersID_PATH_PARAMETERTimeOffPlans")
    @Documentation("parameters")
    private WorkersID_PATH_PARAMETERTimeOffPlans workersID_PATH_PARAMETERTimeOffPlansParameters = new WorkersID_PATH_PARAMETERTimeOffPlans();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == HumanResourceManagementSwaggerServiceChoice.JobChangeReasons) {
            return this.jobChangeReasonsParameters;
        }
        if (this.service == HumanResourceManagementSwaggerServiceChoice.JobChangeReasonsID_PATH_PARAMETER) {
            return this.jobChangeReasonsID_PATH_PARAMETERParameters;
        }
        if (this.service == HumanResourceManagementSwaggerServiceChoice.SupervisoryOrganizations) {
            return this.supervisoryOrganizationsParameters;
        }
        if (this.service == HumanResourceManagementSwaggerServiceChoice.SupervisoryOrganizationsID_PATH_PARAMETER) {
            return this.supervisoryOrganizationsID_PATH_PARAMETERParameters;
        }
        if (this.service == HumanResourceManagementSwaggerServiceChoice.SupervisoryOrganizationsID_PATH_PARAMETERWorkers) {
            return this.supervisoryOrganizationsID_PATH_PARAMETERWorkersParameters;
        }
        if (this.service == HumanResourceManagementSwaggerServiceChoice.TimeOffRequest) {
            return this.timeOffRequestParameters;
        }
        if (this.service == HumanResourceManagementSwaggerServiceChoice.TimeOffRequestID_PATH_PARAMETER) {
            return this.timeOffRequestID_PATH_PARAMETERParameters;
        }
        if (this.service == HumanResourceManagementSwaggerServiceChoice.Workers) {
            return this.workersParameters;
        }
        if (this.service == HumanResourceManagementSwaggerServiceChoice.WorkersID_PATH_PARAMETER) {
            return this.workersID_PATH_PARAMETERParameters;
        }
        if (this.service == HumanResourceManagementSwaggerServiceChoice.WorkersID_PATH_PARAMETERBusinessTitleChanges) {
            return this.workersID_PATH_PARAMETERBusinessTitleChangesParameters;
        }
        if (this.service == HumanResourceManagementSwaggerServiceChoice.WorkersID_PATH_PARAMETERDirectReports) {
            return this.workersID_PATH_PARAMETERDirectReportsParameters;
        }
        if (this.service == HumanResourceManagementSwaggerServiceChoice.WorkersID_PATH_PARAMETERHistory) {
            return this.workersID_PATH_PARAMETERHistoryParameters;
        }
        if (this.service == HumanResourceManagementSwaggerServiceChoice.WorkersID_PATH_PARAMETERInboxTasks) {
            return this.workersID_PATH_PARAMETERInboxTasksParameters;
        }
        if (this.service == HumanResourceManagementSwaggerServiceChoice.WorkersID_PATH_PARAMETEROrganizations) {
            return this.workersID_PATH_PARAMETEROrganizationsParameters;
        }
        if (this.service == HumanResourceManagementSwaggerServiceChoice.WorkersID_PATH_PARAMETERPaySlips) {
            return this.workersID_PATH_PARAMETERPaySlipsParameters;
        }
        if (this.service == HumanResourceManagementSwaggerServiceChoice.WorkersID_PATH_PARAMETERSupervisoryOrganizationsManaged) {
            return this.workersID_PATH_PARAMETERSupervisoryOrganizationsManagedParameters;
        }
        if (this.service == HumanResourceManagementSwaggerServiceChoice.WorkersID_PATH_PARAMETERTimeOffEntries) {
            return this.workersID_PATH_PARAMETERTimeOffEntriesParameters;
        }
        if (this.service == HumanResourceManagementSwaggerServiceChoice.WorkersID_PATH_PARAMETERTimeOffPlans) {
            return this.workersID_PATH_PARAMETERTimeOffPlansParameters;
        }

        return null;
    }
}
