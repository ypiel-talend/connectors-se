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
package org.talend.components.common.schema;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.runtime.record.RecordBuilderFactoryImpl;

class RecordGuessWorkTest {

    private final RecordBuilderFactory factory = new RecordBuilderFactoryImpl("test");

    @Test
    void typeTest() {
        final JsonObject o1 = Json.createObjectBuilder().add("Hello", "3.2").add("number", "1").build();
        final JsonObject o2 = Json.createObjectBuilder().add("number", 2).add("option", "true").build();
        final RecordGuessWork gw = new RecordGuessWork();
        gw.add(o1);
        gw.add(o2);
        final Schema schema = gw.generateSchema(this.factory);
        Assertions.assertNotNull(schema);
        Assertions.assertEquals(Schema.Type.RECORD, schema.getType());
        Assertions.assertNull(schema.getElementSchema());
        Assertions.assertEquals(3, schema.getEntries().size());

        JsonToRecord tr = new JsonToRecord(this.factory);
        final Record r1 = tr.toRecord(o1, schema);
        final Record r2 = tr.toRecord(o2, schema);
        Assertions.assertNotNull(r1);
        Assertions.assertNotNull(r2);
        Assertions.assertEquals(3.2d, r1.getDouble("Hello"), 0.001d);
        Assertions.assertEquals(1L, r1.getLong("number"));
        Assertions.assertFalse(r1.getOptionalBoolean("option").isPresent());

        Assertions.assertFalse(r2.getOptionalDouble("Hello").isPresent());
        Assertions.assertEquals(2L, r2.getLong("number"));
        Assertions.assertEquals(true, r2.getBoolean("option"));
    }

    @Test
    public void testSimple() {
        final RecordGuessWork gw = new RecordGuessWork();
        final JsonObject o1 = Json.createObjectBuilder().add("Hello", "World").add("number", "1").build();
        final JsonObject o2 = Json.createObjectBuilder().add("number", 2).add("option", true).build();
        gw.add(o1);
        gw.add(o2);
        final Schema schema = gw.generateSchema(this.factory);
        Assertions.assertNotNull(schema);
        Assertions.assertEquals(Schema.Type.RECORD, schema.getType());
        Assertions.assertNull(schema.getElementSchema());
        Assertions.assertEquals(3, schema.getEntries().size());

        JsonToRecord tr = new JsonToRecord(this.factory);
        final Record r1 = tr.toRecord(o1, schema);
        final Record r2 = tr.toRecord(o2, schema);
        Assertions.assertNotNull(r1);
        Assertions.assertNotNull(r2);
        Assertions.assertEquals("World", r1.getString("Hello"));
        Assertions.assertEquals(1L, r1.getLong("number"));
        Assertions.assertFalse(r1.getOptionalBoolean("option").isPresent());

        Assertions.assertEquals(null, r2.getString("Hello"));
        Assertions.assertEquals(2L, r2.getLong("number"));
        Assertions.assertEquals(true, r2.getBoolean("option"));
    }

    @Test
    void innerRecord() {
        final RecordGuessWork gw = new RecordGuessWork();
        final JsonObject o1 = Json.createObjectBuilder().add("Hello", "World")
                .add("subRecord", Json.createObjectBuilder().add("field_1", "val1").add("field_2", "val2")).build();
        final JsonObject o2 = Json.createObjectBuilder().add("Hello", "World")
                .add("subRecord", Json.createObjectBuilder().add("field_2", "val2")).build();

        gw.add(o1);
        gw.add(o2);
        final Schema schema = gw.generateSchema(this.factory);

        JsonToRecord tr = new JsonToRecord(this.factory);
        final Record r1 = tr.toRecord(o1, schema);
        final Record r2 = tr.toRecord(o2, schema);
        Assertions.assertNotNull(r1);
        Assertions.assertNotNull(r2);
        final Record subRecord1 = r1.getRecord("subRecord");
        Assertions.assertNotNull(subRecord1);

        Assertions.assertEquals("val1", subRecord1.getString("field_1"));
    }

    @Test
    void innerRecordArray() {
        final RecordGuessWork gw = new RecordGuessWork();
        final JsonObject o1 = Json.createObjectBuilder().add("Hello", "World")
                .add("array",
                        Json.createArrayBuilder().add(Json.createObjectBuilder().add("field_1", "val1").add("field_2", "val2")))
                .build();
        final JsonObject o2 = Json.createObjectBuilder().add("Hello", "People")
                .add("array",
                        Json.createArrayBuilder()
                                .add(Json.createObjectBuilder().add("field_1", "val1_1").add("field_2_2", "val_2"))
                                .add(Json.createObjectBuilder().add("field_1", "field_1v_1").add("field_2", "val_2")))
                .build();

        gw.add(o1);
        gw.add(o2);
        final Schema schema = gw.generateSchema(this.factory);

        JsonToRecord tr = new JsonToRecord(this.factory);
        final Record r1 = tr.toRecord(o1, schema);
        final Record r2 = tr.toRecord(o2, schema);
        Assertions.assertNotNull(r1);
        Assertions.assertNotNull(r2);
        final Collection<Record> array2 = r2.getArray(Record.class, "array");
        Assertions.assertNotNull(array2);
        Assertions.assertEquals(2, array2.size());
    }

