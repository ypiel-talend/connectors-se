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

import lombok.Data;
import org.talend.components.recordtester.conf.CodingConfig;
import org.talend.components.recordtester.conf.Config;
import org.talend.components.recordtester.service.AbstractProvider;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class SchemaWithANull extends AbstractProvider {

    private boolean missing = false;

    public SchemaWithANull() {
        this.setMissing(false);
    }

    @Override
    public List<Object> get(final CodingConfig config) {
        final Schema nestedRecord = getRecordBuilderFactory().newSchemaBuilder(Schema.Type.RECORD)
                .withEntry(newEntry(Schema.Type.STRING, "aaa")).withEntry(newEntry(Schema.Type.STRING, "bbb")).build();

        final Schema schema = getRecordBuilderFactory().newSchemaBuilder(Schema.Type.RECORD)
                .withEntry(newArrayEntry("data", nestedRecord)).withEntry(newEntry(Schema.Type.STRING, "val2"))
                .withEntry(newEntry(Schema.Type.DOUBLE, "val1")).build();

        List<Object> records = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            records.add(getRecord(i, newArrayEntry("data", nestedRecord), this.isMissing()));
        }

        return records;
    }

    private Record getRecord(int i, Schema.Entry arrayEntry, boolean missing) {
        final Record rec1 = getRecordBuilderFactory().newRecordBuilder().withString("aaa", "aaa" + i).withString("bbb", "bbb" + i)
                .build();
        final Record rec2 = getRecordBuilderFactory().newRecordBuilder().withString("aaa", "aaaB" + i)
                .withString("bbb", "bbbB" + i).build();

        final List<Record> elts = Arrays.asList(rec1, rec2);
        Record.Builder intermediate = getRecordBuilderFactory().newRecordBuilder().withArray(arrayEntry, elts).withDouble("val1",
                12.12d);
        if (i % 3 != 0) {
            intermediate = intermediate.withString("val2", "val2_" + i);
        } else {
            if (!missing) {
                intermediate = intermediate.withString("val2", null);
            }
        }

        return intermediate.build();
    }

}
