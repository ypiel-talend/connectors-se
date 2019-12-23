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
@GridLayout({ @GridLayout.Row("module"), @GridLayout.Row("functionServiceSwagger"), @GridLayout.Row("userInfoSwagger"),
        @GridLayout.Row("timeTrackingSwagger"), @GridLayout.Row("customerAccountsSwagger"), @GridLayout.Row("recruitingSwagger"),
        @GridLayout.Row("connectSwagger"), @GridLayout.Row("grantsV2alphaSwagger"),
        @GridLayout.Row("humanResourceManagementSwagger"), @GridLayout.Row("studentSwagger"),
        @GridLayout.Row("referenceDataSwagger"), @GridLayout.Row("talentManagementSwagger"),
        @GridLayout.Row("commonServicesSwagger"), @GridLayout.Row("performanceManagementV1Swagger"),
        @GridLayout.Row("globalPayrollLabsSwagger"), @GridLayout.Row("outboundWebhooksSwagger"),
        @GridLayout.Row("projectsSwagger"), @GridLayout.Row("customBusinessProcessSwagger"),
        @GridLayout.Row("customObjectDefinitionsSwagger"), @GridLayout.Row("customTasksSwagger"),
        @GridLayout.Row("performanceManagementV2Swagger"), @GridLayout.Row("procurementSwagger"),
        @GridLayout.Row("customBusinessProcessConfigSwagger"), @GridLayout.Row("performanceEnablementV3Swagger"),
        @GridLayout.Row("organizationsSwagger"), @GridLayout.Row("customObjectDataV2SingleInstanceSwagger"),
        @GridLayout.Row("businessProcessSwagger"), @GridLayout.Row("customObjectDataV2MultiInstanceSwagger"),
        @GridLayout.Row("customerActivitiesSwagger"), @GridLayout.Row("payrollV1alphaSwagger"),
        @GridLayout.Row("globalPayrollSwagger"), @GridLayout.Row("expensesSwagger") })
