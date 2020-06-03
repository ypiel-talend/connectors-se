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

import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.record.Schema.Entry;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * Translate json object to record.
 *
 * CAUTIONS :
 * records don't support variables objects in array ..
 * while in json, array can contains different kind of type :
 * `[ "Is a String", 123, { "field": "value" }, [1, 2, "text"] ]`
 * records does not.
 */
@Slf4j
public class JsonToRecord {

    /** record facotry */
    private final RecordBuilderFactory recordBuilderFactory;

    public JsonToRecord(RecordBuilderFactory recordBuilderFactory) {
        this.recordBuilderFactory = recordBuilderFactory;
    }

    /**
     * Convert json object to record (with guessing schema).
     * 
     * @param json : json data.
     * @param schema : schema
     * @return data in record format.
     */
    public Record toRecord(final JsonObject json, final Schema schema) {
        if (json == null) {
            return null;
        }
        return convertJsonObjectToRecord(schema, json);
    }

    private Record convertJsonObjectToRecord(Schema schema, JsonObject json) {
        final Record.Builder builder = recordBuilderFactory.newRecordBuilder(schema);
        schema.getEntries().forEach((Entry entry) -> this.integrateEntryToRecord(entry, builder, json));

        return builder.build();
    }

    private void integrateEntryToRecord(Entry entry, Record.Builder builder, JsonObject json) {
        if (!json.containsKey(entry.getName()) || json.isNull(entry.getName())) {
            return;
        }
        final JsonValue jsonValue = json.get(entry.getName());
        if (jsonValue == null) {
            return;
        }

        switch (entry.getType()) {
        case RECORD: {
            if (jsonValue instanceof JsonObject) {
                final JsonObject jsonObject = jsonValue.asJsonObject();
                final Record record = convertJsonObjectToRecord(entry.getElementSchema(), jsonObject);
                builder.withRecord(entry, record);
            }
        }
            break;
        case ARRAY:
            if (jsonValue instanceof JsonArray) {
                final List<?> objects = convertJsonArray(entry.getElementSchema(), jsonValue.asJsonArray());
                if (objects != null) {
                    builder.withArray(entry, objects);
                }
            }
            break;
        case STRING: {
            String value = json.getString(entry.getName());
            builder.withString(entry, value);
        }
            break;
        case INT: {
            int value = jsonValue instanceof JsonNumber ? json.getInt(entry.getName())
                    : Integer.parseInt(json.getString(entry.getName()));
            builder.withInt(entry, value);
        }
            break;
        case LONG: {
            long value = this.getLong(jsonValue);
            builder.withLong(entry, value);
        }
            break;
        case FLOAT:
        case DOUBLE: {
            double value = this.getDouble(jsonValue);
            builder.withDouble(entry, value);
        }
            break;
        case BOOLEAN: {
            boolean value = this.getBoolean(jsonValue);
            builder.withBoolean(entry, value);
        }
            break;
        case BYTES: {
            String value = json.getString(entry.getName());
            builder.withBytes(entry, value.getBytes());
        }
            break;
        case DATETIME:
            try {
                String value = json.getString(entry.getName());
                builder.withDateTime(entry, ZonedDateTime.parse(value));
            } catch (DateTimeException e) {
                log.error("[convertJsonObjectToRecord] parse ZonedDateTime failed for {} : {}.", entry.getName(),
                        json.get(entry.getName()));
            }
            break;
        }
    }

    /**
     * Extract list of record format element from json array.
     * 
     * @param arraySchema : schema of json array element.
     * @param json : json array.
     * @return list of value.
     */
    private List<? extends Object> convertJsonArray(Schema arraySchema, JsonArray json) {

        final List<? extends Object> result;
        switch (arraySchema.getType()) {
        case RECORD:
            result = json.stream().map((JsonValue v) -> convertJsonObjectToRecord(arraySchema, v.asJsonObject()))
                    .collect(Collectors.toList());
            break;
        case ARRAY:
            result = json.stream().map((JsonValue v) -> convertJsonArray(arraySchema.getElementSchema(), v.asJsonArray()))
                    .collect(Collectors.toList());

            break;
        case STRING:
            result = json.stream().map(JsonString.class::cast).map(JsonString::getString).collect(Collectors.toList());
            break;
        case LONG:
            result = json.stream().map(this::getLong).collect(Collectors.toList());
            break;
        case DOUBLE:
            result = json.stream().map(this::getDouble).collect(Collectors.toList());
            break;
        case BOOLEAN:
            result = json.stream().map(this::getBoolean).collect(Collectors.toList());
            break;
        default: {
            result = null;
        }
        }

        return result;
    }

    private Long getLong(JsonValue value) {
        if (value == null) {
            return null;
        }
        if (value instanceof JsonNumber) {
            return ((JsonNumber) value).longValue();
        }
        if (value instanceof JsonString) {
            try {
                return Long.parseLong(((JsonString) value).getString());
            } catch (NumberFormatException ex) {
            }
        }
        return null;
    }

    private Double getDouble(JsonValue value) {
        if (value == null) {
            return null;
        }
        if (value instanceof JsonNumber) {
            return ((JsonNumber) value).doubleValue();
        }
        if (value instanceof JsonString) {
            try {
                return Double.parseDouble(((JsonString) value).getString());
            } catch (NumberFormatException ex) {
            }
        }
        return null;
    }

    private Boolean getBoolean(JsonValue value) {
        if (value == null) {
            return null;
        }
        if (value == JsonValue.TRUE) {
            return Boolean.TRUE;
        }
        if (value == JsonValue.FALSE) {
            return Boolean.FALSE;
        }
        if (value instanceof JsonString) {
            return Boolean.parseBoolean(((JsonString) value).getString());
        }
        return null;
    }
}
