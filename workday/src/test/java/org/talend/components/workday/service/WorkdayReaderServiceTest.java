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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.talend.components.workday.WorkdayException;
import org.talend.components.workday.dataset.WorkdayServiceDataSet;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Values;
import org.talend.sdk.component.junit5.WithComponents;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.Collection;
import java.util.Iterator;

@WithComponents("org.talend.components.workday")
class WorkdayReaderServiceTest {

    @Service
    private WorkdayReaderService reader;

    @Test
    void extractIterator() {

        final Iterator<JsonObject> iter1 = reader.extractIterator(null, "hello");
        Assertions.assertNotNull(iter1);
        Assertions.assertFalse(iter1.hasNext());

        JsonObject objErr = Json.createObjectBuilder().add("error", "error for test")
                .add("errors", Json.createArrayBuilder().add("ErrTest")).build();
        Assertions.assertThrows(WorkdayException.class, () -> reader.extractIterator(objErr, "hello"),
                "error for test : ErrTest");

        JsonObject objOK = Json.createObjectBuilder().add("tabVide", Json.createArrayBuilder()).build();
        final Iterator<JsonObject> iterArray = reader.extractIterator(objOK, "tabVide");
        Assertions.assertNotNull(iterArray);
        Assertions.assertFalse(iterArray.hasNext());

        JsonObject objOK2 = Json.createObjectBuilder().add("tabOK", Json.createArrayBuilder()
                .add(Json.createObjectBuilder().add("p", "Hello")).add(Json.createObjectBuilder().add("p", "World"))).build();
        final Iterator<JsonObject> iterArray2 = reader.extractIterator(objOK2, "tabOK");
        Assertions.assertNotNull(iterArray2);
        Assertions.assertTrue(iterArray2.hasNext());
        final JsonObject jsonObject = iterArray2.next();
        final String value = jsonObject.getString("p");
        Assertions.assertNotNull(value);
        Assertions.assertEquals("Hello", value.toString());
    }
}
