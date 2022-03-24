/*
 * Copyright (C) 2006-2022 Talend Inc. - www.talend.com
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
package org.talend.components.common.stream.output.avro;

import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.stream.Collectors;

import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Array;
import org.apache.avro.generic.GenericRecord;
import org.talend.components.common.stream.AvroHelper;
import org.talend.components.common.stream.api.output.RecordConverter;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.record.Schema.Entry;

public class RecordToAvro implements RecordConverter<GenericRecord, org.apache.avro.Schema> {

    private static final String ERROR_UNDEFINED_TYPE = "Undefined type %s.";

    private org.apache.avro.Schema avroSchema;

    private final String currentRecordNamespace;

    private Schema cachedSchema;

    public RecordToAvro(String currentRecordNamespace) {
        assert currentRecordNamespace != null : "currentRecordNamespace can't be null";
        this.currentRecordNamespace = currentRecordNamespace;
    }

    @Override
    public GenericRecord fromRecord(final Record record) {
        if (avroSchema == null || !(Objects.equals(this.cachedSchema, record.getSchema()))) {
            this.cachedSchema = record.getSchema();
            avroSchema = fromRecordSchema(record.getSchema());
        }
        return recordToAvro(record, newAvroRecord(avroSchema));
    }

    private GenericRecord recordToAvro(final Record fromRecord,
            final GenericRecord toRecord) {
        if (fromRecord == null) {
            return toRecord;
        }
        toRecord.getSchema()
                .getFields() //
                .forEach((final org.apache.avro.Schema.Field field) -> this.toTCKRecord(field, fromRecord, toRecord));

        return toRecord;
    }

    private void toTCKRecord(final org.apache.avro.Schema.Field field,
            final Record fromRecord,
            final GenericRecord toRecord) {
        final String name = field.name();
        final org.apache.avro.Schema.Type fieldType = AvroHelper.getFieldType(field);
        switch (fieldType) {
        case RECORD:
            final Record record = fromRecord.getRecord(name);
            if (record != null) {
                final org.apache.avro.Schema subSchema = field.schema();
                final GenericRecord subrecord = this.recordToAvro(record, newAvroRecord(subSchema));
                toRecord.put(name, subrecord);
            }
            break;
        case ARRAY:
            final Collection<Object> tckArray = fromRecord.getOptionalArray(Object.class, name).orElse(null);
            final Collection<?> avroArray = this.treatCollection(field.schema(), tckArray);
            if (avroArray != null) {
                toRecord.put(name, avroArray);
            }

            break;
        case STRING:
            toRecord.put(name, fromRecord.getOptionalString(name).orElse(null));
            break;
        case BYTES:
            final Optional<byte[]> optionalBytesValue = fromRecord.getOptionalBytes(name);
            if (optionalBytesValue.isPresent()) {
                ByteBuffer byteBuffer = ByteBuffer.wrap(fromRecord.getBytes(name));
                toRecord.put(name, byteBuffer);
            } else {
                toRecord.put(name, null);
            }
            break;
        case INT:
            OptionalInt optionalIntValue = fromRecord.getOptionalInt(name);
            if (optionalIntValue.isPresent()) {
                toRecord.put(name, optionalIntValue.getAsInt());
            } else {
                toRecord.put(name, null);
            }
            break;
        case LONG:
            OptionalLong optionalLongValue = fromRecord.getOptionalLong(name);
            if (optionalLongValue.isPresent()) {
                toRecord.put(name, optionalLongValue.getAsLong());
            } else {
                toRecord.put(name, null);
            }
            break;
        case FLOAT:
            OptionalDouble optionalFloat = fromRecord.getOptionalFloat(name);
            if (optionalFloat.isPresent()) {
                toRecord.put(name, Double.valueOf(optionalFloat.getAsDouble()).floatValue());
            } else {
                toRecord.put(name, null);
            }
            break;
        case DOUBLE:
            OptionalDouble optionalDouble = fromRecord.getOptionalDouble(name);
            if (optionalDouble.isPresent()) {
                toRecord.put(name, optionalDouble.getAsDouble());
            } else {
                toRecord.put(name, null);
            }
            break;
        case BOOLEAN:
            toRecord.put(name, fromRecord.getOptionalBoolean(name).orElse(null));
            break;
        default:
            throw new IllegalStateException(String.format(ERROR_UNDEFINED_TYPE, fieldType.name()));
        }
    }

    private Collection<?> treatCollection(final org.apache.avro.Schema schema,
            final Collection<?> values) {

        if (values == null) {
            return null;
        }
        if (values.isEmpty()) {
            return values;
        }
        final Collection<?> avroValues;
        final Object firstArrayValue = values.iterator().next();
        if (firstArrayValue instanceof Record) {
            final org.apache.avro.Schema subSchema = AvroHelper.getUnionSchema(schema).getElementType();
            avroValues = values
                    .stream()
                    .map(o -> this.recordToAvro((Record) o, this.newAvroRecord(subSchema)))
                    .collect(Collectors.toList());

        } else if (firstArrayValue instanceof Collection) {
            final org.apache.avro.Schema elementType = AvroHelper.getUnionSchema(schema).getElementType();
            avroValues = values.stream()
                    .map(Collection.class::cast)
                    .map((Collection subValues) -> this.treatCollection(elementType, subValues))
                    .collect(Collectors.toList());
        } else {
            avroValues = values;
        }
        return avroValues;
    }

    private GenericData.Record newAvroRecord(final org.apache.avro.Schema schema) {
        final org.apache.avro.Schema simpleSchema = this.extractSimpleType(schema);
        return new GenericData.Record(simpleSchema);
    }

    private org.apache.avro.Schema extractSimpleType(final org.apache.avro.Schema schema) {
        if (!org.apache.avro.Schema.Type.UNION.equals(schema.getType())) {
            return schema;
        }
        return schema.getTypes()
                .stream()
                .filter((org.apache.avro.Schema sub) -> !(org.apache.avro.Schema.Type.NULL.equals(sub.getType())))
                .map((org.apache.avro.Schema sub) -> {
                    if (org.apache.avro.Schema.Type.UNION.equals(sub.getType())) {
                        return extractSimpleType(sub);
                    }
                    return sub;
                })
                .findFirst()
                .orElse(null);
    }

    /**
     * Infer an Avro Schema from a Record SchemaSch
     *
     * @param schema the Record schema
     * @return an Avro Schema
     */
    @Override
    public org.apache.avro.Schema fromRecordSchema(final Schema schema) {
        final SchemaToAvro schemaToAvro = new SchemaToAvro(this.currentRecordNamespace);
        return schemaToAvro.fromRecordSchema(null, schema);
    }

    private Entry getEntry(String name, Schema schema) {
        for (Entry e : schema.getEntries()) {
            if (name.equals(e.getName())) {
                return e;
            }
        }
        return null;
    }

    private Class<?> getJavaClassForType(final Schema.Type type) {
        switch (type) {
        case RECORD:
            return Record.class;
        case ARRAY:
            return Array.class;
        case STRING:
            return String.class;
        case BYTES:
            return Byte[].class;
        case INT:
            return Integer.class;
        case LONG:
            return Long.class;
        case FLOAT:
            return Float.class;
        case DOUBLE:
            return Double.class;
        case BOOLEAN:
            return Boolean.class;
        case DATETIME:
            return ZonedDateTime.class;
        }
        return Object.class;
    }
}
