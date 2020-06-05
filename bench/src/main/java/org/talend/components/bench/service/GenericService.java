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
package org.talend.components.bench.service;

import java.util.Arrays;

import org.apache.johnzon.mapper.Mapper;
import org.apache.johnzon.mapper.MapperBuilder;
import org.talend.components.bench.beans.Large;
import org.talend.components.bench.beans.Medium;
import org.talend.components.bench.beans.Small;
import org.talend.components.bench.config.Dataset;
import org.talend.components.bench.config.Dataset.ObjectSize;
import org.talend.components.bench.config.Dataset.ObjectType;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Record.Builder;
import org.talend.sdk.component.api.record.Schema.Entry;
import org.talend.sdk.component.api.record.Schema.Type;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Data
public class GenericService {

    @Service
    private RecordBuilderFactory recordBuilderFactory;

    private final Mapper mapper = new MapperBuilder().build();

    private final Small objectSmall = Small.builder().build();

    private final Medium objectMedium = Medium.builder().build();

    private final Large objectLarge = Large.builder().build();

    private Record smallRecord;

    private Record mediumRecord;

    private Record largeRecord;

    public GenericService() {
        this.objectSmall.setC1("value 1");
        this.objectSmall.setC2("value 2");
        this.objectSmall.setC3("value 3");

        this.objectMedium.setL1(this.objectSmall.toBuilder().build());
        this.objectMedium.setL2(this.objectSmall.toBuilder().c2("new c2 value").build());
        this.objectMedium.setM1("text for medium object\nwith return");
        this.objectMedium.setNumber1(1255);
        this.objectMedium.setLights(Arrays.asList(this.objectSmall.toBuilder().c3("c3 on list").build(),
                this.objectSmall.toBuilder().c3("c3-next on list").build(),
                this.objectSmall.toBuilder().c3("c3-last on list").build()));

        this.objectLarge.setText1("big text to test large object\nfor test that real test performance");
        this.objectLarge.setText2("big text to test large object\nfor test that real test performance");
        this.objectLarge.setText3("big text to test large object\nfor test that real test performance");
        this.objectLarge.setText4("big text to test large object\nfor test that real test performance");
        this.objectLarge.setM1("Medium text.");
        this.objectLarge.setMed1(this.objectMedium.toBuilder().l1(this.objectSmall.toBuilder().build()).build());
        this.objectLarge.setL1(this.objectSmall.toBuilder().c2("small in large").build());
        this.objectLarge.setMediums(Arrays.asList(this.objectMedium.toBuilder().build(),
                this.objectMedium.toBuilder().l1(this.objectSmall.toBuilder().build()).build()));
        this.objectLarge.setSubObject(this.objectLarge.toBuilder().build());
    }

    private Record buildBasic() {
        return this.recordBuilderFactory.newRecordBuilder().withString("attr1", "basic value 1") //
                .withString("attr2", "basic value 2") //
                .withString("attr3", "basic value 3") //
                .build();
    }

    private void init() {
        this.smallRecord = this.recordBuilderFactory.newRecordBuilder().withString("c1", "value 1") //
                .withString("c2", "value 2") //
                .withString("c3", "value 3") //
                .build();

        this.mediumRecord = this.recordBuilderFactory.newRecordBuilder().withRecord("l1", this.smallRecord) //
                .withString("text1", "Long text for bench performance test\nFrom Talend") //
                .withString("text2", "big text to test large object\nfor test that real test performance") //
                .withString("text3", "big text to test large object\nfor test that real test performance") //
                .withLong("text3", 1225L) //
                .withRecord("other", buildBasic()).build();

        Record basic = this.buildBasic();
        final Entry arrayEntry = this.recordBuilderFactory.newEntryBuilder().withType(Type.ARRAY).withName("array")
                .withElementSchema(basic.getSchema()).build();

        Builder builderLarge = this.recordBuilderFactory.newRecordBuilder() //
                .withRecord("m1", this.mediumRecord) //
                .withRecord("m2", this.mediumRecord) //
                .withArray(arrayEntry, Arrays.asList(basic, this.buildBasic(), this.buildBasic()));
        for (int i = 0; i < 40; i++) {
            builderLarge = builderLarge.withString("field_" + i, "Value String for large record number " + i);
        }
        this.largeRecord = builderLarge.build();
    }

    public Object generate(Dataset.ObjectType ot, Dataset.ObjectSize sz) {
        if (this.smallRecord == null) {
            this.init();
        }
        final Object object = this.findJavaObject(sz);

        if (ot == ObjectType.JAVA_CLASS) {
            return object;
        }
        if (ot == ObjectType.JSON) {
            return this.mapper.writeObjectAsString(object);
        }
        if (ot == ObjectType.RECORD) {
            if (sz == ObjectSize.SMALL) {
                return this.smallRecord;
            }
            if (sz == ObjectSize.MEDIUM) {
                return this.mediumRecord;
            }
            if (sz == ObjectSize.LARGE) {
                return this.largeRecord;
            }
        }
        return null;
    }

    private Object findJavaObject(Dataset.ObjectSize sz) {
        if (sz == ObjectSize.SMALL) {
            return this.objectSmall;
        }
        if (sz == ObjectSize.MEDIUM) {
            return this.objectMedium;
        }
        if (sz == ObjectSize.LARGE) {
            return this.objectLarge;
        }
        return this.objectSmall; // default.
    }

}
