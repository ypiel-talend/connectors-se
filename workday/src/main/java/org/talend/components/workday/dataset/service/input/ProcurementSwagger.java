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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("requisitionsParameters"),
        @GridLayout.Row("requisitionsRequisitionIdParameters"),
        @GridLayout.Row("requisitionsRequisitionIdRelatedPurchaseOrdersParameters"),
        @GridLayout.Row("requisitionsRequisitionIdRequisitionLinesParameters"),
        @GridLayout.Row("requisitionsRequisitionIdRequisitionLinesRequisitionLineIdParameters"),
        @GridLayout.Row("requisitionsRequisitionIdattachmentsParameters"),
        @GridLayout.Row("requisitionsRequisitionIdattachmentsattachmentIdParameters") })
@Documentation("Procurement")
public class ProcurementSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("submittedBy"), @GridLayout.Row("submittedByPerson"), @GridLayout.Row("submittedBySupplier"),
            @GridLayout.Row("fromDate"), @GridLayout.Row("toDate"), @GridLayout.Row("requester"),
            @GridLayout.Row("requisitionType") })
    public static class Requisitions implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Filter requisitions by submitted Worker. Expects the workday id or the reference id of the worker.")
        private String submittedBy;

        @Option
        @Documentation("Filter requisitions by submitted Person. Expects the workday id or the reference id of the person.")
        private String submittedByPerson;

        @Option
        @Documentation("Filter requisitions by submitted Supplier. Expects the workday id or the reference id of the supplier.")
        private String submittedBySupplier;

        @Option
        @Documentation("Filter requisitions with document date after the date specified.")
        private String fromDate;

        @Option
        @Documentation("Filter requisitions with document date on or before the date specified.")
        private String toDate;

        @Option
        @Documentation("Filter requisitions by requester.")
        private String requester;

        @Option
        @Documentation("Filter requisitions by requisition type.")
        private String requisitionType;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "procurement/v1/requisitions/";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.submittedBy != null) {
                queryParam.put("submittedBy", this.submittedBy);
            }
            if (this.submittedByPerson != null) {
                queryParam.put("submittedByPerson", this.submittedByPerson);
            }
            if (this.submittedBySupplier != null) {
                queryParam.put("submittedBySupplier", this.submittedBySupplier);
            }
            if (this.fromDate != null) {
                queryParam.put("fromDate", this.fromDate);
            }
            if (this.toDate != null) {
                queryParam.put("toDate", this.toDate);
            }
            if (this.requester != null) {
                queryParam.put("requester", this.requester);
            }
            if (this.requisitionType != null) {
                queryParam.put("requisitionType", this.requisitionType);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("requisitionId") })
    public static class RequisitionsRequisitionId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The unique Requisition identifier. This can either be a workday id (WID), or a reference id in the form of '<identifier-type>=<identifier>' - for example, 'Requisition_Number=REQ-1234'.")
        private String requisitionId;

        @Override
        public String getServiceToCall() {
            return "procurement/v1/requisitions/" + this.requisitionId + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("requisitionId") })
    public static class RequisitionsRequisitionIdattachments implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The unique Requisition identifier. This can either be a workday id (WID), or a reference id in the form of '<identifier-type>=<identifier>' - for example, 'Requisition_Number=REQ-1234'.")
        private String requisitionId;

        @Override
        public String getServiceToCall() {
            return "procurement/v1/requisitions/" + this.requisitionId + "/attachments";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("requisitionId"), @GridLayout.Row("attachmentId"), @GridLayout.Row("type") })
    public static class RequisitionsRequisitionIdattachmentsattachmentId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The unique Requisition identifier. This can either be a workday id (WID), or a reference id in the form of '<identifier-type>=<identifier>' - for example, 'Requisition_Number=REQ-1234'.")
        private String requisitionId;

        @Option
        @Documentation("Unique attachment identifier (WID)")
        private String attachmentId;

        @Option
        @Documentation("if this parameter is passed with value 'getFileContent', the response will be the binary file content instead.")
        private String type;

        @Override
        public String getServiceToCall() {
            return "procurement/v1/requisitions/" + this.requisitionId + "/attachments/" + this.attachmentId + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.type != null) {
                queryParam.put("type", this.type);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("requisitionId") })
    public static class RequisitionsRequisitionIdRelatedPurchaseOrders implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The unique Requisition identifier. This can either be a workday id (WID), or a reference id in the form of '<identifier-type>=<identifier>' - for example, 'Requisition_Number=REQ-1234'.")
        private String requisitionId;

        @Override
        public String getServiceToCall() {
            return "procurement/v1/requisitions/" + this.requisitionId + "/relatedPurchaseOrders";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("requisitionId") })
    public static class RequisitionsRequisitionIdRequisitionLines implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The unique Requisition identifier. This can either be a workday id (WID), or a reference id in the form of '<identifier-type>=<identifier>' - for example, 'Requisition_Number=REQ-1234'.")
        private String requisitionId;

        @Override
        public String getServiceToCall() {
            return "procurement/v1/requisitions/" + this.requisitionId + "/requisitionLines/";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("requisitionId"), @GridLayout.Row("requisitionLineId") })
    public static class RequisitionsRequisitionIdRequisitionLinesRequisitionLineId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The unique Requisition identifier. This can either be a workday id (WID), or a reference id in the form of '<identifier-type>=<identifier>' - for example, 'Requisition_Number=REQ-1234'.")
        private String requisitionId;

        @Option
        @Documentation("The unique Requisition Line identifier. This has to be a workday id (WID).")
        private String requisitionLineId;

        @Override
        public String getServiceToCall() {
            return "procurement/v1/requisitions/" + this.requisitionId + "/requisitionLines/" + this.requisitionLineId + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum ProcurementSwaggerServiceChoice {
        Requisitions,
        RequisitionsRequisitionId,
        RequisitionsRequisitionIdRelatedPurchaseOrders,
        RequisitionsRequisitionIdRequisitionLines,
        RequisitionsRequisitionIdRequisitionLinesRequisitionLineId,
        RequisitionsRequisitionIdattachments,
        RequisitionsRequisitionIdattachmentsattachmentId;
    }

    @Option
    @Documentation("selected service")
    private ProcurementSwaggerServiceChoice service = ProcurementSwaggerServiceChoice.Requisitions;

    @Option
    @ActiveIf(target = "service", value = "Requisitions")
    @Documentation("parameters")
    private Requisitions requisitionsParameters = new Requisitions();

    @Option
    @ActiveIf(target = "service", value = "RequisitionsRequisitionId")
    @Documentation("parameters")
    private RequisitionsRequisitionId requisitionsRequisitionIdParameters = new RequisitionsRequisitionId();

    @Option
    @ActiveIf(target = "service", value = "RequisitionsRequisitionIdRelatedPurchaseOrders")
    @Documentation("parameters")
    private RequisitionsRequisitionIdRelatedPurchaseOrders requisitionsRequisitionIdRelatedPurchaseOrdersParameters = new RequisitionsRequisitionIdRelatedPurchaseOrders();

    @Option
    @ActiveIf(target = "service", value = "RequisitionsRequisitionIdRequisitionLines")
    @Documentation("parameters")
    private RequisitionsRequisitionIdRequisitionLines requisitionsRequisitionIdRequisitionLinesParameters = new RequisitionsRequisitionIdRequisitionLines();

    @Option
    @ActiveIf(target = "service", value = "RequisitionsRequisitionIdRequisitionLinesRequisitionLineId")
    @Documentation("parameters")
    private RequisitionsRequisitionIdRequisitionLinesRequisitionLineId requisitionsRequisitionIdRequisitionLinesRequisitionLineIdParameters = new RequisitionsRequisitionIdRequisitionLinesRequisitionLineId();

    @Option
    @ActiveIf(target = "service", value = "RequisitionsRequisitionIdattachments")
    @Documentation("parameters")
    private RequisitionsRequisitionIdattachments requisitionsRequisitionIdattachmentsParameters = new RequisitionsRequisitionIdattachments();

    @Option
    @ActiveIf(target = "service", value = "RequisitionsRequisitionIdattachmentsattachmentId")
    @Documentation("parameters")
    private RequisitionsRequisitionIdattachmentsattachmentId requisitionsRequisitionIdattachmentsattachmentIdParameters = new RequisitionsRequisitionIdattachmentsattachmentId();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == ProcurementSwaggerServiceChoice.Requisitions) {
            return this.requisitionsParameters;
        }
        if (this.service == ProcurementSwaggerServiceChoice.RequisitionsRequisitionId) {
            return this.requisitionsRequisitionIdParameters;
        }
        if (this.service == ProcurementSwaggerServiceChoice.RequisitionsRequisitionIdRelatedPurchaseOrders) {
            return this.requisitionsRequisitionIdRelatedPurchaseOrdersParameters;
        }
        if (this.service == ProcurementSwaggerServiceChoice.RequisitionsRequisitionIdRequisitionLines) {
            return this.requisitionsRequisitionIdRequisitionLinesParameters;
        }
        if (this.service == ProcurementSwaggerServiceChoice.RequisitionsRequisitionIdRequisitionLinesRequisitionLineId) {
            return this.requisitionsRequisitionIdRequisitionLinesRequisitionLineIdParameters;
        }
        if (this.service == ProcurementSwaggerServiceChoice.RequisitionsRequisitionIdattachments) {
            return this.requisitionsRequisitionIdattachmentsParameters;
        }
        if (this.service == ProcurementSwaggerServiceChoice.RequisitionsRequisitionIdattachmentsattachmentId) {
            return this.requisitionsRequisitionIdattachmentsattachmentIdParameters;
        }

        return null;
    }
}
