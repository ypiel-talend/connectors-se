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

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

public class EntryGuessWork {

    private final String name;

    private boolean nullable = false;

    private final Set<Schema.Type> possibleTypes = EnumSet.allOf(Schema.Type.class);

    private RecordGuessWork record = null;

    private ArrayGuessWork array = null;

    public EntryGuessWork(String name) {
        this.name = name;
        this.possibleTypes.remove(Schema.Type.DATETIME); // not native json
        this.possibleTypes.remove(Schema.Type.INT); // long default for numbers
        this.possibleTypes.remove(Schema.Type.FLOAT); // double default for floating numbers
        this.possibleTypes.remove(Schema.Type.BYTES); // String

        this.possibleTypes.remove(Schema.Type.RECORD); // record add if encountered
        this.possibleTypes.remove(Schema.Type.ARRAY); // array add if encountered
    }

    public Schema.Entry generate(RecordBuilderFactory factory) {
        final Schema.Entry.Builder builder = factory.newEntryBuilder().withName(this.name).withNullable(this.nullable);
        this.completeWithType(factory, builder);
        return builder.build();
    }

    protected void completeWithType(RecordBuilderFactory factory, Schema.Entry.Builder entryBuilder) {

        final Schema.Type realType = this.getPriorSchema();
        entryBuilder.withType(realType);

        if (realType == Schema.Type.RECORD && this.record != null) {
            final Schema schema = this.record.generateSchema(factory);
            entryBuilder.withElementSchema(schema);
        }

        if (realType == Schema.Type.ARRAY && this.array != null) {
            entryBuilder.withElementSchema(this.array.generateSchema(factory));
        }
    }

    void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    boolean isNullable() {
        return nullable;
    }

    public String getName() {
        return name;
    }

    private Schema.Type getPriorSchema() {
        return findFirst(Schema.Type.RECORD, Schema.Type.ARRAY, Schema.Type.LONG, Schema.Type.BOOLEAN, Schema.Type.DOUBLE,
                Schema.Type.STRING);
    }

    private Schema.Type findFirst(Schema.Type... types) {
        for (Schema.Type current : types) {
            if (this.possibleTypes.contains(current)) {
                return current;
            }
        }
        return Schema.Type.STRING;
    }

    public void add(JsonValue value) {
        if (value == null) {
            this.nullable = true;
            return;
        }
        switch (value.getValueType()) {
        case ARRAY:
            this.addArray(((JsonArray) value));
            break;
        case OBJECT:
            this.addRecord(((JsonObject) value));
            break;
        case STRING:
            this.addString(((JsonString) value).getString());
            break;
        case NUMBER:
            this.addNumber((JsonNumber) value);
            break;
        case TRUE:
        case FALSE:
            this.addBoolean();
            break;
        case NULL:
            this.nullable = true;
            break;
        }
    }

    private void addArray(JsonArray array) {
        if (!this.possibleTypes.contains(Schema.Type.ARRAY)) {
            this.possibleTypes.add(Schema.Type.ARRAY);
            this.array = new ArrayGuessWork();
        }
        for (JsonValue jv : array) {
            this.array.add(jv);
        }
    }

    private void addRecord(JsonObject object) {
        if (!this.possibleTypes.contains(Schema.Type.RECORD)) {
            this.possibleTypes.add(Schema.Type.RECORD);
            this.record = new RecordGuessWork();
        }
        this.record.add(object);
    }

    private void addBoolean() {
        if (!this.possibleTypes.contains(Schema.Type.BOOLEAN)) { // several json type for this column => to default String
            this.possibleTypes.clear();
            this.possibleTypes.add(Schema.Type.STRING);
        } else if (this.possibleTypes.size() > 1) {
            this.possibleTypes.clear();
            this.possibleTypes.add(Schema.Type.BOOLEAN);
        }
    }

    private void addNumber(JsonNumber jsonNumber) {
        if (!jsonNumber.isIntegral()) {
            if (this.possibleTypes.contains(Schema.Type.LONG)) {
                this.possibleTypes.remove(Schema.Type.LONG);
            }
        }
    }

    private void addString(String s) {
        if (this.possibleTypes.size() == 1) {
            return;
        }
        if (s.isEmpty()) {
            this.possibleTypes.clear();
            this.possibleTypes.add(Schema.Type.STRING);
            return;
        }
        this.withString(s, Integer::parseInt, Schema.Type.INT);
        this.withString(s, Long::parseLong, Schema.Type.LONG);
        this.withString(s, Float::parseFloat, Schema.Type.FLOAT);
        this.withString(s, Double::parseDouble, Schema.Type.DOUBLE);
        if (this.possibleTypes.contains(Schema.Type.BOOLEAN) && !"false".equalsIgnoreCase(s) && !"true".equalsIgnoreCase(s)) {
            this.possibleTypes.remove(Schema.Type.BOOLEAN);
        }
    }

    private void withString(String s, Consumer<String> converter, Schema.Type testedType) {
        if (this.possibleTypes.contains(testedType)) {
            try {
                converter.accept(s);
            } catch (NumberFormatException ex) {
                this.possibleTypes.remove(testedType);
            }
        }
    }
}
