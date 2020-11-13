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
package org.talend.components.recordtester.service.record;

import org.talend.components.recordtester.conf.CodingConfig;
import org.talend.components.recordtester.service.AbstractProvider;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Fixed extends AbstractProvider {

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private long ref_timestamp;

    private final Schema fixed_schema;

    private final Schema sub_record_schema;

    public Fixed() {
        super();

        try {
            ref_timestamp = sdf.parse("01/01/2000").getTime();
        } catch (ParseException e) {
            throw new RuntimeException("Can't generate date.", e);
        }

        sub_record_schema = this.getRecordBuilderFactory().newSchemaBuilder(Schema.Type.RECORD)
                .withEntry(this.newEntry(Schema.Type.STRING, "rec_string")).withEntry(this.newEntry(Schema.Type.INT, "rec_int"))
                .build();

        this.fixed_schema = this.getRecordBuilderFactory().newSchemaBuilder(Schema.Type.RECORD)
                .withEntry(this.newEntry(Schema.Type.STRING, "a_string"))
                .withEntry(this.newEntry(Schema.Type.BOOLEAN, "a_boolean")).withEntry(this.newEntry(Schema.Type.INT, "a_int"))
                .withEntry(this.newEntry(Schema.Type.LONG, "a_long")).withEntry(this.newEntry(Schema.Type.FLOAT, "a_float"))
                .withEntry(this.newEntry(Schema.Type.DOUBLE, "a_double"))
                .withEntry(this.newEntry(Schema.Type.DATETIME, "a_datetime"))
                .withEntry(this.newEntry(Schema.Type.BYTES, "a_byte_array"))
                .withEntry(this.newArrayEntry("a_string_array",
                        this.getRecordBuilderFactory().newSchemaBuilder(Schema.Type.STRING).build()))
                .withEntry(this.newRecordEntry("a_record", sub_record_schema)).build();

    }

    public int getNbFields() {
        return this.fixed_schema.getEntries().size();
    }

    @Override
    public List<Object> get(CodingConfig config) {
        return new FakeList(config.getNbRecord(), this);
    }

    public Record createARecord(int i, int current_null_field, int toggle) {
        Record.Builder builder = this.getRecordBuilderFactory().newRecordBuilder(this.fixed_schema);

        builder.withString("a_string", current_null_field == 1 ? null : "string_" + i);
        if (current_null_field != 2)
            builder.withBoolean("a_boolean", toggle % 2 == 0);
        if (current_null_field != 3)
            builder.withInt("a_int", (toggle % 2 == 0) ? Integer.MIN_VALUE : Integer.MAX_VALUE);
        if (current_null_field != 4)
            builder.withLong("a_long", (toggle % 2 == 0) ? Long.MIN_VALUE : Long.MAX_VALUE);
        if (current_null_field != 5)
            builder.withFloat("a_float", (toggle % 2 == 0) ? Float.MIN_VALUE : Float.MAX_VALUE);
        if (current_null_field != 6)
            builder.withDouble("a_double", (toggle % 2 == 0) ? Double.MIN_VALUE : Double.MAX_VALUE);
        if (current_null_field != 7)
            builder.withFloat("a_float", (toggle % 2 == 0) ? Float.MIN_VALUE : Float.MAX_VALUE);

        try {
            builder.withDateTime("a_date", (current_null_field != 8) ? sdf.parse("10/04/" + (2000 + i)) : null);
        } catch (ParseException e) {
            throw new RuntimeException("Can't generate date.", e);
        }

        if (current_null_field != 8)
            builder.withBytes("a_byte_array", ("index_" + i).getBytes());

        final Record sub_record = this.getRecordBuilderFactory().newRecordBuilder(this.sub_record_schema)
                .withString("rec_string", "rec_string_" + i).withInt("rec_int", i).build();
        builder.withRecord("a_record", current_null_field != 9 ? sub_record : null);

        return builder.build();
    }

    private static class FakeList implements List<Object> {

        private final int size;

        private final Fixed provider;

        public FakeList(int size, Fixed provider) {
            this.size = size;
            this.provider = provider;
        }

        @Override
        public int size() {
            return this.size;
        }

        @Override
        public boolean isEmpty() {
            return size > 0;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<Object> iterator() {
            return new FixedRecordIterator(this.size, this.provider);
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean add(Object record) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends Object> c) {
            return false;
        }

        @Override
        public boolean addAll(int index, Collection<? extends Object> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public Record get(int index) {
            return this.provider.createARecord(index, 0, 1);
        }

        @Override
        public Record set(int index, Object element) {
            return this.provider.createARecord(index, 0, 1);
        }

        @Override
        public void add(int index, Object element) {

        }

        @Override
        public Record remove(int index) {
            return this.provider.createARecord(index, 0, 1);
        }

        @Override
        public int indexOf(Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }

        @Override
        public ListIterator<Object> listIterator() {
            return null;
        }

        @Override
        public ListIterator<Object> listIterator(int index) {
            return null;
        }

        @Override
        public List<Object> subList(int fromIndex, int toIndex) {
            return null;
        }

    }

    private static class FixedRecordIterator implements Iterator<Object> {

        private final int size;

        private final Fixed provider;

        private int current = 0;

        private int current_null_field = 0;

        private int toggle = 1;

        private int nbFields;

        public FixedRecordIterator(int size, Fixed provider) {
            this.size = size;
            this.provider = provider;
            this.nbFields = this.provider.getNbFields();
        }

        @Override
        public boolean hasNext() {
            return current < size;
        }

        @Override
        public Object next() {
            current++;
            current_null_field++;
            if (current_null_field >= this.nbFields) {
                current_null_field = 0;
            }
            toggle++;
            if (toggle >= 3) {
                toggle = 1;
            }
            return this.provider.createARecord(current, current_null_field, toggle);
        }
    }
}
