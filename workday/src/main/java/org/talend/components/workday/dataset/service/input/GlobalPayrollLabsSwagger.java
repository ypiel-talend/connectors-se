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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("batchesParameters"), @GridLayout.Row("batchesIDParameters"),
        @GridLayout.Row("batchesIDExternalInputsParameters"), @GridLayout.Row("batchesIDExternalInputsSubresourceIDParameters"),
        @GridLayout.Row("payComponentsParameters"), @GridLayout.Row("payComponentsIDParameters"),
        @GridLayout.Row("workersIDParameters"), @GridLayout.Row("workersIDExternalInputsParameters"),
        @GridLayout.Row("workersIDExternalInputsSubresourceIDParameters") })
@Documentation("Global Payroll (Labs)")
public class GlobalPayrollLabsSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 0L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("usage") })
    public static class Batches implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Workday ID or reference ID of the batch usage. The reference ID uses the <Application_Batch_Usage_ID>=<id> format; example: Batch_ID=PAYROLL_INTERFACE. See the available values listed.")
        private java.util.List<String> usage;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "globalPayroll/labs/batches";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.usage != null) {
                queryParam.put("usage", this.usage);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD") })
    public static class BatchesID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Workday ID or reference ID of the batch. The reference ID uses the <Batch_ID>=<id> format; example: Batch_ID=1234. The value of the Workday ID can be found in the list of batches from the response of an API request to /batches. The value of the reference ID is the same as the batch ID, which is displayed in the 'descriptor' field of the response of an API request to /batches.")
        private String iD;

        @Override
        public String getServiceToCall() {
            return "globalPayroll/labs/batches/" + this.iD + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("startDate"), @GridLayout.Row("endDate"), @GridLayout.Row("component"), @GridLayout.Row("iD") })
    public static class BatchesIDExternalInputs implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The start date of the payroll input using the yyyy-mm-dd format.")
        private String startDate;

        @Option
        @Documentation("The end date of the payroll input using the yyyy-mm-dd format.")
        private String endDate;

        @Option
        @Documentation("The Workday ID or reference ID of the pay component for the payroll input. The reference ID uses the <Deduction_Code>=<id> format for deductions and <Earning_Code>=<=id> for earnings; example: Deduction_Code=Deduction1, Earning_Code-Earning1. The value of the Workday ID can be found in the list of pay components from the response of an API request to /payComponents. The value of the reference ID is the same as the pay component code, which can be found in the View External Payroll Deductions or View External Payroll Earnings report.")
        private String component;

        @Option
        @Documentation("The Workday ID or reference ID of the batch. The reference ID uses the <Batch_ID>=<id> format; example: Batch_ID=1234. The value of the Workday ID can be found in the list of batches from the response of an API request to /batches. The value of the reference ID is the same as the batch ID, which is displayed in the 'descriptor' field of the response of an API request to /batches.")
        private String iD;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "globalPayroll/labs/batches/" + this.iD + "/externalInputs";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.startDate != null) {
                queryParam.put("startDate", this.startDate);
            }
            if (this.endDate != null) {
                queryParam.put("endDate", this.endDate);
            }
            if (this.component != null) {
                queryParam.put("component", this.component);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD"), @GridLayout.Row("subresourceID") })
    public static class BatchesIDExternalInputsSubresourceID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Workday ID or reference ID of the batch. The reference ID uses the <Batch_ID>=<id> format; example: Batch_ID=1234. The value of the Workday ID can be found in the list of batches from the response of an API request to /batches. The value of the reference ID is the same as the batch ID, which is displayed in the 'descriptor' field of the response of an API request to /batches.")
        private String iD;

        @Option
        @Documentation("ID of specified payroll input instance. This can be found in the list of payroll inputs from the response of an API request to /batches/{ID}/externalInputs.")
        private String subresourceID;

        @Override
        public String getServiceToCall() {
            return "globalPayroll/labs/batches/" + this.iD + "/externalInputs/" + this.subresourceID + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("usage") })
    public static class PayComponents implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The pay component usage type.")
        private queryUsage usage = queryUsage.deduction;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "globalPayroll/labs/payComponents";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.usage != null) {
                queryParam.put("usage", this.usage);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD") })
    public static class PayComponentsID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Workday ID or reference ID of the pay component. The reference ID uses the <Deduction_Code>=<id> format for deductions and <Earning_Code>=<=id> for earnings; example: Deduction_Code=Deduction1, Earning_Code=Earning1. The value of the Workday ID can be found in the list of pay components from the response of an API request to /payComponents. The value of the reference ID is the same as the pay component code, which can be found in the View External Payroll Deductions or View External Payroll Earnings report.")
        private String iD;

        @Override
        public String getServiceToCall() {
            return "globalPayroll/labs/payComponents/" + this.iD + "";
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
            return "globalPayroll/labs/workers";
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
    public static class WorkersID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Workday ID of the worker. This value can be found in the list of workers from the response of an API request to /workers.")
        private String iD;

        @Override
        public String getServiceToCall() {
            return "globalPayroll/labs/workers/" + this.iD + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("batchId"), @GridLayout.Row("component"), @GridLayout.Row("startDate"),
            @GridLayout.Row("endDate"), @GridLayout.Row("iD") })
    public static class WorkersIDExternalInputs implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Workday ID or reference ID of the batch for the payroll input. The reference ID uses the <Batch_ID>=<id> format; example: Batch_ID=1234. The value of the Workday ID can be found in the list of batches from the response of an API request to /batches. The value of the reference ID is the same as the batch ID, which is displayed in the 'descriptor' field of the response of an API request to /batches.")
        private java.util.List<String> batchId;

        @Option
        @Documentation("The Workday ID or reference ID of the Payroll Interface pay component for the payroll input. The reference ID uses the <Deduction_Code>=<id> format for deductions and <Earning_Code>=<=id> for earnings; example: Deduction_Code=Deduction1, Earning_Code-Earning1. The value of the Workday ID can be found in the list of pay components from the response of an API request to /payComponents. The value of the reference ID is the same as the pay component code, which can be found in the View External Payroll Deductions or View External Payroll Earnings report.")
        private String component;

        @Option
        @Documentation("The start date of the payroll input using the yyyy-mm-dd format.")
        private String startDate;

        @Option
        @Documentation("The end date of the payroll input using the yyyy-mm-dd format.")
        private String endDate;

        @Option
        @Documentation("The Workday ID of the worker. This value can be found in the list of workers from the response of an API request to /workers.")
        private String iD;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "globalPayroll/labs/workers/" + this.iD + "/externalInputs";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.batchId != null) {
                queryParam.put("batchId", this.batchId);
            }
            if (this.component != null) {
                queryParam.put("component", this.component);
            }
            if (this.startDate != null) {
                queryParam.put("startDate", this.startDate);
            }
            if (this.endDate != null) {
                queryParam.put("endDate", this.endDate);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD"), @GridLayout.Row("subresourceID") })
    public static class WorkersIDExternalInputsSubresourceID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Workday ID of the worker. This value can be found in the list of workers from the response of an API request to /workers.")
        private String iD;

        @Option
        @Documentation("The Workday ID of the payroll input. This can be found in the list of payroll inputs from the response of an API request to /workers/{ID}/externalInputs.")
        private String subresourceID;

        @Override
        public String getServiceToCall() {
            return "globalPayroll/labs/workers/" + this.iD + "/externalInputs/" + this.subresourceID + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum queryUsage {
        deduction,
        earning
    }

    public enum GlobalPayrollLabsSwaggerServiceChoice {
        Batches,
        BatchesID,
        BatchesIDExternalInputs,
        BatchesIDExternalInputsSubresourceID,
        PayComponents,
        PayComponentsID,
        Workers,
        WorkersID,
        WorkersIDExternalInputs,
        WorkersIDExternalInputsSubresourceID;
    }

    @Option
    @Documentation("selected service")
    private GlobalPayrollLabsSwaggerServiceChoice service = GlobalPayrollLabsSwaggerServiceChoice.Batches;

    @Option
    @ActiveIf(target = "service", value = "Batches")
    @Documentation("parameters")
    private Batches batchesParameters = new Batches();

    @Option
    @ActiveIf(target = "service", value = "BatchesID")
    @Documentation("parameters")
    private BatchesID batchesIDParameters = new BatchesID();

    @Option
    @ActiveIf(target = "service", value = "BatchesIDExternalInputs")
    @Documentation("parameters")
    private BatchesIDExternalInputs batchesIDExternalInputsParameters = new BatchesIDExternalInputs();

    @Option
    @ActiveIf(target = "service", value = "BatchesIDExternalInputsSubresourceID")
    @Documentation("parameters")
    private BatchesIDExternalInputsSubresourceID batchesIDExternalInputsSubresourceIDParameters = new BatchesIDExternalInputsSubresourceID();

    @Option
    @ActiveIf(target = "service", value = "PayComponents")
    @Documentation("parameters")
    private PayComponents payComponentsParameters = new PayComponents();

    @Option
    @ActiveIf(target = "service", value = "PayComponentsID")
    @Documentation("parameters")
    private PayComponentsID payComponentsIDParameters = new PayComponentsID();

    private Workers workersParameters = new Workers();

    @Option
    @ActiveIf(target = "service", value = "WorkersID")
    @Documentation("parameters")
    private WorkersID workersIDParameters = new WorkersID();

    @Option
    @ActiveIf(target = "service", value = "WorkersIDExternalInputs")
    @Documentation("parameters")
    private WorkersIDExternalInputs workersIDExternalInputsParameters = new WorkersIDExternalInputs();

    @Option
    @ActiveIf(target = "service", value = "WorkersIDExternalInputsSubresourceID")
    @Documentation("parameters")
    private WorkersIDExternalInputsSubresourceID workersIDExternalInputsSubresourceIDParameters = new WorkersIDExternalInputsSubresourceID();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == GlobalPayrollLabsSwaggerServiceChoice.Batches) {
            return this.batchesParameters;
        }
        if (this.service == GlobalPayrollLabsSwaggerServiceChoice.BatchesID) {
            return this.batchesIDParameters;
        }
        if (this.service == GlobalPayrollLabsSwaggerServiceChoice.BatchesIDExternalInputs) {
            return this.batchesIDExternalInputsParameters;
        }
        if (this.service == GlobalPayrollLabsSwaggerServiceChoice.BatchesIDExternalInputsSubresourceID) {
            return this.batchesIDExternalInputsSubresourceIDParameters;
        }
        if (this.service == GlobalPayrollLabsSwaggerServiceChoice.PayComponents) {
            return this.payComponentsParameters;
        }
        if (this.service == GlobalPayrollLabsSwaggerServiceChoice.PayComponentsID) {
            return this.payComponentsIDParameters;
        }
        if (this.service == GlobalPayrollLabsSwaggerServiceChoice.Workers) {
            return this.workersParameters;
        }
        if (this.service == GlobalPayrollLabsSwaggerServiceChoice.WorkersID) {
            return this.workersIDParameters;
        }
        if (this.service == GlobalPayrollLabsSwaggerServiceChoice.WorkersIDExternalInputs) {
            return this.workersIDExternalInputsParameters;
        }
        if (this.service == GlobalPayrollLabsSwaggerServiceChoice.WorkersIDExternalInputsSubresourceID) {
            return this.workersIDExternalInputsSubresourceIDParameters;
        }

        return null;
    }
}
