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

package org.talend.components.couchbase.testutils;

import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.talend.components.couchbase.CouchbaseUtilTest;
import org.talend.components.couchbase.datastore.CouchbaseDataStore;
import org.talend.components.couchbase.service.CouchbaseService;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.junit5.WithComponents;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WithComponents("org.talend.components.couchbase")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Testing of CouchbaseService class")
public class CouchbaseServiceTest {

    @Service
    private CouchbaseService couchbaseService;

    @Test
    @DisplayName("Two bootstrap nodes without spaces")
    void resolveAddressesTest() {
        String inputUrl = "192.168.0.1,192.168.0.2";
        String[] resultArrayWithUrls = couchbaseService.resolveAddresses(inputUrl);
        assertEquals("192.168.0.1", resultArrayWithUrls[0], "first expected node");
        assertEquals("192.168.0.2", resultArrayWithUrls[1], "second expected node");
    }

    @Test
    @DisplayName("Two bootstrap nodes with extra spaces")
    void resolveAddressesWithSpacesTest() {
        String inputUrl = " 192.168.0.1,  192.168.0.2";
        String[] resultArrayWithUrls = couchbaseService.resolveAddresses(inputUrl);
        assertEquals("192.168.0.1", resultArrayWithUrls[0], "first expected node");
        assertEquals("192.168.0.2", resultArrayWithUrls[1], "second expected node");
    }

    @Test
    void creatingSchemaTest() {
        Schema schema = couchbaseService.getSchema(createStructuredJsonObject());
        System.out.println();
    }

    public JsonObject createStructuredJsonObject() {
        JsonObject jsonObjectInnerInner = JsonObject.create();
        jsonObjectInnerInner.put("val1", "000000");
        jsonObjectInnerInner.put("val2", "111111");
        jsonObjectInnerInner.put("val3", "222222");

        JsonObject jsonObjectInner = JsonObject.create();
        jsonObjectInner.put("mobile", "0989901515");
        jsonObjectInner.put("home", "0342556644");
        jsonObjectInner.put("inner", jsonObjectInnerInner);
        jsonObjectInner.put("some", "444444");

        JsonObject jsonObjectOuterOuter = JsonObject.create();
        jsonObjectOuterOuter.put("id", "0017");
        jsonObjectOuterOuter.put("name", "Patrik");
        jsonObjectOuterOuter.put("surname", "Human");
        jsonObjectOuterOuter.put("tel", jsonObjectInner);

        JsonArray jsonArray = JsonArray.create();
        jsonArray.add("one");
        jsonArray.add("two");
        jsonArray.add("three");

        jsonObjectOuterOuter.put("numbers", jsonArray);

        JsonObject innerArray1 = JsonObject.create();
        innerArray1.put("arr01", 1);
        innerArray1.put("arr02", 2);
        innerArray1.put("arr03", 3);

        JsonObject innerArray2 = JsonObject.create();
        innerArray2.put("arr11", 1);
        innerArray2.put("arr22", 2);
        innerArray2.put("arr33", 3);

        JsonArray jsonArray1 = JsonArray.create();
        jsonArray1.add(innerArray1);
        jsonArray1.add(innerArray2);

        jsonObjectOuterOuter.put("arrWithJSON", jsonArray1);

        return jsonObjectOuterOuter;
    }

    // private Schema createSampleSchema(){
    //
    // }
}
