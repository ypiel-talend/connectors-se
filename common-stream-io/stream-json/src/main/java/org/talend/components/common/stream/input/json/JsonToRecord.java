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
package org.talend.components.common.stream.input.json;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class JsonToRecord {

    private final RecordBuilderFactory factory;

    private final NumberOption numberOption;

    public JsonToRecord(final RecordBuilderFactory factory) {
        this(factory, false);
    }

    public JsonToRecord(RecordBuilderFactory factory, boolean forceNumberAsDouble) {
        this.factory = factory;
        if (forceNumberAsDouble) {
            this.numberOption = NumberOption.ForceDoubleType;
        } else {
            this.numberOption = NumberOption.InferType;
        }
    }

    /*
     * Copy from TCK RecordConverters.java
     * Just removing dependency to JsonLorg.apache.johnzon.core.JsonLongImpl
     * https://github.com/Talend/component-runtime/blob/0597e8dc0498559528a65cde64eccfe1cfea2913/component-runtime-impl/src/main/
     * java/org/talend/sdk/component/runtime/record/RecordConverters.java#L134
     */
    public Record toRecord(final JsonObject object) {
        final Record.Builder builder = factory.newRecordBuilder();
        object.forEach((String key, JsonValue value) -> {
            switch (value.getValueType()) {
            case ARRAY: {
                List<Object> items = value.asJsonArray().stream().map(this::mapJson).collect(toList());
                final Schema arrayElementSchema = getArrayElementSchema(factory, items);

                if (arrayElementSchema.getType() == Schema.Type.RECORD) {
                    // If it is an array of record, we set the elementSchema of the
                    // in all record since it is an aggregate.
                    // If not, some record may have missing entries in their schema.
                    items = items.stream().map(i -> {
                        final Record r = (Record) i;
                        final Record.Builder nestedBuilder = factory.newRecordBuilder(arrayElementSchema);
                        r.getSchema().getEntries().stream().forEach(e -> {
                            switch (e.getType()) {
                            case RECORD:
                                nestedBuilder.withRecord(e, r.getRecord(e.getName()));
                                break;
                            case ARRAY:
                                nestedBuilder.withArray(e, r.getArray(Object.class, e.getName()));
                                break;
                            case STRING:
                                nestedBuilder.withString(e, r.getString(e.getName()));
                                break;
                            case BYTES:
                                nestedBuilder.withBytes(e, r.getBytes(e.getName()));
                                break;
                            case INT:
                                nestedBuilder.withInt(e, r.getInt(e.getName()));
                                break;
                            case LONG:
                                nestedBuilder.withLong(e, r.getLong(e.getName()));
                                break;
                            case FLOAT:
                                nestedBuilder.withFloat(e, r.getFloat(e.getName()));
                                break;
                            case DOUBLE:
                                nestedBuilder.withDouble(e, r.getDouble(e.getName()));
                                break;
                            case BOOLEAN:
                                nestedBuilder.withBoolean(e, r.getBoolean(e.getName()));
                                break;
                            case DATETIME:
                                nestedBuilder.withDateTime(e, r.getDateTime(e.getName()));
                            default:
                                throw new RuntimeException(
                                        "A new type should have been added and should be supported : " + e.getType());
                            }
                        });

                        return nestedBuilder.build();
                    }).collect(toList());
                }

                builder.withArray(factory.newEntryBuilder().withName(key).withType(Schema.Type.ARRAY)
                        .withElementSchema(arrayElementSchema).withNullable(true).build(), items);
                break;
            }
            case OBJECT: {
                final Record record = toRecord(value.asJsonObject());
                builder.withRecord(factory.newEntryBuilder().withName(key).withType(Schema.Type.RECORD)
                        .withElementSchema(record.getSchema()).withNullable(true).build(), record);
                break;
            }
            case TRUE:
            case FALSE:
                final Schema.Entry entry = factory.newEntryBuilder().withName(key).withType(Schema.Type.BOOLEAN)
                        .withNullable(true).build();
                builder.withBoolean(entry, JsonValue.TRUE.equals(value));
                break;
            case STRING:
                builder.withString(key, JsonString.class.cast(value).getString());
                break;
            case NUMBER:
                final JsonNumber number = JsonNumber.class.cast(value);
                this.numberOption.setNumber(builder, factory.newEntryBuilder(), key, number);
                break;
            case NULL:
                break;
            default:
                throw new IllegalArgumentException("Unsupported value type: " + value);
            }
        });
        return builder.build();
    }

    private Object mapJson(final JsonValue it) {
        if (JsonObject.class.isInstance(it)) {
            return toRecord(it.asJsonObject());
        }
        if (JsonArray.class.isInstance(it)) {
            return it.asJsonArray().stream().map(this::mapJson).collect(toList());
        }
        if (JsonString.class.isInstance(it)) {
            return JsonString.class.cast(it).getString();
        }
        if (JsonNumber.class.isInstance(it)) {
            return this.numberOption.getNumber(JsonNumber.class.cast(it));
        }
        if (JsonValue.FALSE.equals(it)) {
            return false;
        }
        if (JsonValue.TRUE.equals(it)) {
            return true;
        }
        if (JsonValue.NULL.equals(it)) {
            return null;
        }
        return it;
    }

    private Schema getArrayElementSchema(final RecordBuilderFactory factory, final List<Object> items) {
        if (items.isEmpty()) {
            return factory.newSchemaBuilder(Schema.Type.STRING).build();
        }
        final Schema firstSchema = toSchema(items.get(0));
        if (firstSchema.getType() == Schema.Type.RECORD) {
            // This code merges schema of all record of the array [{aaa, bbb}, {aaa, ccc}] => {aaa, bbb, ccc}
            return items.stream().skip(1).map(this::toSchema).reduce(firstSchema, (Schema s1, Schema s2) -> {
                if (s1 == null) {
                    return s2;
                }
                if (s2 == null) { // unlikely
                    return s1;
                }
                final List<Schema.Entry> entries1 = s1.getEntries();
                final List<Schema.Entry> entries2 = s2.getEntries();
                final Set<String> names1 = entries1.stream().map(Schema.Entry::getName).collect(toSet());
                final Set<String> names2 = entries2.stream().map(Schema.Entry::getName).collect(toSet());
                if (!names1.equals(names2)) {
                    // here we are not good since values will not be right anymore,
                    // forbidden for current version anyway but potentially supported later
                    final Schema.Builder builder = factory.newSchemaBuilder(Schema.Type.RECORD);
                    entries1.forEach(builder::withEntry);
                    entries2.stream().filter(it -> !names1.contains(it.getName())).forEach(builder::withEntry);
                    return builder.build();
                }
                return s1;
            });
        } else {
            return firstSchema;
        }
    }

    private Schema toSchema(final Object next) {
        if (String.class.isInstance(next) || JsonString.class.isInstance(next)) {
            return factory.newSchemaBuilder(Schema.Type.STRING).build();
        }
        if (Integer.class.isInstance(next)) {
            return factory.newSchemaBuilder(Schema.Type.INT).build();
        }
        if (Long.class.isInstance(next)) {
            return factory.newSchemaBuilder(Schema.Type.LONG).build();
        }
        if (Float.class.isInstance(next)) {
            return factory.newSchemaBuilder(Schema.Type.FLOAT).build();
        }
        if (JsonNumber.class.isInstance(next)) {
            Schema.Type schemaType = this.numberOption.getNumberType(JsonNumber.class.cast(next));
            return factory.newSchemaBuilder(schemaType).build();
        }
        if (BigDecimal.class.isInstance(next)) {
            return factory.newSchemaBuilder(Schema.Type.DOUBLE).build();
        }
        if (Double.class.isInstance(next)) {
            return factory.newSchemaBuilder(Schema.Type.DOUBLE).build();
        }
        if (Boolean.class.isInstance(next) || JsonValue.TRUE.equals(next) || JsonValue.FALSE.equals(next)) {
            return factory.newSchemaBuilder(Schema.Type.BOOLEAN).build();
        }
        if (Date.class.isInstance(next) || ZonedDateTime.class.isInstance(next)) {
            return factory.newSchemaBuilder(Schema.Type.DATETIME).build();
        }
        if (byte[].class.isInstance(next)) {
            return factory.newSchemaBuilder(Schema.Type.BYTES).build();
        }
        if (Collection.class.isInstance(next) || JsonArray.class.isInstance(next)) {
            final Collection collection = Collection.class.cast(next);
            if (collection.isEmpty()) {
                return factory.newSchemaBuilder(Schema.Type.STRING).build();
            }
            return factory.newSchemaBuilder(Schema.Type.ARRAY).withElementSchema(toSchema(collection.iterator().next())).build();
        }
        if (Record.class.isInstance(next)) {
            return Record.class.cast(next).getSchema();
        }
        throw new IllegalArgumentException("unsupported type for " + next);
    }

    private enum NumberOption {
        ForceDoubleType {

            public Number getNumber(JsonNumber number) {
                return number.doubleValue();
            }

            public void setNumber(Record.Builder builder, Schema.Entry.Builder entryBuilder, String key, JsonNumber number) {
                final Schema.Entry entry = entryBuilder.withName(key).withType(Schema.Type.DOUBLE).withNullable(true).build();
                builder.withDouble(entry, number.doubleValue());
            }

            public Schema.Type getNumberType(JsonNumber number) {
                return Schema.Type.DOUBLE;
            }
        },
        InferType {

            public Number getNumber(JsonNumber number) {
                if (number.isIntegral()) {
                    return number.longValueExact();
                } else {
                    return number.doubleValue();
                }
            }

            public void setNumber(Record.Builder builder, Schema.Entry.Builder entryBuilder, String key, JsonNumber number) {
                if (number.isIntegral()) {
                    final Schema.Entry entry = entryBuilder.withName(key).withType(Schema.Type.LONG).withNullable(true).build();
                    builder.withLong(entry, number.longValueExact());
                } else {
                    final Schema.Entry entry = entryBuilder.withName(key).withType(Schema.Type.DOUBLE).withNullable(true).build();
                    builder.withDouble(entry, number.doubleValue());
                }
            }

            public Schema.Type getNumberType(JsonNumber number) {
                if (number.isIntegral()) {
                    return Schema.Type.LONG;
                }
                return Schema.Type.DOUBLE;
            }
        };

        public abstract Number getNumber(JsonNumber number);

        public abstract void setNumber(Record.Builder builder, Schema.Entry.Builder entryBuilder, String key, JsonNumber number);

        public abstract Schema.Type getNumberType(JsonNumber number);
    }

}
