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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("calendar_yearVersLatestItemsParameters"),
        @GridLayout.Row("calendar_yearVersLatestItemsIdParameters"), @GridLayout.Row("calendar_yearVersLatestSearchParameters"),
        @GridLayout.Row("calendar_yearVersLatestSearchScoredParameters"), @GridLayout.Row("cip_codeVersLatestItemsParameters"),
        @GridLayout.Row("cip_codeVersLatestItemsIdParameters"), @GridLayout.Row("cip_codeVersLatestSearchParameters"),
        @GridLayout.Row("cip_codeVersLatestSearchScoredParameters"),
        @GridLayout.Row("communication_usage_typeVersLatestItemsParameters"),
        @GridLayout.Row("communication_usage_typeVersLatestItemsIdParameters"),
        @GridLayout.Row("communication_usage_typeVersLatestSearchParameters"),
        @GridLayout.Row("communication_usage_typeVersLatestSearchScoredParameters"),
        @GridLayout.Row("countryVersLatestItemsParameters"), @GridLayout.Row("countryVersLatestItemsIdParameters"),
        @GridLayout.Row("countryVersLatestSearchParameters"), @GridLayout.Row("countryVersLatestSearchScoredParameters"),
        @GridLayout.Row("country_cityVersLatestItemsParameters"), @GridLayout.Row("country_cityVersLatestItemsIdParameters"),
        @GridLayout.Row("country_cityVersLatestSearchParameters"),
        @GridLayout.Row("country_cityVersLatestSearchScoredParameters"),
        @GridLayout.Row("country_phone_codeVersLatestItemsParameters"),
        @GridLayout.Row("country_phone_codeVersLatestItemsIdParameters"),
        @GridLayout.Row("country_phone_codeVersLatestSearchParameters"),
        @GridLayout.Row("country_phone_codeVersLatestSearchScoredParameters"),
        @GridLayout.Row("country_regionVersLatestItemsParameters"), @GridLayout.Row("country_regionVersLatestItemsIdParameters"),
        @GridLayout.Row("country_regionVersLatestSearchParameters"),
        @GridLayout.Row("country_regionVersLatestSearchScoredParameters"),
        @GridLayout.Row("country_region_typeVersLatestItemsParameters"),
        @GridLayout.Row("country_region_typeVersLatestItemsIdParameters"),
        @GridLayout.Row("country_region_typeVersLatestSearchParameters"),
        @GridLayout.Row("country_region_typeVersLatestSearchScoredParameters"),
        @GridLayout.Row("currencyVersLatestItemsParameters"), @GridLayout.Row("currencyVersLatestItemsIdParameters"),
        @GridLayout.Row("currencyVersLatestSearchParameters"), @GridLayout.Row("currencyVersLatestSearchScoredParameters"),
        @GridLayout.Row("custom_org_worktag_dimensionVersLatestItemsParameters"),
        @GridLayout.Row("custom_org_worktag_dimensionVersLatestItemsIdParameters"),
        @GridLayout.Row("custom_org_worktag_dimensionVersLatestSearchParameters"),
        @GridLayout.Row("custom_org_worktag_dimensionVersLatestSearchScoredParameters"),
        @GridLayout.Row("document_statusVersLatestItemsParameters"),
        @GridLayout.Row("document_statusVersLatestItemsIdParameters"),
        @GridLayout.Row("document_statusVersLatestSearchParameters"),
        @GridLayout.Row("document_statusVersLatestSearchScoredParameters"),
        @GridLayout.Row("enrollment_statusVersLatestItemsParameters"),
        @GridLayout.Row("enrollment_statusVersLatestItemsIdParameters"),
        @GridLayout.Row("enrollment_statusVersLatestSearchParameters"),
        @GridLayout.Row("enrollment_statusVersLatestSearchScoredParameters"),
        @GridLayout.Row("financial_aid_award_yearVersLatestItemsParameters"),
        @GridLayout.Row("financial_aid_award_yearVersLatestItemsIdParameters"),
        @GridLayout.Row("financial_aid_award_yearVersLatestSearchParameters"),
        @GridLayout.Row("financial_aid_award_yearVersLatestSearchScoredParameters"),
        @GridLayout.Row("jp_addressesVersV10ItemsParameters"), @GridLayout.Row("jp_addressesVersV10ItemsIdParameters"),
        @GridLayout.Row("jp_addressesVersV10SearchParameters"), @GridLayout.Row("jp_addressesVersV10SearchScoredParameters"),
        @GridLayout.Row("national_id_typeVersLatestItemsParameters"),
        @GridLayout.Row("national_id_typeVersLatestItemsIdParameters"),
        @GridLayout.Row("national_id_typeVersLatestSearchParameters"),
        @GridLayout.Row("national_id_typeVersLatestSearchScoredParameters"),
        @GridLayout.Row("postal_codeVersLatestItemsParameters"), @GridLayout.Row("postal_codeVersLatestItemsIdParameters"),
        @GridLayout.Row("postal_codeVersLatestSearchParameters"), @GridLayout.Row("postal_codeVersLatestSearchScoredParameters"),
        @GridLayout.Row("question_typeVersLatestItemsParameters"), @GridLayout.Row("question_typeVersLatestItemsIdParameters"),
        @GridLayout.Row("question_typeVersLatestSearchParameters"),
        @GridLayout.Row("question_typeVersLatestSearchScoredParameters"),
        @GridLayout.Row("recruiting_sourceVersLatestItemsParameters"),
        @GridLayout.Row("recruiting_sourceVersLatestItemsIdParameters"),
        @GridLayout.Row("recruiting_sourceVersLatestSearchParameters"),
        @GridLayout.Row("recruiting_sourceVersLatestSearchScoredParameters"),
        @GridLayout.Row("related_person_relationship_typeVersLatestItemsParameters"),
        @GridLayout.Row("related_person_relationship_typeVersLatestItemsIdParameters"),
        @GridLayout.Row("related_person_relationship_typeVersLatestSearchParameters"),
        @GridLayout.Row("related_person_relationship_typeVersLatestSearchScoredParameters"),
        @GridLayout.Row("retention_risk_factor_typeVersLatestItemsParameters"),
        @GridLayout.Row("retention_risk_factor_typeVersLatestItemsIdParameters"),
        @GridLayout.Row("retention_risk_factor_typeVersLatestSearchParameters"),
        @GridLayout.Row("retention_risk_factor_typeVersLatestSearchScoredParameters"),
        @GridLayout.Row("severance_service_dateVersLatestItemsParameters"),
        @GridLayout.Row("severance_service_dateVersLatestItemsIdParameters"),
        @GridLayout.Row("severance_service_dateVersLatestSearchParameters"),
        @GridLayout.Row("severance_service_dateVersLatestSearchScoredParameters"),
        @GridLayout.Row("social_network_meta_typeVersLatestItemsParameters"),
        @GridLayout.Row("social_network_meta_typeVersLatestItemsIdParameters"),
        @GridLayout.Row("social_network_meta_typeVersLatestSearchParameters"),
        @GridLayout.Row("social_network_meta_typeVersLatestSearchScoredParameters"),
        @GridLayout.Row("taggable_typeVersLatestItemsParameters"), @GridLayout.Row("taggable_typeVersLatestItemsIdParameters"),
        @GridLayout.Row("taggable_typeVersLatestSearchParameters"),
        @GridLayout.Row("taggable_typeVersLatestSearchScoredParameters"), @GridLayout.Row("tax_id_typeVersLatestItemsParameters"),
        @GridLayout.Row("tax_id_typeVersLatestItemsIdParameters"), @GridLayout.Row("tax_id_typeVersLatestSearchParameters"),
        @GridLayout.Row("tax_id_typeVersLatestSearchScoredParameters"), @GridLayout.Row("user_languageVersLatestItemsParameters"),
        @GridLayout.Row("user_languageVersLatestItemsIdParameters"), @GridLayout.Row("user_languageVersLatestSearchParameters"),
        @GridLayout.Row("user_languageVersLatestSearchScoredParameters"),
        @GridLayout.Row("worktag_aggregation_typeVersLatestItemsParameters"),
        @GridLayout.Row("worktag_aggregation_typeVersLatestItemsIdParameters"),
        @GridLayout.Row("worktag_aggregation_typeVersLatestSearchParameters"),
        @GridLayout.Row("worktag_aggregation_typeVersLatestSearchScoredParameters"),
        @GridLayout.Row("worktag_typeVersLatestItemsParameters"), @GridLayout.Row("worktag_typeVersLatestItemsIdParameters"),
        @GridLayout.Row("worktag_typeVersLatestSearchParameters"),
        @GridLayout.Row("worktag_typeVersLatestSearchScoredParameters"),
        @GridLayout.Row("worktag_usageVersLatestItemsParameters"), @GridLayout.Row("worktag_usageVersLatestItemsIdParameters"),
        @GridLayout.Row("worktag_usageVersLatestSearchParameters"),
        @GridLayout.Row("worktag_usageVersLatestSearchScoredParameters"),
        @GridLayout.Row("worktags_rest_validation_idVersLatestItemsParameters"),
        @GridLayout.Row("worktags_rest_validation_idVersLatestItemsIdParameters"),
        @GridLayout.Row("worktags_rest_validation_idVersLatestSearchParameters"),
        @GridLayout.Row("worktags_rest_validation_idVersLatestSearchScoredParameters"),
        @GridLayout.Row("aca_4980h_safe_harborVersLatestItemsParameters"),
        @GridLayout.Row("aca_4980h_safe_harborVersLatestItemsIdParameters"),
        @GridLayout.Row("aca_4980h_safe_harborVersLatestSearchParameters"),
        @GridLayout.Row("aca_4980h_safe_harborVersLatestSearchScoredParameters"),
        @GridLayout.Row("admission_decisionVersLatestItemsParameters"),
        @GridLayout.Row("admission_decisionVersLatestItemsIdParameters"),
        @GridLayout.Row("admission_decisionVersLatestSearchParameters"),
        @GridLayout.Row("admission_decisionVersLatestSearchScoredParameters") })
