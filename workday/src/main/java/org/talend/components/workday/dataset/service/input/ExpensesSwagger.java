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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("entriesParameters"), @GridLayout.Row("entriesIdParameters"),
        @GridLayout.Row("expenseItemsParameters"), @GridLayout.Row("reportsParameters") })
@Documentation("Expenses")
public class ExpensesSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("fromDate"), @GridLayout.Row("toDate"), @GridLayout.Row("expenseEntryStatus"),
            @GridLayout.Row("entryType") })
    public static class Entries implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The earliest date for which to retrieve the Expense Entries, inclusive of the date.")
        private String fromDate;

        @Option
        @Documentation("The latest date for which to retrieve the Expense Entries, inclusive of the date.")
        private String toDate;

        @Option
        @Documentation("The status for the expense entries. Possible WIDs for this query parameter are\n  New -> dc74b9a0446c11de98360015c5e6daf6\n  Pending -> dc74b8b0446c11de98360015c5e6daf6\n  Expensed -> dc74ba90446c11de98360015c5e6daf6\n  Closed -> dc74bb80446c11de98360015c5e6daf6\n  Prepaid -> fdef527489e34c00b438b5dd7dacee79\n  Do Not Show -> 281f291f7a1b4a2b8c9e94b0e733510e\n  Paid -> d5478541ee1b431ab34a28339b40caf5")
        private String expenseEntryStatus;

        @Option
        @Documentation("The type of expense for the expense entries. Possible WIDs for this query parameter are\n  Quick Expense -> 744524587a9b4f74bdaa69fd7ffb91fc\n  Credit Card Transaction -> cd152f26446c11de98360015c5e6daf6\n  Travel Booking Record -> 3b370c53eda4100004772fea0e870002")
        private String entryType;

        @Override
        public String getServiceToCall() {
            return "expense/v1/entries";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.fromDate != null) {
                queryParam.put("fromDate", this.fromDate);
            }
            if (this.toDate != null) {
                queryParam.put("toDate", this.toDate);
            }
            if (this.expenseEntryStatus != null) {
                queryParam.put("expenseEntryStatus", this.expenseEntryStatus);
            }
            if (this.entryType != null) {
                queryParam.put("entryType", this.entryType);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id") })
    public static class EntriesId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The unique Workday Identifier (WID) for the resource. Only WID will work here.")
        private String id;

        @Override
        public String getServiceToCall() {
            return "expense/v1/entries/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("searchString"), @GridLayout.Row("disallowFixedItems") })
    public static class ExpenseItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("String input to be used to generate an array of Expense Items.")
        private String searchString;

        @Option
        @Documentation("Boolean input to be filter fixed items.")
        private Boolean disallowFixedItems = Boolean.TRUE;

        @Override
        public String getServiceToCall() {
            return "expense/v1/expenseItems";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.searchString != null) {
                queryParam.put("searchString", this.searchString);
            }
            if (this.disallowFixedItems != null) {
                queryParam.put("disallowFixedItems", this.disallowFixedItems);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("expenseReportStatus"), @GridLayout.Row("expenseReportMemo"), @GridLayout.Row("fromDate"),
            @GridLayout.Row("toDate") })
    public static class Reports implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Status of the Expense Report. Possible statuses are -\n  Draft-> dc76c4fc446c11de98360015c5e6daf6\n  In Progress-> dc76c8bc446c11de98360015c5e6daf6\n  Approved-> 91b0d382d50848e898733757caa9f84a\n  Canceled-> dc76c9b6446c11de98360015c5e6daf6")
        private String expenseReportStatus;

        @Option
        @Documentation("Name of Expense Report")
        private String expenseReportMemo;

        @Option
        @Documentation("Part of date range (creation date) to search for Expense Reports. If start date is not provided, it becomes end date.")
        private String fromDate;

        @Option
        @Documentation("Part of date range (creation date) to search for Expense Reports. If end date is not provided, it becomes current date. If neither is provided, then all available expenses are displayed.")
        private String toDate;

        @Override
        public String getServiceToCall() {
            return "expense/v1/reports";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.expenseReportStatus != null) {
                queryParam.put("expenseReportStatus", this.expenseReportStatus);
            }
            if (this.expenseReportMemo != null) {
                queryParam.put("expenseReportMemo", this.expenseReportMemo);
            }
            if (this.fromDate != null) {
                queryParam.put("fromDate", this.fromDate);
            }
            if (this.toDate != null) {
                queryParam.put("toDate", this.toDate);
            }
            return queryParam;
        }
    }

    public enum ExpensesSwaggerServiceChoice {
        Entries,
        EntriesId,
        ExpenseItems,
        Reports;
    }

    @Option
    @Documentation("selected service")
    private ExpensesSwaggerServiceChoice service = ExpensesSwaggerServiceChoice.Entries;

    @Option
    @ActiveIf(target = "service", value = "Entries")
    @Documentation("parameters")
    private Entries entriesParameters = new Entries();

    @Option
    @ActiveIf(target = "service", value = "EntriesId")
    @Documentation("parameters")
    private EntriesId entriesIdParameters = new EntriesId();

    @Option
    @ActiveIf(target = "service", value = "ExpenseItems")
    @Documentation("parameters")
    private ExpenseItems expenseItemsParameters = new ExpenseItems();

    @Option
    @ActiveIf(target = "service", value = "Reports")
    @Documentation("parameters")
    private Reports reportsParameters = new Reports();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == ExpensesSwaggerServiceChoice.Entries) {
            return this.entriesParameters;
        }
        if (this.service == ExpensesSwaggerServiceChoice.EntriesId) {
            return this.entriesIdParameters;
        }
        if (this.service == ExpensesSwaggerServiceChoice.ExpenseItems) {
            return this.expenseItemsParameters;
        }
        if (this.service == ExpensesSwaggerServiceChoice.Reports) {
            return this.reportsParameters;
        }

        return null;
    }
}