    @Test
    void innerSimpleArray() {
        final RecordGuessWork gw = new RecordGuessWork();
        final JsonObject o1 = Json.createObjectBuilder().add("Hello", "World")
                .add("array", Json.createArrayBuilder().add("First")).build();
        final JsonObject o2 = Json.createObjectBuilder().add("Hello", "People")
                .add("array", Json.createArrayBuilder().add("Second").add("Third")).build();

        gw.add(o1);
        gw.add(o2);
        final Schema schema = gw.generateSchema(this.factory);

        JsonToRecord tr = new JsonToRecord(this.factory);
        final Record r1 = tr.toRecord(o1, schema);
        final Record r2 = tr.toRecord(o2, schema);
        Assertions.assertNotNull(r1);
        Assertions.assertNotNull(r2);
        final Collection<String> array2 = r2.getArray(String.class, "array");
        Assertions.assertNotNull(array2);
        Assertions.assertEquals(2, array2.size());
        Assertions.assertTrue(array2.contains("Second"));
        Assertions.assertTrue(array2.contains("Third"));
    }

    @Test
    void testArrayOfArray() {
        final JsonObject jsonObject1 = Json.createObjectBuilder().add("Hello", "World")
                .add("arrayOfArray", Json.createArrayBuilder().add(Json.createArrayBuilder().add(20).add(30).add(40))
                        .add(Json.createArrayBuilder().add(11).add(12).add(13)))
                .build();

        final JsonObject jsonObject2 = Json.createObjectBuilder().add("Hello", "World1")
                .add("arrayOfArray", Json.createArrayBuilder().add(Json.createArrayBuilder().add(20).add(30).add(40))
                        .add(Json.createArrayBuilder().add(11).add(12).add(13)))
                .build();
        final RecordGuessWork gw = new RecordGuessWork();
        gw.add(jsonObject1);
        gw.add(jsonObject2);
        final Schema schema = gw.generateSchema(this.factory);

        JsonToRecord tr = new JsonToRecord(this.factory);
        final Record r1 = tr.toRecord(jsonObject1, schema);
        final Record r2 = tr.toRecord(jsonObject2, schema);
        Assertions.assertNotNull(r1);
        Assertions.assertNotNull(r2);
    }

    @Test
    void testComplex() {
        final JsonObject jsonObject1 = Json.createObjectBuilder().add("Hello", "World")
                .add("array", Json.createArrayBuilder().add("First"))
                .add("arrayOfObject",
                        Json.createArrayBuilder().add(Json.createObjectBuilder().add("f1", "v1"))
                                .add(Json.createObjectBuilder().add("f1", "v2").add("f2", "v2f2").addNull("f3")))
                .add("arrayOfArray",
                        Json.createArrayBuilder().add(Json.createArrayBuilder().add(20).add(30).add(40))
                                .add(Json.createArrayBuilder().add(11).add(12).add(13)))
                .add("subRecord", Json.createObjectBuilder().add("field_1", "val1").add("field_2", "val2")).build();

        final JsonObject jsonObject2 = Json.createObjectBuilder().add("Hello", "World1")
                .add("array", Json.createArrayBuilder().add("First"))
                .add("arrayOfObject",
                        Json.createArrayBuilder().add(Json.createObjectBuilder().add("f1", "v1"))
                                .add(Json.createObjectBuilder().add("f1", "v2").addNull("f3")))
                .add("arrayOfArray",
                        Json.createArrayBuilder().add(Json.createArrayBuilder().add(20).add(30).add(40))
                                .add(Json.createArrayBuilder().add(11).add(12).add(13)))
                .add("subRecord", Json.createObjectBuilder().add("field_1", "val1").add("field_2", "val2")).build();
        final RecordGuessWork gw = new RecordGuessWork();
        gw.add(jsonObject1);
        gw.add(jsonObject2);
        final Schema schema = gw.generateSchema(this.factory);

        JsonToRecord tr = new JsonToRecord(this.factory);
        final Record r1 = tr.toRecord(jsonObject1, schema);
        final Record r2 = tr.toRecord(jsonObject2, schema);
        Assertions.assertNotNull(r1);
        Assertions.assertNotNull(r2);
    }

    @Test
    void loadComplexFromFilesRecordsA(){
        int[] n = new int[]{1,2,3};

        final RecordGuessWork gw = new RecordGuessWork();
        for(int i : n) {
            try (InputStream fis = getClass().getClassLoader().getResourceAsStream("recordsA/00"+i+".json")) {
                JsonReader reader = Json.createReader(fis);
                JsonObject jsonObject = reader.readObject();
                gw.add(jsonObject);
            } catch (IOException e) {
                System.err.println("err : " + e);
                e.printStackTrace();
            }
        }

        final Schema schema = gw.generateSchema(this.factory);
        Assertions.assertEquals("dc", schema.getEntries().get(3).getElementSchema().getEntries().get(2).getName());
        Assertions.assertEquals(Schema.Type.DOUBLE, schema.getEntries().get(3).getElementSchema().getEntries().get(2).getType());
    }
}