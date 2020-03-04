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
package org.talend.components.workday.input.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue.ValueType;

import org.talend.components.common.input.PageIterator;

import lombok.extern.slf4j.Slf4j;

/**
 * Help to retrieve data from workday with and without pagination.
 */
@Slf4j
public class ResultGetter {

    private static final int limit = 100;

    /**
     * Build workday data retriever.
     * 
     * @param finder : workday data find function from parameters (Map<String, Object>).
     * @param queryParamGetter : function to get parameter for call.
     * @param pageable : true if 'finder' represent a paginable workday function.
     * @return retriver instance.
     */
    public static Retriever buildRetriever(final Function<Map<String, Object>, JsonObject> finder,
            final Supplier<Map<String, Object>> queryParamGetter, boolean pageable) {
        if (pageable) {
            return new PageRetriever(finder, queryParamGetter);
        }
        return new SimpleRetriever(finder, queryParamGetter);
    }

    /**
     * Base class for workday service data getter.
     */
    public static abstract class Retriever {

        /** workday data find function from parameters (Map<String, Object>). */
        private final Function<Map<String, Object>, JsonObject> finder;

        /** function to get parameter for call. */
        private final Supplier<Map<String, Object>> queryParamGetter;

        public Retriever(Function<Map<String, Object>, JsonObject> finder, Supplier<Map<String, Object>> queryParamGetter) {
            this.finder = finder;
            this.queryParamGetter = queryParamGetter;
        }

        /**
         * abstract function that return a function that iterate on each workday record.
         * 
         * @return function iterating on workday record.
         */
        public abstract Supplier<JsonObject> getResultRetriever();

        protected Map<String, Object> getQueryParams(Object param) {
            return this.queryParamGetter.get();
        }

        protected JsonObject retrieveJsonObject(Object param) {
            final Map<String, Object> queryParams = getQueryParams(param);
            return this.finder.apply(queryParams);
        }

        /**
         * Extract data from workday result.
         * 
         * @param jsonData : workday json service result (format : '"{"total":5, "data":[{....},{...} ...]')
         * @return iterator on each sub object of data array.
         */
        protected final Iterator<JsonObject> extractContentIterator(JsonObject jsonData) {
            if (jsonData == null) {
                return Collections.emptyIterator();
            }
            if (jsonData.containsKey("data") && jsonData.get("data").getValueType() == ValueType.ARRAY) {
                final JsonArray data = jsonData.getJsonArray("data");
                return data.stream().map(JsonObject.class::cast).iterator();
            }
            return Collections.singletonList(jsonData).iterator();
        }
    }

    /**
     * Concrete class of workday data retriever in case of pagination.
     */
    public static class PageRetriever extends Retriever {

        private int total = -1;

        public PageRetriever(Function<Map<String, Object>, JsonObject> finder, Supplier<Map<String, Object>> queryParamGetter) {
            super(finder, queryParamGetter);
        }

        @Override
        public Supplier<JsonObject> getResultRetriever() {
            final PageIterator<JsonObject> producerIterator = new PageIterator<>(this::elementsOfPage);
            return producerIterator::next;
        }

        private Iterator<JsonObject> elementsOfPage(int pageNumber) {
            final JsonObject jsonRet = this.retrieveJsonObject(pageNumber);
            if (jsonRet == null) {
                return null;
            }
            return this.extractContentIterator(jsonRet);

        }

        /**
         * Overide query param to add param of pagination (offset / limit)
         * 
         * @param param : extra parameter (here, page number)
         * @return all params (include pagination).
         */
        @Override
        protected Map<String, Object> getQueryParams(final Object param) {

            // get basic param.
            final Map<String, Object> queryParams = super.getQueryParams(param);

            // build map wth all params.
            final Map<String, Object> allQueryParams = new HashMap<>();
            if (queryParams != null) {
                allQueryParams.putAll(queryParams);
            }
            if (param instanceof Integer) {
                final int pageNumber = (Integer) param;
                allQueryParams.put("offset", pageNumber * ResultGetter.limit);
                allQueryParams.put("limit", ResultGetter.limit);
            }
            return allQueryParams;
        }

        /**
         * Retrieve specific page from workday service.
         * 
         * @param param : page number.
         * @return workday json page of data (format : '"{"total":5, "data":[{....},{...} ...]').
         */
        @Override
        protected JsonObject retrieveJsonObject(Object param) {
            if (!(param instanceof Integer)) {
                return null;
            }
            int pageNumber = (Integer) param;
            log.info("workday search page {}", pageNumber);
            if (this.total >= 0 && (pageNumber * ResultGetter.limit) >= this.total) {
                log.warn("last page already reached, return null");
                return null;
            }

            final JsonObject ret = super.retrieveJsonObject(pageNumber);
            if (this.total < 0) {
                synchronized (this) {
                    this.total = ret.getInt("total");
                }
            }
            return ret;
        }
    }

    /**
     * Concrete class to retrieve simple objects from workday service (without pagination).
     * (format : '"{"total":5, "data":[{....},{...} ...]')
     */
    public static class SimpleRetriever extends Retriever {

        public SimpleRetriever(Function<Map<String, Object>, JsonObject> finder, Supplier<Map<String, Object>> queryParamGetter) {
            super(finder, queryParamGetter);
        }

        @Override
        public Supplier<JsonObject> getResultRetriever() {
            final JsonObject jsonObject = this.retrieveJsonObject(null);
            final Iterator<JsonObject> objectIterator = this.extractContentIterator(jsonObject);
            return () -> {
                if (objectIterator == null || !objectIterator.hasNext()) {
                    return null;
                }
                return objectIterator.next();
            };
        }
    }

}
