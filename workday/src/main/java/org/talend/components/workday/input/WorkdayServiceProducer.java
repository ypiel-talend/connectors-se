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

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue.ValueType;

import org.talend.components.workday.dataset.WorkdayServiceDataSet;
import org.talend.components.workday.input.services.ResultGetter;
import org.talend.components.workday.service.WorkdayReaderService;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.service.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Version(1)
@Icon(value = Icon.IconType.CUSTOM, custom = "WorkdayInput")
@Emitter(family = "Workday", name = "Service")
@Documentation("Component to extract data from workday ERP via REST services")
public class WorkdayServiceProducer implements Serializable {

    private static final long serialVersionUID = -293252782589800593L;

    private final InputConfiguration inputConfig;

    @Service
    private WorkdayReaderService reader;

    private transient Supplier<JsonObject> supplierObject = null;

    public WorkdayServiceProducer(@Option("configuration") InputConfiguration inputConfig, WorkdayReaderService reader) {
        this.inputConfig = inputConfig;
        this.reader = reader;
    }

    @Producer
    public JsonObject next() {
        final JsonObject next = this.getSupplierJson().get();
        return next;
    }

    private synchronized Supplier<JsonObject> getSupplierJson() {
        if (this.supplierObject == null) {

            final WorkdayServiceDataSet ds = this.inputConfig.getDataSet();

            final ResultGetter.Retriever retriever = ResultGetter.buildRetriever(this::findWorkdayResult, ds::extractQueryParam,
                    this.inputConfig.getDataSet().isPaginable());
            this.supplierObject = retriever.getResultRetriever();
        }
        return this.supplierObject;
    }

    private JsonObject findWorkdayResult(Map<String, Object> queryParams) {
        final WorkdayServiceDataSet ds = this.inputConfig.getDataSet();
        return this.reader.find(ds.getDatastore(), ds, queryParams);
    }

}
