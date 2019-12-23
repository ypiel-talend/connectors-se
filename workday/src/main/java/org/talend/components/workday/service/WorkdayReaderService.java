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
package org.talend.components.workday.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.talend.components.workday.WorkdayException;
import org.talend.components.workday.dataset.QueryHelper;
import org.talend.components.workday.datastore.Token;
import org.talend.components.workday.datastore.WorkdayDataStore;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.http.Response;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Version(1)
@Slf4j
public class WorkdayReaderService {

    @Service
    private WorkdayReader reader;

    @Service
    private AccessTokenService accessToken;

    @Getter
    @Service
    private RecordBuilderFactory factory;

    // private final transient SwaggerLoader loader;

    public WorkdayReaderService() {
        final URL swaggersDirectory = WorkdayReaderService.class.getClassLoader().getResource("swaggers/");
        // this.loader = new SwaggerLoader(swaggersDirectory.getPath());
    }

    public JsonObject find(WorkdayDataStore datastore, QueryHelper helper, Map<String, Object> queryParams) {
        final Token token = accessToken.findToken(datastore);
        final String authorizeHeader = token.getAuthorizationHeaderValue();

        this.reader.base(datastore.getEndpoint());

        final String serviceToCall = helper.getServiceToCall();
        // helper.
        log.info("calling service {}", serviceToCall);

        Map<String, Object> queryParamsCompat = queryParams.entrySet().stream()
                .filter((Map.Entry<String, Object> e) -> e.getValue() != null).collect(
                        Collectors.toMap(Map.Entry::getKey, (Map.Entry<String, Object> e) -> buildQueryParamValue(e.getValue())));
        final Response<JsonObject> result = reader.search(authorizeHeader, serviceToCall, queryParamsCompat);

        if (result.status() / 100 != 2) {
            String errorLib = result.error(String.class);
            log.error("Error while retrieve data {} : HTTP {} : {}", serviceToCall, result.status(), errorLib);
            throw new WorkdayException(errorLib);
        }
        return result.body();
    }

    /**
     * Make value type compatible with RequestParser.QueryParamsProvider (component runtime)
     * (either collection, either String)
     * 
     * @param originalValue : value.
     * @return compatible value.
     */
    private Object buildQueryParamValue(Object originalValue) {
        if (Collection.class.isInstance(originalValue)) {
            return originalValue;
        }
        return String.valueOf(originalValue);
    }

    public JsonObject findPage(WorkdayDataStore datastore, QueryHelper helper, int offset, int limit,
            Map<String, Object> queryParams) {
        final Map<String, Object> allQueryParams = new HashMap<>();
        if (queryParams != null) {
            allQueryParams.putAll(queryParams);
        }
        allQueryParams.put("offset", offset);
        allQueryParams.put("limit", limit);
        return this.find(datastore, helper, allQueryParams);
    }

    public Iterator<JsonObject> extractIterator(JsonObject result, String arrayName) {
        if (result == null) {
            return Collections.emptyIterator();
        }
        final String error = result.getString("error", null);
        if (error != null) {
            final JsonArray errors = result.getJsonArray("errors");
            throw new WorkdayException(error + " : " + errors.toString());
        }
        final JsonArray data = result.getJsonArray(arrayName);
        if (data == null || data.isEmpty()) {
            return Collections.emptyIterator();
        }
        return data.stream().map(JsonObject.class::cast).iterator();
    }

    /*
     * @DynamicValues("workdayModules")
     * public Values loadModules() {
     * log.info("loadModules");
     * return new Values(loader.getModules());
     * }
     */

    /*
     * @Suggestions("workdayServices")
     * public SuggestionValues loadServices(String module) {
     * log.info("loadServices for module {}", module);
     * final List<SuggestionValues.Item> services = loader.findGetServices(module).keySet().stream()
     * .map((String service) -> new SuggestionValues.Item(service, service))
     * .sorted(Comparator.comparing(SuggestionValues.Item::getLabel)).collect(Collectors.toList());
     * 
     * return new SuggestionValues(false, services);
     * }
     */

    /**
     * @Update("workdayServicesParams")
     * public WorkdayServiceDataSet.Parameters loadServiceParameter(String module, String service) {
     * log.info("workdayServicesParams suggestion for {} {}", module, service);
     * final WorkdayServiceDataSet.Parameters parameters = new WorkdayServiceDataSet.Parameters();
     * parameters.setParametersList(Collections.emptyList());
     * 
     * final Map<String, Schema> responses = this.loader.findGetResponse(module, this.factory);
     * Schema responseSchema = responses.get(service);
     * parameters.setResponseSchema(responseSchema);
     * 
     * final Map<String, List<WorkdayServiceDataSet.Parameter>> moduleServices = this.loader.findGetServices(module);
     * if (moduleServices == null) {
     * log.warn("no module {}", module);
     * return parameters;
     * }
     * final List<WorkdayServiceDataSet.Parameter> serviceParameters = moduleServices.get(service);
     * if (serviceParameters == null) {
     * return parameters;
     * }
     * log.info("workdayServicesParams : nombre params {}", serviceParameters.size());
     * parameters.setParametersList(serviceParameters);
     * 
     * return parameters;
     * }
     */
}
