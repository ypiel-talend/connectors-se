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

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.talend.components.workday.input.services.ResultGetter.PageRetriever;
import org.talend.components.workday.input.services.ResultGetter.SimpleRetriever;

import static org.junit.jupiter.api.Assertions.*;

class ResultGetterTest {

    @Test
    void testSimple() {
        final Function<Map<String, Object>, JsonObject> finderNull = (Map<String, Object> obj) -> null;
        final Supplier<Map<String, Object>> queryParamGetter = () -> null;
        final SimpleRetriever simpleNothing = new SimpleRetriever(finderNull, queryParamGetter);

        final Supplier<JsonObject> noResult = simpleNothing.getResultRetriever();
        Assertions.assertNotNull(noResult);
        Assertions.assertNull(noResult.get());

        final Function<Map<String, Object>, JsonObject> finderOne = (Map<String, Object> obj) -> Json.createObjectBuilder() //
                .add("Hello", "World") //
                .build();

        final SimpleRetriever simpleOne = new SimpleRetriever(finderOne, queryParamGetter);

        final Supplier<JsonObject> OneResult = simpleOne.getResultRetriever();
        final JsonObject jsonObject = OneResult.get();
        Assertions.assertNotNull(jsonObject);
        final JsonObject afterEnd = OneResult.get();
        Assertions.assertNull(afterEnd);

        final Function<Map<String, Object>, JsonObject> finderArrayInt = (Map<String, Object> obj) -> Json.createObjectBuilder() //
                .add("total", 3) //
                .add("data",
                        Json.createArrayBuilder().add(Json.createObjectBuilder().add("Field", 1).build())
                                .add(Json.createObjectBuilder().add("Field", 2).build())
                                .add(Json.createObjectBuilder().add("Field", 3).build()).build())
                .build();
        final SimpleRetriever simpleInt = new SimpleRetriever(finderArrayInt, queryParamGetter);
        final Supplier<JsonObject> IntResult = simpleInt.getResultRetriever();
        final JsonObject One = IntResult.get();
        final JsonObject Two = IntResult.get();
        final JsonObject Tree = IntResult.get();
        final JsonObject Nothing = IntResult.get();
        Assertions.assertNotNull(One);
        Assertions.assertNotNull(One.getValue("/Field"));
        Assertions.assertNotNull(Two);
        Assertions.assertNotNull(Tree);
        Assertions.assertNull(Nothing);
    }

    @Test
    void testPaginator() {
        final Supplier<Map<String, Object>> queryParamGetter = () -> null;
        final PageRetriever pages = new PageRetriever(this::getPage, () -> null);
        final Supplier<JsonObject> jsonGetter = pages.getResultRetriever();

        JsonObject jsonObject = jsonGetter.get();
        int counter = 0;
        while (jsonObject != null) {
            counter++;
            jsonObject = jsonGetter.get();
        }
        Assertions.assertEquals(15, counter); // 5 pages of 3 elements.
    }

    private JsonObject getPage(Map<String, Object> params) {
        final Integer offset = (Integer) params.get("offset");
        if (offset <= 500) {
            return Json.createObjectBuilder() //
                    .add("total", 445) //
                    .add("data",
                            Json.createArrayBuilder().add(Json.createObjectBuilder().add("Field", offset + 1).build())
                                    .add(Json.createObjectBuilder().add("Field", offset + 2).build())
                                    .add(Json.createObjectBuilder().add("Field", offset + 3).build()).build())
                    .build();
        }
        return null;
    }

}