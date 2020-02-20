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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("customObjectsCustomObjectAliasCustomObjectIDParameters"),
        @GridLayout.Row("extendedBusinessObjectResourceExtendedBusinessObjectIdCustomObjectsCustomObjectAliasParameters") })
@Documentation("Custom Object Data v2 (Multi-Instance)")
public class CustomObjectDataV2MultiInstanceSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 2L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("customObjectAlias"), @GridLayout.Row("customObjectID") })
    public static class CustomObjectsCustomObjectAliasCustomObjectID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Web service alias for the custom object. When the custom object is created, the alias is supplied by the object creator.")
        private String customObjectAlias;

        @Option
        @Documentation("For multi-instance custom objects, the custom object ID is any valid ID for the custom object which includes the compound ID for the custom object. The compound ID is defined as a concatenation of the WID of the extended business object, a semi-colon, and the reference ID of the custom object. Example: The Worker ID (ID=66faa65677834f0a9b586dd5645e900) links with the reference ID (grantName=Merit_4Q2011), separated by a semicolon (;) to look like: /customObjects/stockGrants/66faa65677834f0a9b586dd5645e9004;grantName=Merit_4Q2011")
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

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("extendedBusinessObjectResource"), @GridLayout.Row("extendedBusinessObjectId"),
            @GridLayout.Row("customObjectAlias") })
    public static class ExtendedBusinessObjectResourceExtendedBusinessObjectIdCustomObjectsCustomObjectAlias
            implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Business Object that has been extended by the multi-instance custom object")
        private pathExtendedBusinessObjectResource extendedBusinessObjectResource = pathExtendedBusinessObjectResource.academicAffiliates;

        @Option
        @Documentation("ID of the instance of the Extended Business Object")
        private String extendedBusinessObjectId;

        @Option
        @Documentation("Web service alias for the custom object definition. When the custom object is created, the alias is supplied by the object creator.")
        private String customObjectAlias;

        @Override
        public String getServiceToCall() {
            return "customObject/v2/" + this.extendedBusinessObjectResource + "/" + this.extendedBusinessObjectId
                    + "/customObjects/" + this.customObjectAlias + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum pathExtendedBusinessObjectResource {
        academicAffiliates,
        academicUnits,
        academicUnitHierarchies,
        bankAccounts,
        businessAssets,
        companies,
        costCenters,
        customers,
        customerContracts,
        customerContractLines,
        customerInvoice,
        customerInvoiceAdjustments,
        customerInvoiceLine,
        formerWorkers,
        jobProfiles,
        ledgerAccounts,
        loans,
        locations,
        positionRestrictions,
        projects,
        applicants,
        purchaseOrders,
        regions,
        supervisoryOrganizations,
        suppliers,
        supplierContracts,
        supplierInvoices,
        supplierInvoiceAdjustments,
        workers
    }

    public enum CustomObjectDataV2MultiInstanceSwaggerServiceChoice {
        CustomObjectsCustomObjectAliasCustomObjectID,
        ExtendedBusinessObjectResourceExtendedBusinessObjectIdCustomObjectsCustomObjectAlias;
    }

    @Option
    @Documentation("selected service")
    private CustomObjectDataV2MultiInstanceSwaggerServiceChoice service = CustomObjectDataV2MultiInstanceSwaggerServiceChoice.CustomObjectsCustomObjectAliasCustomObjectID;

    @Option
    @ActiveIf(target = "service", value = "CustomObjectsCustomObjectAliasCustomObjectID")
    @Documentation("parameters")
    private CustomObjectsCustomObjectAliasCustomObjectID customObjectsCustomObjectAliasCustomObjectIDParameters = new CustomObjectsCustomObjectAliasCustomObjectID();

    @Option
    @ActiveIf(target = "service", value = "ExtendedBusinessObjectResourceExtendedBusinessObjectIdCustomObjectsCustomObjectAlias")
    @Documentation("parameters")
    private ExtendedBusinessObjectResourceExtendedBusinessObjectIdCustomObjectsCustomObjectAlias extendedBusinessObjectResourceExtendedBusinessObjectIdCustomObjectsCustomObjectAliasParameters = new ExtendedBusinessObjectResourceExtendedBusinessObjectIdCustomObjectsCustomObjectAlias();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == CustomObjectDataV2MultiInstanceSwaggerServiceChoice.CustomObjectsCustomObjectAliasCustomObjectID) {
            return this.customObjectsCustomObjectAliasCustomObjectIDParameters;
        }
        if (this.service == CustomObjectDataV2MultiInstanceSwaggerServiceChoice.ExtendedBusinessObjectResourceExtendedBusinessObjectIdCustomObjectsCustomObjectAlias) {
            return this.extendedBusinessObjectResourceExtendedBusinessObjectIdCustomObjectsCustomObjectAliasParameters;
        }

        return null;
    }
}
