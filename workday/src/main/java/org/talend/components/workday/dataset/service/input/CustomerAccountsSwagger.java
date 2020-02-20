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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("customersParameters"), @GridLayout.Row("customersIDParameters"),
        @GridLayout.Row("invoicePDFsIDParameters"), @GridLayout.Row("invoicesParameters"),
        @GridLayout.Row("invoicesIDParameters"), @GridLayout.Row("invoicesIDPrintRunsParameters"),
        @GridLayout.Row("paymentsIDParameters") })
@Documentation("Customer Accounts")
public class CustomerAccountsSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("name") })
    public static class Customers implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The name of the customer.")
        private String name;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "customerAccounts/v1/customers";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.name != null) {
                queryParam.put("name", this.name);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD") })
    public static class CustomersID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("ID of specified instance.  A list of customer instances can be retrieved by doing a GET on the customers endpoint.")
        private String iD;

        @Override
        public String getServiceToCall() {
            return "customerAccounts/v1/customers/" + this.iD + "";
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
    public static class InvoicePDFsID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("ID of specified instance. The ID of an invoice PDF can be found for a particular invoice by using the printRuns endpoint and selecting pdf ID data element from the returned print run(s).")
        private String iD;

        @Override
        public String getServiceToCall() {
            return "customerAccounts/v1/invoicePDFs/" + this.iD + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("toDueDate"), @GridLayout.Row("fromInvoiceDate"), @GridLayout.Row("fromDueDate"),
            @GridLayout.Row("company"), @GridLayout.Row("transactionType"), @GridLayout.Row("toInvoiceDate"),
            @GridLayout.Row("billToCustomer"), @GridLayout.Row("invoiceStatus"), @GridLayout.Row("paymentStatus") })
    public static class Invoices implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The due date for the reporting transaction.")
        private String toDueDate;

        @Option
        @Documentation("The date of the customer invoice.")
        private String fromInvoiceDate;

        @Option
        @Documentation("The due date for the reporting transaction.")
        private String fromDueDate;

        @Option
        @Documentation("The company for the reporting transaction.")
        private java.util.List<String> company;

        @Option
        @Documentation("The transaction type for the reporting transaction.")
        private String transactionType;

        @Option
        @Documentation("The date of the customer invoice.")
        private String toInvoiceDate;

        @Option
        @Documentation("The customer for the invoice.  This field allows you to drill into the details of the evaluation and access related actions.")
        private java.util.List<String> billToCustomer;

        @Option
        @Documentation("Include invoices with these invoice statuses. Values: Approved (91b0d382d50848e898733757caa9f84a), Canceled (dc76c9b6446c11de98360015c5e6daf6), Denied (dc76c7cc446c11de98360015c5e6daf6), Draft (dc76c4fc446c11de98360015c5e6daf6), Incomplete(e264bc68581342baa70ab61bf71032fe), In Progress (dc76c8bc446c11de98360015c5e6daf6).")
        private java.util.List<String> invoiceStatus;

        @Option
        @Documentation("Include invoices with these payments statuses. Values: Paid (d9e4362a446c11de98360015c5e6daf6), Unpaid (d9e43940446c11de98360015c5e6daf6), Partially Paid (d9e43706446c11de98360015c5e6daf6).")
        private java.util.List<String> paymentStatus;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "customerAccounts/v1/invoices";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.toDueDate != null) {
                queryParam.put("toDueDate", this.toDueDate);
            }
            if (this.fromInvoiceDate != null) {
                queryParam.put("fromInvoiceDate", this.fromInvoiceDate);
            }
            if (this.fromDueDate != null) {
                queryParam.put("fromDueDate", this.fromDueDate);
            }
            if (this.company != null) {
                queryParam.put("company", this.company);
            }
            if (this.transactionType != null) {
                queryParam.put("transactionType", this.transactionType);
            }
            if (this.toInvoiceDate != null) {
                queryParam.put("toInvoiceDate", this.toInvoiceDate);
            }
            if (this.billToCustomer != null) {
                queryParam.put("billToCustomer", this.billToCustomer);
            }
            if (this.invoiceStatus != null) {
                queryParam.put("invoiceStatus", this.invoiceStatus);
            }
            if (this.paymentStatus != null) {
                queryParam.put("paymentStatus", this.paymentStatus);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD") })
    public static class InvoicesID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("ID of specified instance. A list of invoice instances can be retrieved by doing a GET on the invoices endpoint.")
        private String iD;

        @Override
        public String getServiceToCall() {
            return "customerAccounts/v1/invoices/" + this.iD + "";
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
    public static class InvoicesIDPrintRuns implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("ID of specified instance. A list of invoice instances can be retrieved by doing a GET on the invoices endpoint.")
        private String iD;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "customerAccounts/v1/invoices/" + this.iD + "/printRuns";
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
    public static class PaymentsID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("ID of specified instance. When a payment is created via the API the ID is returned in the response to the POST request.")
        private String iD;

        @Override
        public String getServiceToCall() {
            return "customerAccounts/v1/payments/" + this.iD + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum CustomerAccountsSwaggerServiceChoice {
        Customers,
        CustomersID,
        InvoicePDFsID,
        Invoices,
        InvoicesID,
        InvoicesIDPrintRuns,
        PaymentsID;
    }

    @Option
    @Documentation("selected service")
    private CustomerAccountsSwaggerServiceChoice service = CustomerAccountsSwaggerServiceChoice.Customers;

    @Option
    @ActiveIf(target = "service", value = "Customers")
    @Documentation("parameters")
    private Customers customersParameters = new Customers();

    @Option
    @ActiveIf(target = "service", value = "CustomersID")
    @Documentation("parameters")
    private CustomersID customersIDParameters = new CustomersID();

    @Option
    @ActiveIf(target = "service", value = "InvoicePDFsID")
    @Documentation("parameters")
    private InvoicePDFsID invoicePDFsIDParameters = new InvoicePDFsID();

    @Option
    @ActiveIf(target = "service", value = "Invoices")
    @Documentation("parameters")
    private Invoices invoicesParameters = new Invoices();

    @Option
    @ActiveIf(target = "service", value = "InvoicesID")
    @Documentation("parameters")
    private InvoicesID invoicesIDParameters = new InvoicesID();

    @Option
    @ActiveIf(target = "service", value = "InvoicesIDPrintRuns")
    @Documentation("parameters")
    private InvoicesIDPrintRuns invoicesIDPrintRunsParameters = new InvoicesIDPrintRuns();

    @Option
    @ActiveIf(target = "service", value = "PaymentsID")
    @Documentation("parameters")
    private PaymentsID paymentsIDParameters = new PaymentsID();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == CustomerAccountsSwaggerServiceChoice.Customers) {
            return this.customersParameters;
        }
        if (this.service == CustomerAccountsSwaggerServiceChoice.CustomersID) {
            return this.customersIDParameters;
        }
        if (this.service == CustomerAccountsSwaggerServiceChoice.InvoicePDFsID) {
            return this.invoicePDFsIDParameters;
        }
        if (this.service == CustomerAccountsSwaggerServiceChoice.Invoices) {
            return this.invoicesParameters;
        }
        if (this.service == CustomerAccountsSwaggerServiceChoice.InvoicesID) {
            return this.invoicesIDParameters;
        }
        if (this.service == CustomerAccountsSwaggerServiceChoice.InvoicesIDPrintRuns) {
            return this.invoicesIDPrintRunsParameters;
        }
        if (this.service == CustomerAccountsSwaggerServiceChoice.PaymentsID) {
            return this.paymentsIDParameters;
        }

        return null;
    }
}
