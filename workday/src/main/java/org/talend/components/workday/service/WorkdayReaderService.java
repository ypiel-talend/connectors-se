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
package org.talend.components.workday.service;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.talend.components.common.schema.JsonToRecord;
import org.talend.components.common.schema.RecordGuessWork;
import org.talend.components.workday.WorkdayException;
import org.talend.components.workday.dataset.QueryHelper;
import org.talend.components.workday.datastore.Token;
import org.talend.components.workday.datastore.WorkdayDataStore;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.http.Response;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import lombok.extern.slf4j.Slf4j;

@Service
@Version(1)
@Slf4j
public class WorkdayReaderService {

    @Service
    private WorkdayReader reader;

    @Service
    private AccessTokenService accessToken;

    @Service
    private RecordBuilderFactory factory;

    public JsonObject find(WorkdayDataStore datastore, QueryHelper helper, Map<String, String> queryParams) {
        final Token token = accessToken.findToken(datastore);
        final String authorizeHeader = token.getAuthorizationHeaderValue();

        this.reader.base(datastore.getEndpoint());

        final String serviceToCall = helper.getServiceToCall();
        log.debug("calling service {}", serviceToCall);

        Response<JsonObject> result = reader.search(authorizeHeader, serviceToCall, queryParams);

        if (result.status() / 100 != 2) {
            String errorLib = result.error(String.class);
            log.error("Error while retrieve data {} : HTTP {} : {}", serviceToCall, result.status(), errorLib);
            throw new WorkdayException(errorLib);
        }
        return result.body();
    }

    public Iterator<Record> extractIterator(JsonObject result, String arrayName) {
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
        final RecordGuessWork gw = new RecordGuessWork();
        data.forEach((JsonValue v) -> {
            if (v instanceof JsonObject) {
                gw.add(v.asJsonObject());
            }
        });
        final Schema schema = gw.generateSchema(this.factory);
        final JsonToRecord toRecord = new JsonToRecord(this.factory);


        return data.stream()
                .map(JsonObject.class::cast)
                .map((JsonObject js) -> toRecord.toRecord(js, schema))
                .iterator();
    }
}