@Documentation("Reference Data")
public class ReferenceDataSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class aca_4980h_safe_harborVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/aca_4980h_safe_harbor/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class aca_4980h_safe_harborVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/aca_4980h_safe_harbor/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class aca_4980h_safe_harborVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/aca_4980h_safe_harbor/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class aca_4980h_safe_harborVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/aca_4980h_safe_harbor/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class admission_decisionVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/admission_decision/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class admission_decisionVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/admission_decision/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class admission_decisionVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/admission_decision/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class admission_decisionVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/admission_decision/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Calendar_yearVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/calendar_year/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Calendar_yearVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/calendar_year/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Calendar_yearVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/calendar_year/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Calendar_yearVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/calendar_year/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Cip_codeVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/cip_code/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Cip_codeVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/cip_code/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Cip_codeVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/cip_code/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Cip_codeVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/cip_code/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Communication_usage_typeVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/communication_usage_type/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Communication_usage_typeVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/communication_usage_type/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Communication_usage_typeVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/communication_usage_type/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Communication_usage_typeVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/communication_usage_type/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class CountryVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/country/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class CountryVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/country/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class CountryVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/country/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class CountryVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/country/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Country_cityVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/country_city/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Country_cityVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/country_city/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Country_cityVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/country_city/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Country_cityVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/country_city/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Country_phone_codeVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/country_phone_code/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Country_phone_codeVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/country_phone_code/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Country_phone_codeVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/country_phone_code/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Country_phone_codeVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/country_phone_code/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Country_regionVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/country_region/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Country_regionVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/country_region/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Country_regionVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/country_region/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Country_regionVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/country_region/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Country_region_typeVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/country_region_type/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Country_region_typeVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/country_region_type/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Country_region_typeVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/country_region_type/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Country_region_typeVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/country_region_type/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class CurrencyVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/currency/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class CurrencyVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/currency/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class CurrencyVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/currency/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class CurrencyVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/currency/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Custom_org_worktag_dimensionVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/custom_org_worktag_dimension/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Custom_org_worktag_dimensionVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/custom_org_worktag_dimension/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Custom_org_worktag_dimensionVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/custom_org_worktag_dimension/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Custom_org_worktag_dimensionVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/custom_org_worktag_dimension/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Document_statusVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/document_status/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Document_statusVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/document_status/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Document_statusVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/document_status/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Document_statusVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/document_status/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Enrollment_statusVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/enrollment_status/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Enrollment_statusVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/enrollment_status/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Enrollment_statusVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/enrollment_status/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Enrollment_statusVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/enrollment_status/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Financial_aid_award_yearVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/financial_aid_award_year/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Financial_aid_award_yearVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/financial_aid_award_year/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Financial_aid_award_yearVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/financial_aid_award_year/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Financial_aid_award_yearVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/financial_aid_award_year/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Jp_addressesVersV10Items implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/jp_addresses/vers/v1.0/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Jp_addressesVersV10ItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/jp_addresses/vers/v1.0/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Jp_addressesVersV10Search implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/jp_addresses/vers/v1.0/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Jp_addressesVersV10SearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/jp_addresses/vers/v1.0/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class National_id_typeVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/national_id_type/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class National_id_typeVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/national_id_type/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class National_id_typeVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/national_id_type/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class National_id_typeVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/national_id_type/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Postal_codeVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/postal_code/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Postal_codeVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/postal_code/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Postal_codeVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/postal_code/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Postal_codeVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/postal_code/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Question_typeVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/question_type/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Question_typeVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/question_type/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Question_typeVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/question_type/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Question_typeVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/question_type/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Recruiting_sourceVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/recruiting_source/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Recruiting_sourceVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/recruiting_source/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Recruiting_sourceVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/recruiting_source/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Recruiting_sourceVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/recruiting_source/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Related_person_relationship_typeVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/related_person_relationship_type/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Related_person_relationship_typeVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/related_person_relationship_type/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Related_person_relationship_typeVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/related_person_relationship_type/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Related_person_relationship_typeVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/related_person_relationship_type/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Retention_risk_factor_typeVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/retention_risk_factor_type/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Retention_risk_factor_typeVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/retention_risk_factor_type/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Retention_risk_factor_typeVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/retention_risk_factor_type/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Retention_risk_factor_typeVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/retention_risk_factor_type/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Severance_service_dateVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/severance_service_date/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Severance_service_dateVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/severance_service_date/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Severance_service_dateVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/severance_service_date/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Severance_service_dateVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/severance_service_date/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Social_network_meta_typeVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/social_network_meta_type/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Social_network_meta_typeVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/social_network_meta_type/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Social_network_meta_typeVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/social_network_meta_type/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Social_network_meta_typeVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/social_network_meta_type/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Taggable_typeVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/taggable_type/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Taggable_typeVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/taggable_type/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Taggable_typeVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/taggable_type/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Taggable_typeVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/taggable_type/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Tax_id_typeVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/tax_id_type/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Tax_id_typeVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/tax_id_type/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Tax_id_typeVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/tax_id_type/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Tax_id_typeVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/tax_id_type/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class User_languageVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/user_language/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class User_languageVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/user_language/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class User_languageVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/user_language/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class User_languageVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/user_language/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Worktag_aggregation_typeVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/worktag_aggregation_type/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Worktag_aggregation_typeVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/worktag_aggregation_type/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Worktag_aggregation_typeVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/worktag_aggregation_type/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Worktag_aggregation_typeVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/worktag_aggregation_type/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Worktag_typeVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/worktag_type/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Worktag_typeVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/worktag_type/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Worktag_typeVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/worktag_type/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Worktag_typeVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/worktag_type/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Worktag_usageVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/worktag_usage/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Worktag_usageVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/worktag_usage/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Worktag_usageVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/worktag_usage/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Worktag_usageVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/worktag_usage/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("ids"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Worktags_rest_validation_idVersLatestItems implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Parameter for batch get of items. Must take the format id1,id2,... where id1, id2, etc. are the ids of the items you wish to get.")
        private String ids;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/worktags_rest_validation_id/vers/latest/items";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.ids != null) {
                queryParam.put("ids", this.ids);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("cols") })
    public static class Worktags_rest_validation_idVersLatestItemsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of dataset item.")
        private String id;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/worktags_rest_validation_id/vers/latest/items/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Worktags_rest_validation_idVersLatestSearch implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/worktags_rest_validation_id/vers/latest/search";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("q"), @GridLayout.Row("pageLimit"), @GridLayout.Row("pageId"), @GridLayout.Row("cols") })
    public static class Worktags_rest_validation_idVersLatestSearchScored implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Query search string for searching dataset items.\n Specify search phrases using double quotes for multi-word phrases.\n All phrases are optional matches unless prefixed with + to make it required or - to make it excluded, i.e. quick +'brown fox' -red\n Prefix the phrase with the column name to match against a specific column, i.e. title:quick description:'brown fox'\n Use ? to wildcard match a single character or * to wildcard match 0 or more characters, i.e. qu?ck bro* fox\n Use parenthesis to group phrases, i.e. name:(quick 'brown fox')\n For regex, proximity, boosting, fuzziness, ranges, and special characters refer to: https://www.elastic.co/guide/en/elasticsearch/reference/2.3/query-dsl-query-string-query.html#query-string-syntax")
        private String q;

        @Option
        @Documentation("Parameter for search or batch get of items to limit the number of items that are returned. If pageLimit=0 or no pageLimit is specified, then the default limit is used. The maximum number for pageLimit is 100.")
        private Integer pageLimit = 0;

        @Option
        @Documentation("Parameter for batch get of items or search of items. For search, this is the offset to get the next batch of items. For get, this is the id to get the next batch of items.")
        private String pageId;

        @Option
        @Documentation("unkonwn doc in workday")
        private String cols;

        @Override
        public String getServiceToCall() {
            return "referenceData/worktags_rest_validation_id/vers/latest/search/scored";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.q != null) {
                queryParam.put("q", this.q);
            }
            if (this.pageLimit != null) {
                queryParam.put("pageLimit", this.pageLimit);
            }
            if (this.pageId != null) {
                queryParam.put("pageId", this.pageId);
            }
            if (this.cols != null) {
                queryParam.put("cols", this.cols);
            }
            return queryParam;
        }
    }

    public enum ReferenceDataSwaggerServiceChoice {
        Calendar_yearVersLatestItems,
        Calendar_yearVersLatestItemsId,
        Calendar_yearVersLatestSearch,
        Calendar_yearVersLatestSearchScored,
        Cip_codeVersLatestItems,
        Cip_codeVersLatestItemsId,
        Cip_codeVersLatestSearch,
        Cip_codeVersLatestSearchScored,
        Communication_usage_typeVersLatestItems,
        Communication_usage_typeVersLatestItemsId,
        Communication_usage_typeVersLatestSearch,
        Communication_usage_typeVersLatestSearchScored,
        CountryVersLatestItems,
        CountryVersLatestItemsId,
        CountryVersLatestSearch,
        CountryVersLatestSearchScored,
        Country_cityVersLatestItems,
        Country_cityVersLatestItemsId,
        Country_cityVersLatestSearch,
        Country_cityVersLatestSearchScored,
        Country_phone_codeVersLatestItems,
        Country_phone_codeVersLatestItemsId,
        Country_phone_codeVersLatestSearch,
        Country_phone_codeVersLatestSearchScored,
        Country_regionVersLatestItems,
        Country_regionVersLatestItemsId,
        Country_regionVersLatestSearch,
        Country_regionVersLatestSearchScored,
        Country_region_typeVersLatestItems,
        Country_region_typeVersLatestItemsId,
        Country_region_typeVersLatestSearch,
        Country_region_typeVersLatestSearchScored,
        CurrencyVersLatestItems,
        CurrencyVersLatestItemsId,
        CurrencyVersLatestSearch,
        CurrencyVersLatestSearchScored,
        Custom_org_worktag_dimensionVersLatestItems,
        Custom_org_worktag_dimensionVersLatestItemsId,
        Custom_org_worktag_dimensionVersLatestSearch,
        Custom_org_worktag_dimensionVersLatestSearchScored,
        Document_statusVersLatestItems,
        Document_statusVersLatestItemsId,
        Document_statusVersLatestSearch,
        Document_statusVersLatestSearchScored,
        Enrollment_statusVersLatestItems,
        Enrollment_statusVersLatestItemsId,
        Enrollment_statusVersLatestSearch,
        Enrollment_statusVersLatestSearchScored,
        Financial_aid_award_yearVersLatestItems,
        Financial_aid_award_yearVersLatestItemsId,
        Financial_aid_award_yearVersLatestSearch,
        Financial_aid_award_yearVersLatestSearchScored,
        Jp_addressesVersV10Items,
        Jp_addressesVersV10ItemsId,
        Jp_addressesVersV10Search,
        Jp_addressesVersV10SearchScored,
        National_id_typeVersLatestItems,
        National_id_typeVersLatestItemsId,
        National_id_typeVersLatestSearch,
        National_id_typeVersLatestSearchScored,
        Postal_codeVersLatestItems,
        Postal_codeVersLatestItemsId,
        Postal_codeVersLatestSearch,
        Postal_codeVersLatestSearchScored,
        Question_typeVersLatestItems,
        Question_typeVersLatestItemsId,
        Question_typeVersLatestSearch,
        Question_typeVersLatestSearchScored,
        Recruiting_sourceVersLatestItems,
        Recruiting_sourceVersLatestItemsId,
        Recruiting_sourceVersLatestSearch,
        Recruiting_sourceVersLatestSearchScored,
        Related_person_relationship_typeVersLatestItems,
        Related_person_relationship_typeVersLatestItemsId,
        Related_person_relationship_typeVersLatestSearch,
        Related_person_relationship_typeVersLatestSearchScored,
        Retention_risk_factor_typeVersLatestItems,
        Retention_risk_factor_typeVersLatestItemsId,
        Retention_risk_factor_typeVersLatestSearch,
        Retention_risk_factor_typeVersLatestSearchScored,
        Severance_service_dateVersLatestItems,
        Severance_service_dateVersLatestItemsId,
        Severance_service_dateVersLatestSearch,
        Severance_service_dateVersLatestSearchScored,
        Social_network_meta_typeVersLatestItems,
        Social_network_meta_typeVersLatestItemsId,
        Social_network_meta_typeVersLatestSearch,
        Social_network_meta_typeVersLatestSearchScored,
        Taggable_typeVersLatestItems,
        Taggable_typeVersLatestItemsId,
        Taggable_typeVersLatestSearch,
        Taggable_typeVersLatestSearchScored,
        Tax_id_typeVersLatestItems,
        Tax_id_typeVersLatestItemsId,
        Tax_id_typeVersLatestSearch,
        Tax_id_typeVersLatestSearchScored,
        User_languageVersLatestItems,
        User_languageVersLatestItemsId,
        User_languageVersLatestSearch,
        User_languageVersLatestSearchScored,
        Worktag_aggregation_typeVersLatestItems,
        Worktag_aggregation_typeVersLatestItemsId,
        Worktag_aggregation_typeVersLatestSearch,
        Worktag_aggregation_typeVersLatestSearchScored,
        Worktag_typeVersLatestItems,
        Worktag_typeVersLatestItemsId,
        Worktag_typeVersLatestSearch,
        Worktag_typeVersLatestSearchScored,
        Worktag_usageVersLatestItems,
        Worktag_usageVersLatestItemsId,
        Worktag_usageVersLatestSearch,
        Worktag_usageVersLatestSearchScored,
        Worktags_rest_validation_idVersLatestItems,
        Worktags_rest_validation_idVersLatestItemsId,
        Worktags_rest_validation_idVersLatestSearch,
        Worktags_rest_validation_idVersLatestSearchScored,
        aca_4980h_safe_harborVersLatestItems,
        aca_4980h_safe_harborVersLatestItemsId,
        aca_4980h_safe_harborVersLatestSearch,
        aca_4980h_safe_harborVersLatestSearchScored,
        admission_decisionVersLatestItems,
        admission_decisionVersLatestItemsId,
        admission_decisionVersLatestSearch,
        admission_decisionVersLatestSearchScored;
    }

    @Option
    @Documentation("selected service")
    private ReferenceDataSwaggerServiceChoice service = ReferenceDataSwaggerServiceChoice.Calendar_yearVersLatestItems;

    @Option
    @ActiveIf(target = "service", value = "Calendar_yearVersLatestItems")
    @Documentation("parameters")
    private Calendar_yearVersLatestItems calendar_yearVersLatestItemsParameters = new Calendar_yearVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Calendar_yearVersLatestItemsId")
    @Documentation("parameters")
    private Calendar_yearVersLatestItemsId calendar_yearVersLatestItemsIdParameters = new Calendar_yearVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Calendar_yearVersLatestSearch")
    @Documentation("parameters")
    private Calendar_yearVersLatestSearch calendar_yearVersLatestSearchParameters = new Calendar_yearVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Calendar_yearVersLatestSearchScored")
    @Documentation("parameters")
    private Calendar_yearVersLatestSearchScored calendar_yearVersLatestSearchScoredParameters = new Calendar_yearVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Cip_codeVersLatestItems")
    @Documentation("parameters")
    private Cip_codeVersLatestItems cip_codeVersLatestItemsParameters = new Cip_codeVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Cip_codeVersLatestItemsId")
    @Documentation("parameters")
    private Cip_codeVersLatestItemsId cip_codeVersLatestItemsIdParameters = new Cip_codeVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Cip_codeVersLatestSearch")
    @Documentation("parameters")
    private Cip_codeVersLatestSearch cip_codeVersLatestSearchParameters = new Cip_codeVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Cip_codeVersLatestSearchScored")
    @Documentation("parameters")
    private Cip_codeVersLatestSearchScored cip_codeVersLatestSearchScoredParameters = new Cip_codeVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Communication_usage_typeVersLatestItems")
    @Documentation("parameters")
    private Communication_usage_typeVersLatestItems communication_usage_typeVersLatestItemsParameters = new Communication_usage_typeVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Communication_usage_typeVersLatestItemsId")
    @Documentation("parameters")
    private Communication_usage_typeVersLatestItemsId communication_usage_typeVersLatestItemsIdParameters = new Communication_usage_typeVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Communication_usage_typeVersLatestSearch")
    @Documentation("parameters")
    private Communication_usage_typeVersLatestSearch communication_usage_typeVersLatestSearchParameters = new Communication_usage_typeVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Communication_usage_typeVersLatestSearchScored")
    @Documentation("parameters")
    private Communication_usage_typeVersLatestSearchScored communication_usage_typeVersLatestSearchScoredParameters = new Communication_usage_typeVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "CountryVersLatestItems")
    @Documentation("parameters")
    private CountryVersLatestItems countryVersLatestItemsParameters = new CountryVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "CountryVersLatestItemsId")
    @Documentation("parameters")
    private CountryVersLatestItemsId countryVersLatestItemsIdParameters = new CountryVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "CountryVersLatestSearch")
    @Documentation("parameters")
    private CountryVersLatestSearch countryVersLatestSearchParameters = new CountryVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "CountryVersLatestSearchScored")
    @Documentation("parameters")
    private CountryVersLatestSearchScored countryVersLatestSearchScoredParameters = new CountryVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Country_cityVersLatestItems")
    @Documentation("parameters")
    private Country_cityVersLatestItems country_cityVersLatestItemsParameters = new Country_cityVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Country_cityVersLatestItemsId")
    @Documentation("parameters")
    private Country_cityVersLatestItemsId country_cityVersLatestItemsIdParameters = new Country_cityVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Country_cityVersLatestSearch")
    @Documentation("parameters")
    private Country_cityVersLatestSearch country_cityVersLatestSearchParameters = new Country_cityVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Country_cityVersLatestSearchScored")
    @Documentation("parameters")
    private Country_cityVersLatestSearchScored country_cityVersLatestSearchScoredParameters = new Country_cityVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Country_phone_codeVersLatestItems")
    @Documentation("parameters")
    private Country_phone_codeVersLatestItems country_phone_codeVersLatestItemsParameters = new Country_phone_codeVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Country_phone_codeVersLatestItemsId")
    @Documentation("parameters")
    private Country_phone_codeVersLatestItemsId country_phone_codeVersLatestItemsIdParameters = new Country_phone_codeVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Country_phone_codeVersLatestSearch")
    @Documentation("parameters")
    private Country_phone_codeVersLatestSearch country_phone_codeVersLatestSearchParameters = new Country_phone_codeVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Country_phone_codeVersLatestSearchScored")
    @Documentation("parameters")
    private Country_phone_codeVersLatestSearchScored country_phone_codeVersLatestSearchScoredParameters = new Country_phone_codeVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Country_regionVersLatestItems")
    @Documentation("parameters")
    private Country_regionVersLatestItems country_regionVersLatestItemsParameters = new Country_regionVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Country_regionVersLatestItemsId")
    @Documentation("parameters")
    private Country_regionVersLatestItemsId country_regionVersLatestItemsIdParameters = new Country_regionVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Country_regionVersLatestSearch")
    @Documentation("parameters")
    private Country_regionVersLatestSearch country_regionVersLatestSearchParameters = new Country_regionVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Country_regionVersLatestSearchScored")
    @Documentation("parameters")
    private Country_regionVersLatestSearchScored country_regionVersLatestSearchScoredParameters = new Country_regionVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Country_region_typeVersLatestItems")
    @Documentation("parameters")
    private Country_region_typeVersLatestItems country_region_typeVersLatestItemsParameters = new Country_region_typeVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Country_region_typeVersLatestItemsId")
    @Documentation("parameters")
    private Country_region_typeVersLatestItemsId country_region_typeVersLatestItemsIdParameters = new Country_region_typeVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Country_region_typeVersLatestSearch")
    @Documentation("parameters")
    private Country_region_typeVersLatestSearch country_region_typeVersLatestSearchParameters = new Country_region_typeVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Country_region_typeVersLatestSearchScored")
    @Documentation("parameters")
    private Country_region_typeVersLatestSearchScored country_region_typeVersLatestSearchScoredParameters = new Country_region_typeVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "CurrencyVersLatestItems")
    @Documentation("parameters")
    private CurrencyVersLatestItems currencyVersLatestItemsParameters = new CurrencyVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "CurrencyVersLatestItemsId")
    @Documentation("parameters")
    private CurrencyVersLatestItemsId currencyVersLatestItemsIdParameters = new CurrencyVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "CurrencyVersLatestSearch")
    @Documentation("parameters")
    private CurrencyVersLatestSearch currencyVersLatestSearchParameters = new CurrencyVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "CurrencyVersLatestSearchScored")
    @Documentation("parameters")
    private CurrencyVersLatestSearchScored currencyVersLatestSearchScoredParameters = new CurrencyVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Custom_org_worktag_dimensionVersLatestItems")
    @Documentation("parameters")
    private Custom_org_worktag_dimensionVersLatestItems custom_org_worktag_dimensionVersLatestItemsParameters = new Custom_org_worktag_dimensionVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Custom_org_worktag_dimensionVersLatestItemsId")
    @Documentation("parameters")
    private Custom_org_worktag_dimensionVersLatestItemsId custom_org_worktag_dimensionVersLatestItemsIdParameters = new Custom_org_worktag_dimensionVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Custom_org_worktag_dimensionVersLatestSearch")
    @Documentation("parameters")
    private Custom_org_worktag_dimensionVersLatestSearch custom_org_worktag_dimensionVersLatestSearchParameters = new Custom_org_worktag_dimensionVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Custom_org_worktag_dimensionVersLatestSearchScored")
    @Documentation("parameters")
    private Custom_org_worktag_dimensionVersLatestSearchScored custom_org_worktag_dimensionVersLatestSearchScoredParameters = new Custom_org_worktag_dimensionVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Document_statusVersLatestItems")
    @Documentation("parameters")
    private Document_statusVersLatestItems document_statusVersLatestItemsParameters = new Document_statusVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Document_statusVersLatestItemsId")
    @Documentation("parameters")
    private Document_statusVersLatestItemsId document_statusVersLatestItemsIdParameters = new Document_statusVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Document_statusVersLatestSearch")
    @Documentation("parameters")
    private Document_statusVersLatestSearch document_statusVersLatestSearchParameters = new Document_statusVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Document_statusVersLatestSearchScored")
    @Documentation("parameters")
    private Document_statusVersLatestSearchScored document_statusVersLatestSearchScoredParameters = new Document_statusVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Enrollment_statusVersLatestItems")
    @Documentation("parameters")
    private Enrollment_statusVersLatestItems enrollment_statusVersLatestItemsParameters = new Enrollment_statusVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Enrollment_statusVersLatestItemsId")
    @Documentation("parameters")
    private Enrollment_statusVersLatestItemsId enrollment_statusVersLatestItemsIdParameters = new Enrollment_statusVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Enrollment_statusVersLatestSearch")
    @Documentation("parameters")
    private Enrollment_statusVersLatestSearch enrollment_statusVersLatestSearchParameters = new Enrollment_statusVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Enrollment_statusVersLatestSearchScored")
    @Documentation("parameters")
    private Enrollment_statusVersLatestSearchScored enrollment_statusVersLatestSearchScoredParameters = new Enrollment_statusVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Financial_aid_award_yearVersLatestItems")
    @Documentation("parameters")
    private Financial_aid_award_yearVersLatestItems financial_aid_award_yearVersLatestItemsParameters = new Financial_aid_award_yearVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Financial_aid_award_yearVersLatestItemsId")
    @Documentation("parameters")
    private Financial_aid_award_yearVersLatestItemsId financial_aid_award_yearVersLatestItemsIdParameters = new Financial_aid_award_yearVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Financial_aid_award_yearVersLatestSearch")
    @Documentation("parameters")
    private Financial_aid_award_yearVersLatestSearch financial_aid_award_yearVersLatestSearchParameters = new Financial_aid_award_yearVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Financial_aid_award_yearVersLatestSearchScored")
    @Documentation("parameters")
    private Financial_aid_award_yearVersLatestSearchScored financial_aid_award_yearVersLatestSearchScoredParameters = new Financial_aid_award_yearVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Jp_addressesVersV10Items")
    @Documentation("parameters")
    private Jp_addressesVersV10Items jp_addressesVersV10ItemsParameters = new Jp_addressesVersV10Items();

    @Option
    @ActiveIf(target = "service", value = "Jp_addressesVersV10ItemsId")
    @Documentation("parameters")
    private Jp_addressesVersV10ItemsId jp_addressesVersV10ItemsIdParameters = new Jp_addressesVersV10ItemsId();

    @Option
    @ActiveIf(target = "service", value = "Jp_addressesVersV10Search")
    @Documentation("parameters")
    private Jp_addressesVersV10Search jp_addressesVersV10SearchParameters = new Jp_addressesVersV10Search();

    @Option
    @ActiveIf(target = "service", value = "Jp_addressesVersV10SearchScored")
    @Documentation("parameters")
    private Jp_addressesVersV10SearchScored jp_addressesVersV10SearchScoredParameters = new Jp_addressesVersV10SearchScored();

    @Option
    @ActiveIf(target = "service", value = "National_id_typeVersLatestItems")
    @Documentation("parameters")
    private National_id_typeVersLatestItems national_id_typeVersLatestItemsParameters = new National_id_typeVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "National_id_typeVersLatestItemsId")
    @Documentation("parameters")
    private National_id_typeVersLatestItemsId national_id_typeVersLatestItemsIdParameters = new National_id_typeVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "National_id_typeVersLatestSearch")
    @Documentation("parameters")
    private National_id_typeVersLatestSearch national_id_typeVersLatestSearchParameters = new National_id_typeVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "National_id_typeVersLatestSearchScored")
    @Documentation("parameters")
    private National_id_typeVersLatestSearchScored national_id_typeVersLatestSearchScoredParameters = new National_id_typeVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Postal_codeVersLatestItems")
    @Documentation("parameters")
    private Postal_codeVersLatestItems postal_codeVersLatestItemsParameters = new Postal_codeVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Postal_codeVersLatestItemsId")
    @Documentation("parameters")
    private Postal_codeVersLatestItemsId postal_codeVersLatestItemsIdParameters = new Postal_codeVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Postal_codeVersLatestSearch")
    @Documentation("parameters")
    private Postal_codeVersLatestSearch postal_codeVersLatestSearchParameters = new Postal_codeVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Postal_codeVersLatestSearchScored")
    @Documentation("parameters")
    private Postal_codeVersLatestSearchScored postal_codeVersLatestSearchScoredParameters = new Postal_codeVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Question_typeVersLatestItems")
    @Documentation("parameters")
    private Question_typeVersLatestItems question_typeVersLatestItemsParameters = new Question_typeVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Question_typeVersLatestItemsId")
    @Documentation("parameters")
    private Question_typeVersLatestItemsId question_typeVersLatestItemsIdParameters = new Question_typeVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Question_typeVersLatestSearch")
    @Documentation("parameters")
    private Question_typeVersLatestSearch question_typeVersLatestSearchParameters = new Question_typeVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Question_typeVersLatestSearchScored")
    @Documentation("parameters")
    private Question_typeVersLatestSearchScored question_typeVersLatestSearchScoredParameters = new Question_typeVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Recruiting_sourceVersLatestItems")
    @Documentation("parameters")
    private Recruiting_sourceVersLatestItems recruiting_sourceVersLatestItemsParameters = new Recruiting_sourceVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Recruiting_sourceVersLatestItemsId")
    @Documentation("parameters")
    private Recruiting_sourceVersLatestItemsId recruiting_sourceVersLatestItemsIdParameters = new Recruiting_sourceVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Recruiting_sourceVersLatestSearch")
    @Documentation("parameters")
    private Recruiting_sourceVersLatestSearch recruiting_sourceVersLatestSearchParameters = new Recruiting_sourceVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Recruiting_sourceVersLatestSearchScored")
    @Documentation("parameters")
    private Recruiting_sourceVersLatestSearchScored recruiting_sourceVersLatestSearchScoredParameters = new Recruiting_sourceVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Related_person_relationship_typeVersLatestItems")
    @Documentation("parameters")
    private Related_person_relationship_typeVersLatestItems related_person_relationship_typeVersLatestItemsParameters = new Related_person_relationship_typeVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Related_person_relationship_typeVersLatestItemsId")
    @Documentation("parameters")
    private Related_person_relationship_typeVersLatestItemsId related_person_relationship_typeVersLatestItemsIdParameters = new Related_person_relationship_typeVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Related_person_relationship_typeVersLatestSearch")
    @Documentation("parameters")
    private Related_person_relationship_typeVersLatestSearch related_person_relationship_typeVersLatestSearchParameters = new Related_person_relationship_typeVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Related_person_relationship_typeVersLatestSearchScored")
    @Documentation("parameters")
    private Related_person_relationship_typeVersLatestSearchScored related_person_relationship_typeVersLatestSearchScoredParameters = new Related_person_relationship_typeVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Retention_risk_factor_typeVersLatestItems")
    @Documentation("parameters")
    private Retention_risk_factor_typeVersLatestItems retention_risk_factor_typeVersLatestItemsParameters = new Retention_risk_factor_typeVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Retention_risk_factor_typeVersLatestItemsId")
    @Documentation("parameters")
    private Retention_risk_factor_typeVersLatestItemsId retention_risk_factor_typeVersLatestItemsIdParameters = new Retention_risk_factor_typeVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Retention_risk_factor_typeVersLatestSearch")
    @Documentation("parameters")
    private Retention_risk_factor_typeVersLatestSearch retention_risk_factor_typeVersLatestSearchParameters = new Retention_risk_factor_typeVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Retention_risk_factor_typeVersLatestSearchScored")
    @Documentation("parameters")
    private Retention_risk_factor_typeVersLatestSearchScored retention_risk_factor_typeVersLatestSearchScoredParameters = new Retention_risk_factor_typeVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Severance_service_dateVersLatestItems")
    @Documentation("parameters")
    private Severance_service_dateVersLatestItems severance_service_dateVersLatestItemsParameters = new Severance_service_dateVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Severance_service_dateVersLatestItemsId")
    @Documentation("parameters")
    private Severance_service_dateVersLatestItemsId severance_service_dateVersLatestItemsIdParameters = new Severance_service_dateVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Severance_service_dateVersLatestSearch")
    @Documentation("parameters")
    private Severance_service_dateVersLatestSearch severance_service_dateVersLatestSearchParameters = new Severance_service_dateVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Severance_service_dateVersLatestSearchScored")
    @Documentation("parameters")
    private Severance_service_dateVersLatestSearchScored severance_service_dateVersLatestSearchScoredParameters = new Severance_service_dateVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Social_network_meta_typeVersLatestItems")
    @Documentation("parameters")
    private Social_network_meta_typeVersLatestItems social_network_meta_typeVersLatestItemsParameters = new Social_network_meta_typeVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Social_network_meta_typeVersLatestItemsId")
    @Documentation("parameters")
    private Social_network_meta_typeVersLatestItemsId social_network_meta_typeVersLatestItemsIdParameters = new Social_network_meta_typeVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Social_network_meta_typeVersLatestSearch")
    @Documentation("parameters")
    private Social_network_meta_typeVersLatestSearch social_network_meta_typeVersLatestSearchParameters = new Social_network_meta_typeVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Social_network_meta_typeVersLatestSearchScored")
    @Documentation("parameters")
    private Social_network_meta_typeVersLatestSearchScored social_network_meta_typeVersLatestSearchScoredParameters = new Social_network_meta_typeVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Taggable_typeVersLatestItems")
    @Documentation("parameters")
    private Taggable_typeVersLatestItems taggable_typeVersLatestItemsParameters = new Taggable_typeVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Taggable_typeVersLatestItemsId")
    @Documentation("parameters")
    private Taggable_typeVersLatestItemsId taggable_typeVersLatestItemsIdParameters = new Taggable_typeVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Taggable_typeVersLatestSearch")
    @Documentation("parameters")
    private Taggable_typeVersLatestSearch taggable_typeVersLatestSearchParameters = new Taggable_typeVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Taggable_typeVersLatestSearchScored")
    @Documentation("parameters")
    private Taggable_typeVersLatestSearchScored taggable_typeVersLatestSearchScoredParameters = new Taggable_typeVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Tax_id_typeVersLatestItems")
    @Documentation("parameters")
    private Tax_id_typeVersLatestItems tax_id_typeVersLatestItemsParameters = new Tax_id_typeVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Tax_id_typeVersLatestItemsId")
    @Documentation("parameters")
    private Tax_id_typeVersLatestItemsId tax_id_typeVersLatestItemsIdParameters = new Tax_id_typeVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Tax_id_typeVersLatestSearch")
    @Documentation("parameters")
    private Tax_id_typeVersLatestSearch tax_id_typeVersLatestSearchParameters = new Tax_id_typeVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Tax_id_typeVersLatestSearchScored")
    @Documentation("parameters")
    private Tax_id_typeVersLatestSearchScored tax_id_typeVersLatestSearchScoredParameters = new Tax_id_typeVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "User_languageVersLatestItems")
    @Documentation("parameters")
    private User_languageVersLatestItems user_languageVersLatestItemsParameters = new User_languageVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "User_languageVersLatestItemsId")
    @Documentation("parameters")
    private User_languageVersLatestItemsId user_languageVersLatestItemsIdParameters = new User_languageVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "User_languageVersLatestSearch")
    @Documentation("parameters")
    private User_languageVersLatestSearch user_languageVersLatestSearchParameters = new User_languageVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "User_languageVersLatestSearchScored")
    @Documentation("parameters")
    private User_languageVersLatestSearchScored user_languageVersLatestSearchScoredParameters = new User_languageVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Worktag_aggregation_typeVersLatestItems")
    @Documentation("parameters")
    private Worktag_aggregation_typeVersLatestItems worktag_aggregation_typeVersLatestItemsParameters = new Worktag_aggregation_typeVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Worktag_aggregation_typeVersLatestItemsId")
    @Documentation("parameters")
    private Worktag_aggregation_typeVersLatestItemsId worktag_aggregation_typeVersLatestItemsIdParameters = new Worktag_aggregation_typeVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Worktag_aggregation_typeVersLatestSearch")
    @Documentation("parameters")
    private Worktag_aggregation_typeVersLatestSearch worktag_aggregation_typeVersLatestSearchParameters = new Worktag_aggregation_typeVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Worktag_aggregation_typeVersLatestSearchScored")
    @Documentation("parameters")
    private Worktag_aggregation_typeVersLatestSearchScored worktag_aggregation_typeVersLatestSearchScoredParameters = new Worktag_aggregation_typeVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Worktag_typeVersLatestItems")
    @Documentation("parameters")
    private Worktag_typeVersLatestItems worktag_typeVersLatestItemsParameters = new Worktag_typeVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Worktag_typeVersLatestItemsId")
    @Documentation("parameters")
    private Worktag_typeVersLatestItemsId worktag_typeVersLatestItemsIdParameters = new Worktag_typeVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Worktag_typeVersLatestSearch")
    @Documentation("parameters")
    private Worktag_typeVersLatestSearch worktag_typeVersLatestSearchParameters = new Worktag_typeVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Worktag_typeVersLatestSearchScored")
    @Documentation("parameters")
    private Worktag_typeVersLatestSearchScored worktag_typeVersLatestSearchScoredParameters = new Worktag_typeVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Worktag_usageVersLatestItems")
    @Documentation("parameters")
    private Worktag_usageVersLatestItems worktag_usageVersLatestItemsParameters = new Worktag_usageVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Worktag_usageVersLatestItemsId")
    @Documentation("parameters")
    private Worktag_usageVersLatestItemsId worktag_usageVersLatestItemsIdParameters = new Worktag_usageVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Worktag_usageVersLatestSearch")
    @Documentation("parameters")
    private Worktag_usageVersLatestSearch worktag_usageVersLatestSearchParameters = new Worktag_usageVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Worktag_usageVersLatestSearchScored")
    @Documentation("parameters")
    private Worktag_usageVersLatestSearchScored worktag_usageVersLatestSearchScoredParameters = new Worktag_usageVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "Worktags_rest_validation_idVersLatestItems")
    @Documentation("parameters")
    private Worktags_rest_validation_idVersLatestItems worktags_rest_validation_idVersLatestItemsParameters = new Worktags_rest_validation_idVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "Worktags_rest_validation_idVersLatestItemsId")
    @Documentation("parameters")
    private Worktags_rest_validation_idVersLatestItemsId worktags_rest_validation_idVersLatestItemsIdParameters = new Worktags_rest_validation_idVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "Worktags_rest_validation_idVersLatestSearch")
    @Documentation("parameters")
    private Worktags_rest_validation_idVersLatestSearch worktags_rest_validation_idVersLatestSearchParameters = new Worktags_rest_validation_idVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "Worktags_rest_validation_idVersLatestSearchScored")
    @Documentation("parameters")
    private Worktags_rest_validation_idVersLatestSearchScored worktags_rest_validation_idVersLatestSearchScoredParameters = new Worktags_rest_validation_idVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "aca_4980h_safe_harborVersLatestItems")
    @Documentation("parameters")
    private aca_4980h_safe_harborVersLatestItems aca_4980h_safe_harborVersLatestItemsParameters = new aca_4980h_safe_harborVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "aca_4980h_safe_harborVersLatestItemsId")
    @Documentation("parameters")
    private aca_4980h_safe_harborVersLatestItemsId aca_4980h_safe_harborVersLatestItemsIdParameters = new aca_4980h_safe_harborVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "aca_4980h_safe_harborVersLatestSearch")
    @Documentation("parameters")
    private aca_4980h_safe_harborVersLatestSearch aca_4980h_safe_harborVersLatestSearchParameters = new aca_4980h_safe_harborVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "aca_4980h_safe_harborVersLatestSearchScored")
    @Documentation("parameters")
    private aca_4980h_safe_harborVersLatestSearchScored aca_4980h_safe_harborVersLatestSearchScoredParameters = new aca_4980h_safe_harborVersLatestSearchScored();

    @Option
    @ActiveIf(target = "service", value = "admission_decisionVersLatestItems")
    @Documentation("parameters")
    private admission_decisionVersLatestItems admission_decisionVersLatestItemsParameters = new admission_decisionVersLatestItems();

    @Option
    @ActiveIf(target = "service", value = "admission_decisionVersLatestItemsId")
    @Documentation("parameters")
    private admission_decisionVersLatestItemsId admission_decisionVersLatestItemsIdParameters = new admission_decisionVersLatestItemsId();

    @Option
    @ActiveIf(target = "service", value = "admission_decisionVersLatestSearch")
    @Documentation("parameters")
    private admission_decisionVersLatestSearch admission_decisionVersLatestSearchParameters = new admission_decisionVersLatestSearch();

    @Option
    @ActiveIf(target = "service", value = "admission_decisionVersLatestSearchScored")
    @Documentation("parameters")
    private admission_decisionVersLatestSearchScored admission_decisionVersLatestSearchScoredParameters = new admission_decisionVersLatestSearchScored();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == ReferenceDataSwaggerServiceChoice.Calendar_yearVersLatestItems) {
            return this.calendar_yearVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Calendar_yearVersLatestItemsId) {
            return this.calendar_yearVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Calendar_yearVersLatestSearch) {
            return this.calendar_yearVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Calendar_yearVersLatestSearchScored) {
            return this.calendar_yearVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Cip_codeVersLatestItems) {
            return this.cip_codeVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Cip_codeVersLatestItemsId) {
            return this.cip_codeVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Cip_codeVersLatestSearch) {
            return this.cip_codeVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Cip_codeVersLatestSearchScored) {
            return this.cip_codeVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Communication_usage_typeVersLatestItems) {
            return this.communication_usage_typeVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Communication_usage_typeVersLatestItemsId) {
            return this.communication_usage_typeVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Communication_usage_typeVersLatestSearch) {
            return this.communication_usage_typeVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Communication_usage_typeVersLatestSearchScored) {
            return this.communication_usage_typeVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.CountryVersLatestItems) {
            return this.countryVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.CountryVersLatestItemsId) {
            return this.countryVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.CountryVersLatestSearch) {
            return this.countryVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.CountryVersLatestSearchScored) {
            return this.countryVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Country_cityVersLatestItems) {
            return this.country_cityVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Country_cityVersLatestItemsId) {
            return this.country_cityVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Country_cityVersLatestSearch) {
            return this.country_cityVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Country_cityVersLatestSearchScored) {
            return this.country_cityVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Country_phone_codeVersLatestItems) {
            return this.country_phone_codeVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Country_phone_codeVersLatestItemsId) {
            return this.country_phone_codeVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Country_phone_codeVersLatestSearch) {
            return this.country_phone_codeVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Country_phone_codeVersLatestSearchScored) {
            return this.country_phone_codeVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Country_regionVersLatestItems) {
            return this.country_regionVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Country_regionVersLatestItemsId) {
            return this.country_regionVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Country_regionVersLatestSearch) {
            return this.country_regionVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Country_regionVersLatestSearchScored) {
            return this.country_regionVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Country_region_typeVersLatestItems) {
            return this.country_region_typeVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Country_region_typeVersLatestItemsId) {
            return this.country_region_typeVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Country_region_typeVersLatestSearch) {
            return this.country_region_typeVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Country_region_typeVersLatestSearchScored) {
            return this.country_region_typeVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.CurrencyVersLatestItems) {
            return this.currencyVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.CurrencyVersLatestItemsId) {
            return this.currencyVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.CurrencyVersLatestSearch) {
            return this.currencyVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.CurrencyVersLatestSearchScored) {
            return this.currencyVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Custom_org_worktag_dimensionVersLatestItems) {
            return this.custom_org_worktag_dimensionVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Custom_org_worktag_dimensionVersLatestItemsId) {
            return this.custom_org_worktag_dimensionVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Custom_org_worktag_dimensionVersLatestSearch) {
            return this.custom_org_worktag_dimensionVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Custom_org_worktag_dimensionVersLatestSearchScored) {
            return this.custom_org_worktag_dimensionVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Document_statusVersLatestItems) {
            return this.document_statusVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Document_statusVersLatestItemsId) {
            return this.document_statusVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Document_statusVersLatestSearch) {
            return this.document_statusVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Document_statusVersLatestSearchScored) {
            return this.document_statusVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Enrollment_statusVersLatestItems) {
            return this.enrollment_statusVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Enrollment_statusVersLatestItemsId) {
            return this.enrollment_statusVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Enrollment_statusVersLatestSearch) {
            return this.enrollment_statusVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Enrollment_statusVersLatestSearchScored) {
            return this.enrollment_statusVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Financial_aid_award_yearVersLatestItems) {
            return this.financial_aid_award_yearVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Financial_aid_award_yearVersLatestItemsId) {
            return this.financial_aid_award_yearVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Financial_aid_award_yearVersLatestSearch) {
            return this.financial_aid_award_yearVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Financial_aid_award_yearVersLatestSearchScored) {
            return this.financial_aid_award_yearVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Jp_addressesVersV10Items) {
            return this.jp_addressesVersV10ItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Jp_addressesVersV10ItemsId) {
            return this.jp_addressesVersV10ItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Jp_addressesVersV10Search) {
            return this.jp_addressesVersV10SearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Jp_addressesVersV10SearchScored) {
            return this.jp_addressesVersV10SearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.National_id_typeVersLatestItems) {
            return this.national_id_typeVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.National_id_typeVersLatestItemsId) {
            return this.national_id_typeVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.National_id_typeVersLatestSearch) {
            return this.national_id_typeVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.National_id_typeVersLatestSearchScored) {
            return this.national_id_typeVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Postal_codeVersLatestItems) {
            return this.postal_codeVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Postal_codeVersLatestItemsId) {
            return this.postal_codeVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Postal_codeVersLatestSearch) {
            return this.postal_codeVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Postal_codeVersLatestSearchScored) {
            return this.postal_codeVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Question_typeVersLatestItems) {
            return this.question_typeVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Question_typeVersLatestItemsId) {
            return this.question_typeVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Question_typeVersLatestSearch) {
            return this.question_typeVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Question_typeVersLatestSearchScored) {
            return this.question_typeVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Recruiting_sourceVersLatestItems) {
            return this.recruiting_sourceVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Recruiting_sourceVersLatestItemsId) {
            return this.recruiting_sourceVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Recruiting_sourceVersLatestSearch) {
            return this.recruiting_sourceVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Recruiting_sourceVersLatestSearchScored) {
            return this.recruiting_sourceVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Related_person_relationship_typeVersLatestItems) {
            return this.related_person_relationship_typeVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Related_person_relationship_typeVersLatestItemsId) {
            return this.related_person_relationship_typeVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Related_person_relationship_typeVersLatestSearch) {
            return this.related_person_relationship_typeVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Related_person_relationship_typeVersLatestSearchScored) {
            return this.related_person_relationship_typeVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Retention_risk_factor_typeVersLatestItems) {
            return this.retention_risk_factor_typeVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Retention_risk_factor_typeVersLatestItemsId) {
            return this.retention_risk_factor_typeVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Retention_risk_factor_typeVersLatestSearch) {
            return this.retention_risk_factor_typeVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Retention_risk_factor_typeVersLatestSearchScored) {
            return this.retention_risk_factor_typeVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Severance_service_dateVersLatestItems) {
            return this.severance_service_dateVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Severance_service_dateVersLatestItemsId) {
            return this.severance_service_dateVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Severance_service_dateVersLatestSearch) {
            return this.severance_service_dateVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Severance_service_dateVersLatestSearchScored) {
            return this.severance_service_dateVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Social_network_meta_typeVersLatestItems) {
            return this.social_network_meta_typeVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Social_network_meta_typeVersLatestItemsId) {
            return this.social_network_meta_typeVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Social_network_meta_typeVersLatestSearch) {
            return this.social_network_meta_typeVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Social_network_meta_typeVersLatestSearchScored) {
            return this.social_network_meta_typeVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Taggable_typeVersLatestItems) {
            return this.taggable_typeVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Taggable_typeVersLatestItemsId) {
            return this.taggable_typeVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Taggable_typeVersLatestSearch) {
            return this.taggable_typeVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Taggable_typeVersLatestSearchScored) {
            return this.taggable_typeVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Tax_id_typeVersLatestItems) {
            return this.tax_id_typeVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Tax_id_typeVersLatestItemsId) {
            return this.tax_id_typeVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Tax_id_typeVersLatestSearch) {
            return this.tax_id_typeVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Tax_id_typeVersLatestSearchScored) {
            return this.tax_id_typeVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.User_languageVersLatestItems) {
            return this.user_languageVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.User_languageVersLatestItemsId) {
            return this.user_languageVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.User_languageVersLatestSearch) {
            return this.user_languageVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.User_languageVersLatestSearchScored) {
            return this.user_languageVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Worktag_aggregation_typeVersLatestItems) {
            return this.worktag_aggregation_typeVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Worktag_aggregation_typeVersLatestItemsId) {
            return this.worktag_aggregation_typeVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Worktag_aggregation_typeVersLatestSearch) {
            return this.worktag_aggregation_typeVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Worktag_aggregation_typeVersLatestSearchScored) {
            return this.worktag_aggregation_typeVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Worktag_typeVersLatestItems) {
            return this.worktag_typeVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Worktag_typeVersLatestItemsId) {
            return this.worktag_typeVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Worktag_typeVersLatestSearch) {
            return this.worktag_typeVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Worktag_typeVersLatestSearchScored) {
            return this.worktag_typeVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Worktag_usageVersLatestItems) {
            return this.worktag_usageVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Worktag_usageVersLatestItemsId) {
            return this.worktag_usageVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Worktag_usageVersLatestSearch) {
            return this.worktag_usageVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Worktag_usageVersLatestSearchScored) {
            return this.worktag_usageVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Worktags_rest_validation_idVersLatestItems) {
            return this.worktags_rest_validation_idVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Worktags_rest_validation_idVersLatestItemsId) {
            return this.worktags_rest_validation_idVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Worktags_rest_validation_idVersLatestSearch) {
            return this.worktags_rest_validation_idVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.Worktags_rest_validation_idVersLatestSearchScored) {
            return this.worktags_rest_validation_idVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.aca_4980h_safe_harborVersLatestItems) {
            return this.aca_4980h_safe_harborVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.aca_4980h_safe_harborVersLatestItemsId) {
            return this.aca_4980h_safe_harborVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.aca_4980h_safe_harborVersLatestSearch) {
            return this.aca_4980h_safe_harborVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.aca_4980h_safe_harborVersLatestSearchScored) {
            return this.aca_4980h_safe_harborVersLatestSearchScoredParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.admission_decisionVersLatestItems) {
            return this.admission_decisionVersLatestItemsParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.admission_decisionVersLatestItemsId) {
            return this.admission_decisionVersLatestItemsIdParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.admission_decisionVersLatestSearch) {
            return this.admission_decisionVersLatestSearchParameters;
        }
        if (this.service == ReferenceDataSwaggerServiceChoice.admission_decisionVersLatestSearchScored) {
            return this.admission_decisionVersLatestSearchScoredParameters;
        }

        return null;
    }
}
