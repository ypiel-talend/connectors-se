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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("definitionsID_PATH_PARAMETERParameters"),
        @GridLayout.Row("definitionsID_PATH_PARAMETERConditionRulesParameters"),
        @GridLayout.Row("definitionsID_PATH_PARAMETERConditionRulesRULE_ID_PATH_PARAMETERParameters"),
        @GridLayout.Row("definitionsID_PATH_PARAMETERFieldsParameters"),
        @GridLayout.Row("definitionsID_PATH_PARAMETERFieldsFIELD_ID_PATH_PARAMETERParameters"),
        @GridLayout.Row("definitionsID_PATH_PARAMETERValidationsParameters"),
        @GridLayout.Row("definitionsID_PATH_PARAMETERValidationsVALIDATION_ID_PATH_PARAMETERParameters"),
        @GridLayout.Row("fieldTypesID_PATH_PARAMETERParameters"),
        @GridLayout.Row("fieldTypesID_PATH_PARAMETERListValuesParameters"),
        @GridLayout.Row("fieldTypesID_PATH_PARAMETERListValuesLIST_ID_PATH_PARAMETERParameters") })
@Documentation("Custom Object Definitions")
public class CustomObjectDefinitionsSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({})
    public static class Definitions implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "customObjectDefinition/v1/definitions";
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
    public static class DefinitionsID_PATH_PARAMETER implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD_PATH_PARAMETER;

        @Override
        public String getServiceToCall() {
            return "customObjectDefinition/v1/definitions/" + this.iD_PATH_PARAMETER + "";
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
    public static class DefinitionsID_PATH_PARAMETERConditionRules implements Serializable, QueryHelper {

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
            return "customObjectDefinition/v1/definitions/" + this.iD_PATH_PARAMETER + "/conditionRules";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD_PATH_PARAMETER"), @GridLayout.Row("rULE_ID_PATH_PARAMETER") })
    public static class DefinitionsID_PATH_PARAMETERConditionRulesRULE_ID_PATH_PARAMETER implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD_PATH_PARAMETER;

        @Option
        @Documentation("Id of specified instance. This is the WID (Workday ID) of the Condition Rule reference.")
        private String rULE_ID_PATH_PARAMETER;

        @Override
        public String getServiceToCall() {
            return "customObjectDefinition/v1/definitions/" + this.iD_PATH_PARAMETER + "/conditionRules/"
                    + this.rULE_ID_PATH_PARAMETER + "";
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
    public static class DefinitionsID_PATH_PARAMETERFields implements Serializable, QueryHelper {

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
            return "customObjectDefinition/v1/definitions/" + this.iD_PATH_PARAMETER + "/fields";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD_PATH_PARAMETER"), @GridLayout.Row("fIELD_ID_PATH_PARAMETER") })
    public static class DefinitionsID_PATH_PARAMETERFieldsFIELD_ID_PATH_PARAMETER implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance. This is the WID (Workday ID) of the Custom Object Definition reference.")
        private String iD_PATH_PARAMETER;

        @Option
        @Documentation("Id of specified instance. This is the WID (Workday ID) of the Custom Field reference.")
        private String fIELD_ID_PATH_PARAMETER;

        @Override
        public String getServiceToCall() {
            return "customObjectDefinition/v1/definitions/" + this.iD_PATH_PARAMETER + "/fields/" + this.fIELD_ID_PATH_PARAMETER
                    + "";
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
    public static class DefinitionsID_PATH_PARAMETERValidations implements Serializable, QueryHelper {

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
            return "customObjectDefinition/v1/definitions/" + this.iD_PATH_PARAMETER + "/validations";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD_PATH_PARAMETER"), @GridLayout.Row("vALIDATION_ID_PATH_PARAMETER") })
    public static class DefinitionsID_PATH_PARAMETERValidationsVALIDATION_ID_PATH_PARAMETER implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD_PATH_PARAMETER;

        @Option
        @Documentation("Id of specified instance. This is the WID (Workday ID) of the Validation reference.")
        private String vALIDATION_ID_PATH_PARAMETER;

        @Override
        public String getServiceToCall() {
            return "customObjectDefinition/v1/definitions/" + this.iD_PATH_PARAMETER + "/validations/"
                    + this.vALIDATION_ID_PATH_PARAMETER + "";
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
    public static class FieldTypes implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "customObjectDefinition/v1/fieldTypes";
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
    public static class FieldTypesID_PATH_PARAMETER implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD_PATH_PARAMETER;

        @Override
        public String getServiceToCall() {
            return "customObjectDefinition/v1/fieldTypes/" + this.iD_PATH_PARAMETER + "";
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
    public static class FieldTypesID_PATH_PARAMETERListValues implements Serializable, QueryHelper {

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
            return "customObjectDefinition/v1/fieldTypes/" + this.iD_PATH_PARAMETER + "/listValues";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD_PATH_PARAMETER"), @GridLayout.Row("lIST_ID_PATH_PARAMETER") })
    public static class FieldTypesID_PATH_PARAMETERListValuesLIST_ID_PATH_PARAMETER implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD_PATH_PARAMETER;

        @Option
        @Documentation("Id of specified instance")
        private String lIST_ID_PATH_PARAMETER;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "customObjectDefinition/v1/fieldTypes/" + this.iD_PATH_PARAMETER + "/listValues/" + this.lIST_ID_PATH_PARAMETER
                    + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum queryView {
        summary,
        detail
    }

    public enum CustomObjectDefinitionsSwaggerServiceChoice {
        Definitions,
        DefinitionsID_PATH_PARAMETER,
        DefinitionsID_PATH_PARAMETERConditionRules,
        DefinitionsID_PATH_PARAMETERConditionRulesRULE_ID_PATH_PARAMETER,
        DefinitionsID_PATH_PARAMETERFields,
        DefinitionsID_PATH_PARAMETERFieldsFIELD_ID_PATH_PARAMETER,
        DefinitionsID_PATH_PARAMETERValidations,
        DefinitionsID_PATH_PARAMETERValidationsVALIDATION_ID_PATH_PARAMETER,
        FieldTypes,
        FieldTypesID_PATH_PARAMETER,
        FieldTypesID_PATH_PARAMETERListValues,
        FieldTypesID_PATH_PARAMETERListValuesLIST_ID_PATH_PARAMETER;
    }

    @Option
    @Documentation("selected service")
    private CustomObjectDefinitionsSwaggerServiceChoice service = CustomObjectDefinitionsSwaggerServiceChoice.Definitions;

    private Definitions definitionsParameters = new Definitions();

    @Option
    @ActiveIf(target = "service", value = "DefinitionsID_PATH_PARAMETER")
    @Documentation("parameters")
    private DefinitionsID_PATH_PARAMETER definitionsID_PATH_PARAMETERParameters = new DefinitionsID_PATH_PARAMETER();

    @Option
    @ActiveIf(target = "service", value = "DefinitionsID_PATH_PARAMETERConditionRules")
    @Documentation("parameters")
    private DefinitionsID_PATH_PARAMETERConditionRules definitionsID_PATH_PARAMETERConditionRulesParameters = new DefinitionsID_PATH_PARAMETERConditionRules();

    @Option
    @ActiveIf(target = "service", value = "DefinitionsID_PATH_PARAMETERConditionRulesRULE_ID_PATH_PARAMETER")
    @Documentation("parameters")
    private DefinitionsID_PATH_PARAMETERConditionRulesRULE_ID_PATH_PARAMETER definitionsID_PATH_PARAMETERConditionRulesRULE_ID_PATH_PARAMETERParameters = new DefinitionsID_PATH_PARAMETERConditionRulesRULE_ID_PATH_PARAMETER();

    @Option
    @ActiveIf(target = "service", value = "DefinitionsID_PATH_PARAMETERFields")
    @Documentation("parameters")
    private DefinitionsID_PATH_PARAMETERFields definitionsID_PATH_PARAMETERFieldsParameters = new DefinitionsID_PATH_PARAMETERFields();

    @Option
    @ActiveIf(target = "service", value = "DefinitionsID_PATH_PARAMETERFieldsFIELD_ID_PATH_PARAMETER")
    @Documentation("parameters")
    private DefinitionsID_PATH_PARAMETERFieldsFIELD_ID_PATH_PARAMETER definitionsID_PATH_PARAMETERFieldsFIELD_ID_PATH_PARAMETERParameters = new DefinitionsID_PATH_PARAMETERFieldsFIELD_ID_PATH_PARAMETER();

    @Option
    @ActiveIf(target = "service", value = "DefinitionsID_PATH_PARAMETERValidations")
    @Documentation("parameters")
    private DefinitionsID_PATH_PARAMETERValidations definitionsID_PATH_PARAMETERValidationsParameters = new DefinitionsID_PATH_PARAMETERValidations();

    @Option
    @ActiveIf(target = "service", value = "DefinitionsID_PATH_PARAMETERValidationsVALIDATION_ID_PATH_PARAMETER")
    @Documentation("parameters")
    private DefinitionsID_PATH_PARAMETERValidationsVALIDATION_ID_PATH_PARAMETER definitionsID_PATH_PARAMETERValidationsVALIDATION_ID_PATH_PARAMETERParameters = new DefinitionsID_PATH_PARAMETERValidationsVALIDATION_ID_PATH_PARAMETER();

    private FieldTypes fieldTypesParameters = new FieldTypes();

    @Option
    @ActiveIf(target = "service", value = "FieldTypesID_PATH_PARAMETER")
    @Documentation("parameters")
    private FieldTypesID_PATH_PARAMETER fieldTypesID_PATH_PARAMETERParameters = new FieldTypesID_PATH_PARAMETER();

    @Option
    @ActiveIf(target = "service", value = "FieldTypesID_PATH_PARAMETERListValues")
    @Documentation("parameters")
    private FieldTypesID_PATH_PARAMETERListValues fieldTypesID_PATH_PARAMETERListValuesParameters = new FieldTypesID_PATH_PARAMETERListValues();

    @Option
    @ActiveIf(target = "service", value = "FieldTypesID_PATH_PARAMETERListValuesLIST_ID_PATH_PARAMETER")
    @Documentation("parameters")
    private FieldTypesID_PATH_PARAMETERListValuesLIST_ID_PATH_PARAMETER fieldTypesID_PATH_PARAMETERListValuesLIST_ID_PATH_PARAMETERParameters = new FieldTypesID_PATH_PARAMETERListValuesLIST_ID_PATH_PARAMETER();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == CustomObjectDefinitionsSwaggerServiceChoice.Definitions) {
            return this.definitionsParameters;
        }
        if (this.service == CustomObjectDefinitionsSwaggerServiceChoice.DefinitionsID_PATH_PARAMETER) {
            return this.definitionsID_PATH_PARAMETERParameters;
        }
        if (this.service == CustomObjectDefinitionsSwaggerServiceChoice.DefinitionsID_PATH_PARAMETERConditionRules) {
            return this.definitionsID_PATH_PARAMETERConditionRulesParameters;
        }
        if (this.service == CustomObjectDefinitionsSwaggerServiceChoice.DefinitionsID_PATH_PARAMETERConditionRulesRULE_ID_PATH_PARAMETER) {
            return this.definitionsID_PATH_PARAMETERConditionRulesRULE_ID_PATH_PARAMETERParameters;
        }
        if (this.service == CustomObjectDefinitionsSwaggerServiceChoice.DefinitionsID_PATH_PARAMETERFields) {
            return this.definitionsID_PATH_PARAMETERFieldsParameters;
        }
        if (this.service == CustomObjectDefinitionsSwaggerServiceChoice.DefinitionsID_PATH_PARAMETERFieldsFIELD_ID_PATH_PARAMETER) {
            return this.definitionsID_PATH_PARAMETERFieldsFIELD_ID_PATH_PARAMETERParameters;
        }
        if (this.service == CustomObjectDefinitionsSwaggerServiceChoice.DefinitionsID_PATH_PARAMETERValidations) {
            return this.definitionsID_PATH_PARAMETERValidationsParameters;
        }
        if (this.service == CustomObjectDefinitionsSwaggerServiceChoice.DefinitionsID_PATH_PARAMETERValidationsVALIDATION_ID_PATH_PARAMETER) {
            return this.definitionsID_PATH_PARAMETERValidationsVALIDATION_ID_PATH_PARAMETERParameters;
        }
        if (this.service == CustomObjectDefinitionsSwaggerServiceChoice.FieldTypes) {
            return this.fieldTypesParameters;
        }
        if (this.service == CustomObjectDefinitionsSwaggerServiceChoice.FieldTypesID_PATH_PARAMETER) {
            return this.fieldTypesID_PATH_PARAMETERParameters;
        }
        if (this.service == CustomObjectDefinitionsSwaggerServiceChoice.FieldTypesID_PATH_PARAMETERListValues) {
            return this.fieldTypesID_PATH_PARAMETERListValuesParameters;
        }
        if (this.service == CustomObjectDefinitionsSwaggerServiceChoice.FieldTypesID_PATH_PARAMETERListValuesLIST_ID_PATH_PARAMETER) {
            return this.fieldTypesID_PATH_PARAMETERListValuesLIST_ID_PATH_PARAMETERParameters;
        }

        return null;
    }
}