@Documentation("module")
public class ModuleChoice implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    public enum Modules {
        FunctionServiceSwagger,
        UserInfoSwagger,
        TimeTrackingSwagger,
        CustomerAccountsSwagger,
        RecruitingSwagger,
        ConnectSwagger,
        GrantsV2alphaSwagger,
        HumanResourceManagementSwagger,
        StudentSwagger,
        ReferenceDataSwagger,
        TalentManagementSwagger,
        CommonServicesSwagger,
        PerformanceManagementV1Swagger,
        GlobalPayrollLabsSwagger,
        OutboundWebhooksSwagger,
        ProjectsSwagger,
        CustomBusinessProcessSwagger,
        CustomObjectDefinitionsSwagger,
        CustomTasksSwagger,
        PerformanceManagementV2Swagger,
        ProcurementSwagger,
        CustomBusinessProcessConfigSwagger,
        PerformanceEnablementV3Swagger,
        OrganizationsSwagger,
        CustomObjectDataV2SingleInstanceSwagger,
        BusinessProcessSwagger,
        CustomObjectDataV2MultiInstanceSwagger,
        CustomerActivitiesSwagger,
        PayrollV1alphaSwagger,
        GlobalPayrollSwagger,
        ExpensesSwagger
    }

    @Option
    @Documentation("selected module")
    private Modules module;

    @Option
    @ActiveIf(target = "module", value = "FunctionServiceSwagger")
    @Documentation("module FunctionServiceSwagger")
    private FunctionServiceSwagger functionServiceSwagger = new FunctionServiceSwagger();

    @Option
    @ActiveIf(target = "module", value = "UserInfoSwagger")
    @Documentation("module UserInfoSwagger")
    private UserInfoSwagger userInfoSwagger = new UserInfoSwagger();

    @Option
    @ActiveIf(target = "module", value = "TimeTrackingSwagger")
    @Documentation("module TimeTrackingSwagger")
    private TimeTrackingSwagger timeTrackingSwagger = new TimeTrackingSwagger();

    @Option
    @ActiveIf(target = "module", value = "CustomerAccountsSwagger")
    @Documentation("module CustomerAccountsSwagger")
    private CustomerAccountsSwagger customerAccountsSwagger = new CustomerAccountsSwagger();

    @Option
    @ActiveIf(target = "module", value = "RecruitingSwagger")
    @Documentation("module RecruitingSwagger")
    private RecruitingSwagger recruitingSwagger = new RecruitingSwagger();

    @Option
    @ActiveIf(target = "module", value = "ConnectSwagger")
    @Documentation("module ConnectSwagger")
    private ConnectSwagger connectSwagger = new ConnectSwagger();

    @Option
    @ActiveIf(target = "module", value = "GrantsV2alphaSwagger")
    @Documentation("module GrantsV2alphaSwagger")
    private GrantsV2alphaSwagger grantsV2alphaSwagger = new GrantsV2alphaSwagger();

    @Option
    @ActiveIf(target = "module", value = "HumanResourceManagementSwagger")
    @Documentation("module HumanResourceManagementSwagger")
    private HumanResourceManagementSwagger humanResourceManagementSwagger = new HumanResourceManagementSwagger();

    @Option
    @ActiveIf(target = "module", value = "StudentSwagger")
    @Documentation("module StudentSwagger")
    private StudentSwagger studentSwagger = new StudentSwagger();

    @Option
    @ActiveIf(target = "module", value = "ReferenceDataSwagger")
    @Documentation("module ReferenceDataSwagger")
    private ReferenceDataSwagger referenceDataSwagger = new ReferenceDataSwagger();

    @Option
    @ActiveIf(target = "module", value = "TalentManagementSwagger")
    @Documentation("module TalentManagementSwagger")
    private TalentManagementSwagger talentManagementSwagger = new TalentManagementSwagger();

    @Option
    @ActiveIf(target = "module", value = "CommonServicesSwagger")
    @Documentation("module CommonServicesSwagger")
    private CommonServicesSwagger commonServicesSwagger = new CommonServicesSwagger();

    @Option
    @ActiveIf(target = "module", value = "PerformanceManagementV1Swagger")
    @Documentation("module PerformanceManagementV1Swagger")
    private PerformanceManagementV1Swagger performanceManagementV1Swagger = new PerformanceManagementV1Swagger();

    @Option
    @ActiveIf(target = "module", value = "GlobalPayrollLabsSwagger")
    @Documentation("module GlobalPayrollLabsSwagger")
    private GlobalPayrollLabsSwagger globalPayrollLabsSwagger = new GlobalPayrollLabsSwagger();

    @Option
    @ActiveIf(target = "module", value = "OutboundWebhooksSwagger")
    @Documentation("module OutboundWebhooksSwagger")
    private OutboundWebhooksSwagger outboundWebhooksSwagger = new OutboundWebhooksSwagger();

    @Option
    @ActiveIf(target = "module", value = "ProjectsSwagger")
    @Documentation("module ProjectsSwagger")
    private ProjectsSwagger projectsSwagger = new ProjectsSwagger();

    @Option
    @ActiveIf(target = "module", value = "CustomBusinessProcessSwagger")
    @Documentation("module CustomBusinessProcessSwagger")
    private CustomBusinessProcessSwagger customBusinessProcessSwagger = new CustomBusinessProcessSwagger();

    @Option
    @ActiveIf(target = "module", value = "CustomObjectDefinitionsSwagger")
    @Documentation("module CustomObjectDefinitionsSwagger")
    private CustomObjectDefinitionsSwagger customObjectDefinitionsSwagger = new CustomObjectDefinitionsSwagger();

    @Option
    @ActiveIf(target = "module", value = "CustomTasksSwagger")
    @Documentation("module CustomTasksSwagger")
    private CustomTasksSwagger customTasksSwagger = new CustomTasksSwagger();

    @Option
    @ActiveIf(target = "module", value = "PerformanceManagementV2Swagger")
    @Documentation("module PerformanceManagementV2Swagger")
    private PerformanceManagementV2Swagger performanceManagementV2Swagger = new PerformanceManagementV2Swagger();

    @Option
    @ActiveIf(target = "module", value = "ProcurementSwagger")
    @Documentation("module ProcurementSwagger")
    private ProcurementSwagger procurementSwagger = new ProcurementSwagger();

    @Option
    @ActiveIf(target = "module", value = "CustomBusinessProcessConfigSwagger")
    @Documentation("module CustomBusinessProcessConfigSwagger")
    private CustomBusinessProcessConfigSwagger customBusinessProcessConfigSwagger = new CustomBusinessProcessConfigSwagger();

    @Option
    @ActiveIf(target = "module", value = "PerformanceEnablementV3Swagger")
    @Documentation("module PerformanceEnablementV3Swagger")
    private PerformanceEnablementV3Swagger performanceEnablementV3Swagger = new PerformanceEnablementV3Swagger();

    @Option
    @ActiveIf(target = "module", value = "OrganizationsSwagger")
    @Documentation("module OrganizationsSwagger")
    private OrganizationsSwagger organizationsSwagger = new OrganizationsSwagger();

    @Option
    @ActiveIf(target = "module", value = "CustomObjectDataV2SingleInstanceSwagger")
    @Documentation("module CustomObjectDataV2SingleInstanceSwagger")
    private CustomObjectDataV2SingleInstanceSwagger customObjectDataV2SingleInstanceSwagger = new CustomObjectDataV2SingleInstanceSwagger();

    @Option
    @ActiveIf(target = "module", value = "BusinessProcessSwagger")
    @Documentation("module BusinessProcessSwagger")
    private BusinessProcessSwagger businessProcessSwagger = new BusinessProcessSwagger();

    @Option
    @ActiveIf(target = "module", value = "CustomObjectDataV2MultiInstanceSwagger")
    @Documentation("module CustomObjectDataV2MultiInstanceSwagger")
    private CustomObjectDataV2MultiInstanceSwagger customObjectDataV2MultiInstanceSwagger = new CustomObjectDataV2MultiInstanceSwagger();

    @Option
    @ActiveIf(target = "module", value = "CustomerActivitiesSwagger")
    @Documentation("module CustomerActivitiesSwagger")
    private CustomerActivitiesSwagger customerActivitiesSwagger = new CustomerActivitiesSwagger();

    @Option
    @ActiveIf(target = "module", value = "PayrollV1alphaSwagger")
    @Documentation("module PayrollV1alphaSwagger")
    private PayrollV1alphaSwagger payrollV1alphaSwagger = new PayrollV1alphaSwagger();

    @Option
    @ActiveIf(target = "module", value = "GlobalPayrollSwagger")
    @Documentation("module GlobalPayrollSwagger")
    private GlobalPayrollSwagger globalPayrollSwagger = new GlobalPayrollSwagger();

    @Option
    @ActiveIf(target = "module", value = "ExpensesSwagger")
    @Documentation("module ExpensesSwagger")
    private ExpensesSwagger expensesSwagger = new ExpensesSwagger();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.module == Modules.FunctionServiceSwagger) {
            if (this.functionServiceSwagger != null) {
                return this.functionServiceSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.UserInfoSwagger) {
            if (this.userInfoSwagger != null) {
                return this.userInfoSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.TimeTrackingSwagger) {
            if (this.timeTrackingSwagger != null) {
                return this.timeTrackingSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.CustomerAccountsSwagger) {
            if (this.customerAccountsSwagger != null) {
                return this.customerAccountsSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.RecruitingSwagger) {
            if (this.recruitingSwagger != null) {
                return this.recruitingSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.ConnectSwagger) {
            if (this.connectSwagger != null) {
                return this.connectSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.GrantsV2alphaSwagger) {
            if (this.grantsV2alphaSwagger != null) {
                return this.grantsV2alphaSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.HumanResourceManagementSwagger) {
            if (this.humanResourceManagementSwagger != null) {
                return this.humanResourceManagementSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.StudentSwagger) {
            if (this.studentSwagger != null) {
                return this.studentSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.ReferenceDataSwagger) {
            if (this.referenceDataSwagger != null) {
                return this.referenceDataSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.TalentManagementSwagger) {
            if (this.talentManagementSwagger != null) {
                return this.talentManagementSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.CommonServicesSwagger) {
            if (this.commonServicesSwagger != null) {
                return this.commonServicesSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.PerformanceManagementV1Swagger) {
            if (this.performanceManagementV1Swagger != null) {
                return this.performanceManagementV1Swagger.getQueryHelper();
            }
        }
        if (this.module == Modules.GlobalPayrollLabsSwagger) {
            if (this.globalPayrollLabsSwagger != null) {
                return this.globalPayrollLabsSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.OutboundWebhooksSwagger) {
            if (this.outboundWebhooksSwagger != null) {
                return this.outboundWebhooksSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.ProjectsSwagger) {
            if (this.projectsSwagger != null) {
                return this.projectsSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.CustomBusinessProcessSwagger) {
            if (this.customBusinessProcessSwagger != null) {
                return this.customBusinessProcessSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.CustomObjectDefinitionsSwagger) {
            if (this.customObjectDefinitionsSwagger != null) {
                return this.customObjectDefinitionsSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.CustomTasksSwagger) {
            if (this.customTasksSwagger != null) {
                return this.customTasksSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.PerformanceManagementV2Swagger) {
            if (this.performanceManagementV2Swagger != null) {
                return this.performanceManagementV2Swagger.getQueryHelper();
            }
        }
        if (this.module == Modules.ProcurementSwagger) {
            if (this.procurementSwagger != null) {
                return this.procurementSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.CustomBusinessProcessConfigSwagger) {
            if (this.customBusinessProcessConfigSwagger != null) {
                return this.customBusinessProcessConfigSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.PerformanceEnablementV3Swagger) {
            if (this.performanceEnablementV3Swagger != null) {
                return this.performanceEnablementV3Swagger.getQueryHelper();
            }
        }
        if (this.module == Modules.OrganizationsSwagger) {
            if (this.organizationsSwagger != null) {
                return this.organizationsSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.CustomObjectDataV2SingleInstanceSwagger) {
            if (this.customObjectDataV2SingleInstanceSwagger != null) {
                return this.customObjectDataV2SingleInstanceSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.BusinessProcessSwagger) {
            if (this.businessProcessSwagger != null) {
                return this.businessProcessSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.CustomObjectDataV2MultiInstanceSwagger) {
            if (this.customObjectDataV2MultiInstanceSwagger != null) {
                return this.customObjectDataV2MultiInstanceSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.CustomerActivitiesSwagger) {
            if (this.customerActivitiesSwagger != null) {
                return this.customerActivitiesSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.PayrollV1alphaSwagger) {
            if (this.payrollV1alphaSwagger != null) {
                return this.payrollV1alphaSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.GlobalPayrollSwagger) {
            if (this.globalPayrollSwagger != null) {
                return this.globalPayrollSwagger.getQueryHelper();
            }
        }
        if (this.module == Modules.ExpensesSwagger) {
            if (this.expensesSwagger != null) {
                return this.expensesSwagger.getQueryHelper();
            }
        }
        return null;
    }
}
