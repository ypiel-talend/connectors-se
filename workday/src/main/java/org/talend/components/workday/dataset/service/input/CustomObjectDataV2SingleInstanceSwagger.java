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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("customObjectsCustomObjectAliasCustomObjectIDParameters") })
@Documentation("Custom Object Data v2 (Single-Instance)")
public class CustomObjectDataV2SingleInstanceSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 2L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("customObjectAlias"), @GridLayout.Row("customObjectID") })
    public static class CustomObjectsCustomObjectAliasCustomObjectID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Web service alias for the custom object definition. When the custom object is created, the alias is supplied by the object creator.")
        private String customObjectAlias;

        @Option
        @Documentation("For single-instance custom objects, the custom object ID is any valid ID for the custom object, OR any valid ID for the extended Workday object. Example: The ID of the Parking Space of a Worker or the ID of that Worker can be used interchangeably for that Parking Space.")
        private String customObjectID;

        @Override
        public String getServiceToCall() {
            return "customObject/v2/customObjects/" + this.customObjectAlias + "/" + this.customObjectID + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum CustomObjectDataV2SingleInstanceSwaggerServiceChoice {
        CustomObjectsCustomObjectAliasCustomObjectID;
    }

    @Option
    @Documentation("selected service")
    private CustomObjectDataV2SingleInstanceSwaggerServiceChoice service = CustomObjectDataV2SingleInstanceSwaggerServiceChoice.CustomObjectsCustomObjectAliasCustomObjectID;

    @Option
    @ActiveIf(target = "service", value = "CustomObjectsCustomObjectAliasCustomObjectID")
    @Documentation("parameters")
    private CustomObjectsCustomObjectAliasCustomObjectID customObjectsCustomObjectAliasCustomObjectIDParameters = new CustomObjectsCustomObjectAliasCustomObjectID();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == CustomObjectDataV2SingleInstanceSwaggerServiceChoice.CustomObjectsCustomObjectAliasCustomObjectID) {
            return this.customObjectsCustomObjectAliasCustomObjectIDParameters;
        }

        return null;
    }
}
