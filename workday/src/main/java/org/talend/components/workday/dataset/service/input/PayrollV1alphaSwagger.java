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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("payrollInputsParameters"),
        @GridLayout.Row("payrollInputsIDParameters") })
@Documentation("Payroll (v1Alpha)")
public class PayrollV1alphaSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("payComponent"), @GridLayout.Row("worker"), @GridLayout.Row("startDate"),
            @GridLayout.Row("endDate") })
    public static class PayrollInputs implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The pay component for this payroll input.")
        private java.util.List<String> payComponent;

        @Option
        @Documentation("~Worker~ that this input is for.")
        private java.util.List<String> worker;

        @Option
        @Documentation("The start date before which this input does not apply.")
        private String startDate;

        @Option
        @Documentation("The end date after which this input does not apply.")
        private String endDate;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "payroll/v1Alpha/payrollInputs";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.payComponent != null) {
                queryParam.put("payComponent", this.payComponent);
            }
            if (this.worker != null) {
                queryParam.put("worker", this.worker);
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
    @GridLayout({ @GridLayout.Row("iD") })
    public static class PayrollInputsID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Workday ID or reference ID of the Payroll Input. The reference ID uses the <br> <identifier-type>={id} format; example:Payroll_Input_ID=PAYROLL_INPUT-4-77. The ID can be obtained from the GET /payrollInputs operation.")
        private String iD;

        @Override
        public String getServiceToCall() {
            return "payroll/v1Alpha/payrollInputs/" + this.iD + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum PayrollV1alphaSwaggerServiceChoice {
        PayrollInputs,
        PayrollInputsID;
    }

    @Option
    @Documentation("selected service")
    private PayrollV1alphaSwaggerServiceChoice service = PayrollV1alphaSwaggerServiceChoice.PayrollInputs;

    @Option
    @ActiveIf(target = "service", value = "PayrollInputs")
    @Documentation("parameters")
    private PayrollInputs payrollInputsParameters = new PayrollInputs();

    @Option
    @ActiveIf(target = "service", value = "PayrollInputsID")
    @Documentation("parameters")
    private PayrollInputsID payrollInputsIDParameters = new PayrollInputsID();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == PayrollV1alphaSwaggerServiceChoice.PayrollInputs) {
            return this.payrollInputsParameters;
        }
        if (this.service == PayrollV1alphaSwaggerServiceChoice.PayrollInputsID) {
            return this.payrollInputsIDParameters;
        }

        return null;
    }
}
