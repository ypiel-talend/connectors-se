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
package org.talend.components.workday.input;

import lombok.extern.slf4j.Slf4j;

import org.talend.components.common.input.PageIterator;
import org.talend.components.workday.dataset.WorkdayServiceDataSet;
import org.talend.components.workday.service.WorkdayReaderService;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.service.Service;

import javax.annotation.PostConstruct;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@Version(1)
@Icon(value = Icon.IconType.CUSTOM, custom = "WorkdayInput")
@Emitter(family = "Workday", name = "Service")
@Documentation("Component to extract data from workday ERP via REST services")
public class WorkdayServiceProducer implements Serializable {

    private static final long serialVersionUID = -293252782589800593L;

    private static final int limit = 100;

    private final InputConfiguration inputConfig;

    @Service
    private WorkdayReaderService reader;

    private int total = -1;

    private transient Supplier<JsonObject> supplierObject = null;

    // private transient Schema responseSchema;

    public WorkdayServiceProducer(@Option("configuration") InputConfiguration inputConfig, WorkdayReaderService reader) {
        this.inputConfig = inputConfig;
        this.reader = reader;
    }

    @PostConstruct
    public void init() {
        this.getSupplierJson();
        // this.responseSchema = null;
    }

    @Producer
    public JsonObject next() {
        // final Schema responseSchema = getResponseSchema();

        JsonObject next = this.getSupplierJson().get();
        /*
         * if (next != null && responseSchema != null) {
         * next = this.complete(next, responseSchema.getEntries());
         * }
         */
        return next;
    }

    /*
     * private synchronized Schema getResponseSchema() {
     * if (this.responseSchema == null) {
     * final RecordBuilderFactory factory = reader.getFactory();
     * this.responseSchema = this.inputConfig.getDataSet().findResponseSchema(factory);
     * }
     * return responseSchema;
     * }
     */

    /*
     * private JsonObject complete(JsonObject object, List<Schema.Entry> entries) {
     * if (object.size() == entries.size()) {
     * return object;
     * }
     * JsonObjectBuilder builder = Json.createObjectBuilder(object);
     * 
     * entries.forEach((Schema.Entry entry) -> {
     * if (!object.containsKey(entry.getName())) {
     * builder.addNull(entry.getName());
     * }
     * });
     * return builder.build();
     * }
     */

    private Iterator<JsonObject> elementsOfPage(int pageNumber) {
        JsonObject jsonRet = this.getPageContent(pageNumber);
        if (jsonRet == null) {
            return null;
        }
        JsonArray data = jsonRet.getJsonArray("data");
        return data.stream().map(JsonObject.class::cast).iterator();
    }

    /**
     * Extract page content.
     *
     * @param pageNumber : page number to extract.
     * @return extracted page.
     */
    private JsonObject getPageContent(int pageNumber) {
        log.warn("workday serach page {}", pageNumber);
        if (this.total >= 0 && (pageNumber * WorkdayServiceProducer.limit) >= this.total) {
            log.warn("last page already reached, return null");
            return null;
        }
        final WorkdayServiceDataSet ds = this.inputConfig.getDataSet();
        Map<String, Object> queryParams = ds.extractQueryParam();

        JsonObject ret = null;
        if (ds.isPaginable()) {
            ret = this.reader.findPage(ds.getDatastore(), ds, (pageNumber * WorkdayServiceProducer.limit),
                    WorkdayServiceProducer.limit, queryParams);
            if (this.total < 0) {
                synchronized (this) {
                    this.total = ret.getInt("total");
                }
            }
        } else {
            ret = this.reader.find(ds.getDatastore(), ds, queryParams);
            this.total = 0;
        }
        return ret;
    }

    private synchronized Supplier<JsonObject> getSupplierJson() {
        if (this.supplierObject == null) {
            final PageIterator<JsonObject> producerIterator = new PageIterator<>(this::elementsOfPage);
            this.supplierObject = producerIterator::next;
        }
        return this.supplierObject;
    }
}
