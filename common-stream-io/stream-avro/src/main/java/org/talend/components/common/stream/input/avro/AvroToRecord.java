/*
 * Copyright (C) 2006-2021 Talend Inc. - www.talend.com
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
package org.talend.components.common.stream.input.avro;

import static java.util.stream.Collectors.toList;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.avro.generic.GenericRecord;
import org.talend.components.common.stream.AvroHelper;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.record.Schema.Entry;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

public class AvroToRecord {

    private final RecordBuilderFactory recordBuilderFactory;

    private Schema recordSchema;

    public AvroToRecord(RecordBuilderFactory recordBuilderFactory) {
        this.recordBuilderFactory = recordBuilderFactory;
    }

    public Schema inferSchema(final GenericRecord record) {
        return new AvroToSchema(this.recordBuilderFactory).inferSchema(record.getSchema());
    }

    public Record toRecord(GenericRecord record) {
        if (recordSchema == null) {
            recordSchema = inferSchema(record);
        }
        return avroToRecord(record, record.getSchema().getFields(), recordBuilderFactory.newRecordBuilder(recordSchema),
                this.recordSchema);
    }

    private Record avroToRecord(final GenericRecord genericRecord, final List<org.apache.avro.Schema.Field> fields,
            final Record.Builder recordBuilder, final Schema tckSchema) {

        for (org.apache.avro.Schema.Field field : fields) {
            final Object value = genericRecord.get(field.name());
            final Entry entry = tckSchema.getEntry(field.name());
            if (org.apache.avro.Schema.Type.ARRAY.equals(field.schema().getType()) && value instanceof Collection) {
                buildArrayField(field, (Collection<?>) value, recordBuilder, entry);
            } else if (!entry.isNullable() || value != null) {
                buildField(field, value, recordBuilder, entry);
            }

        }
        return recordBuilder.build();
    }

    private void buildArrayField(org.apache.avro.Schema.Field field, Collection<?> value, Record.Builder recordBuilder,
            Entry entry) {
        final org.apache.avro.Schema arraySchema = AvroHelper.getUnionSchema(field.schema());
        final org.apache.avro.Schema arrayInnerType = arraySchema.getElementType();

        final Collection<?> objectArray;
        switch (arrayInnerType.getType()) {
        case RECORD:
            objectArray = ((Collection<GenericRecord>) value).stream()
                    .map(record -> avroToRecord(record, arrayInnerType.getFields(),
                            recordBuilderFactory.newRecordBuilder(entry.getElementSchema()), entry.getElementSchema()))
                    .collect(Collectors.toList());
            break;
        case STRING:
            objectArray = value.stream().map(Object::toString).collect(Collectors.toList());
            break;
        case BYTES:
            objectArray = ((Collection<ByteBuffer>) value).stream().map(ByteBuffer::array).collect(toList());
            break;
        case INT:
            objectArray = ((Collection<Integer>) value).stream().collect(toList());
            break;
        case FLOAT:
            objectArray = ((Collection<Float>) value).stream().collect(toList());
            break;
        case DOUBLE:
            objectArray = ((Collection<Double>) value).stream().collect(toList());
            break;
        case BOOLEAN:
            objectArray = ((Collection<Boolean>) value).stream().collect(toList());
            break;
        case LONG:
            objectArray = ((Collection<Long>) value).stream().collect(toList());
            break;
        default:
            throw new IllegalStateException(String.format(Constants.ERROR_UNDEFINED_TYPE, entry.getType().name()));
        }
        recordBuilder.withArray(entry, objectArray);
    }

    protected void buildField(org.apache.avro.Schema.Field field, Object value, Record.Builder recordBuilder, Entry entry) {
        String logicalType = field.schema().getProp(Constants.AVRO_LOGICAL_TYPE);
        org.apache.avro.Schema.Type fieldType = AvroHelper.getFieldType(field);
        switch (fieldType) {
        case RECORD:
            final Schema schema = entry.getElementSchema();
            final Record.Builder builder = recordBuilderFactory.newRecordBuilder(schema);
            recordBuilder.withRecord(entry,
                    avroToRecord((GenericRecord) value, ((GenericRecord) value).getSchema().getFields(), builder, schema));
            break;
        case ARRAY:
            if (value instanceof Collection<?>) {
                buildArrayField(field, (Collection<?>) value, recordBuilder, entry);
            }
            break;
        case STRING:
            recordBuilder.withString(entry, value != null ? value.toString() : null);
            break;
        case BYTES:
            byte[] bytes = value != null ? ((java.nio.ByteBuffer) value).array() : null;
            recordBuilder.withBytes(entry, bytes);
            break;
        case INT:
            int ivalue = value != null ? (Integer) value : 0;
            if (Constants.AVRO_LOGICAL_TYPE_DATE.equals(logicalType)
                    || Constants.AVRO_LOGICAL_TYPE_TIME_MILLIS.equals(logicalType)) {
                recordBuilder.withDateTime(entry, ZonedDateTime.ofInstant(Instant.ofEpochMilli(ivalue), ZoneOffset.UTC));
            } else {
                recordBuilder.withInt(entry, ivalue);
            }
            break;
        case FLOAT:
            recordBuilder.withFloat(entry, value != null ? (Float) value : 0);
            break;
        case DOUBLE:
            recordBuilder.withDouble(entry, value != null ? (Double) value : 0);
            break;
        case BOOLEAN:
            recordBuilder.withBoolean(entry, value != null ? (Boolean) value : Boolean.FALSE);
            break;
        case LONG:
            long lvalue = value != null ? (Long) value : 0;
            if (Constants.AVRO_LOGICAL_TYPE_TIMESTAMP_MILLIS.equals(logicalType)) {
                recordBuilder.withDateTime(entry, ZonedDateTime.ofInstant(Instant.ofEpochMilli(lvalue), ZoneOffset.UTC));
            } else {
                recordBuilder.withLong(entry, lvalue);
            }
            break;
        default:
            throw new IllegalStateException(String.format(Constants.ERROR_UNDEFINED_TYPE, entry.getType().name()));
        }
    }
}
