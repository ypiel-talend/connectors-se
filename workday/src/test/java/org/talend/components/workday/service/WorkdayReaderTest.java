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

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.bind.JsonbBuilder;

import org.apache.xbean.propertyeditor.PropertyEditorRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.talend.components.workday.WorkdayBaseTest;
import org.talend.components.workday.datastore.Token;
import org.talend.components.workday.datastore.WorkdayDataStore;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.http.HttpClientFactory;
import org.talend.sdk.component.api.service.http.Response;
import org.talend.sdk.component.junit.http.junit5.HttpApi;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.manager.reflect.ParameterModelService;
import org.talend.sdk.component.runtime.manager.reflect.ReflectionService;
import org.talend.sdk.component.runtime.manager.service.http.HttpClientFactoryImpl;

@HttpApi(useSsl = true)
@WithComponents("org.talend.components.workday.service")
class WorkdayReaderTest extends WorkdayBaseTest {

    @Service
    private AccessTokenService service;

    @Service
    private AccessTokenProvider provider;

    @Service
    private WorkdayReader reader;

    @BeforeEach
    void before() {
        provider.base(defaultAuthenticationURL);
        reader.base(defaultServiceURL);
    }

    @Test
    void search() {
        WorkdayDataStore wds = this.buildDataStore();
        Token tk = service.getAccessToken(wds, provider);

        String header = tk.getAuthorizationHeaderValue();
        Map<String, String> params = new HashMap<>();
        params.put("offset", "0");
        params.put("limit", "50");
        Response<JsonObject> rs = reader.search(header, "common/v1/workers", params);

        Assertions.assertNotNull(rs, "reponse null");
        Assertions.assertEquals(2, rs.status() / 100, () -> "status = " + rs.status());
        JsonObject objet = rs.body();
        Assertions.assertNotNull(objet, "contenu de reponse null");
        int tot = objet.getInt("total");
        JsonArray array = objet.getJsonArray("data");
        int nbe = array.size();
        Assertions.assertTrue(nbe <= 50, () -> "nbe = " + nbe + " > 50");
    }
}