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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("execIdParameters"), @GridLayout.Row("functionsIdParameters") })
@Documentation("Function Service")
public class FunctionServiceSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("wcp_logs") })
    public static class ExecId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id or Reference_Id=[referenceId] of specified function.")
        private String id;

        @Option
        @Documentation("When true, the response will be wrapped in a json object which includes logs. See models \'logExecResponse\' and \'logJsonExecResponse\'. This action is Secured by the \'Function Service: Log Access\' Domain.")
        private Boolean wcp_logs = Boolean.TRUE;

        @Override
        public String getServiceToCall() {
            return "functionService/v1/exec/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.wcp_logs != null) {
                queryParam.put("wcp_logs", this.wcp_logs);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({})
    public static class Functions implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "functionService/v1/functions";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id") })
    public static class FunctionsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id or Reference_Id=[referenceId] of specified function.")
        private String id;

        @Override
        public String getServiceToCall() {
            return "functionService/v1/functions/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum FunctionServiceSwaggerServiceChoice {
        ExecId,
        Functions,
        FunctionsId;
    }

    @Option
    @Documentation("selected service")
    private FunctionServiceSwaggerServiceChoice service = FunctionServiceSwaggerServiceChoice.ExecId;

    @Option
    @ActiveIf(target = "service", value = "ExecId")
    @Documentation("parameters")
    private ExecId execIdParameters = new ExecId();

    private Functions functionsParameters = new Functions();

    @Option
    @ActiveIf(target = "service", value = "FunctionsId")
    @Documentation("parameters")
    private FunctionsId functionsIdParameters = new FunctionsId();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == FunctionServiceSwaggerServiceChoice.ExecId) {
            return this.execIdParameters;
        }
        if (this.service == FunctionServiceSwaggerServiceChoice.Functions) {
            return this.functionsParameters;
        }
        if (this.service == FunctionServiceSwaggerServiceChoice.FunctionsId) {
            return this.functionsIdParameters;
        }

        return null;
    }
}
