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

import javax.json.JsonValue;

import org.apache.johnzon.mapper.Mapper;
import org.apache.johnzon.mapper.MapperBuilder;
import org.talend.components.bench.beans.FlatLarge;
import org.talend.components.bench.beans.Large;
import org.talend.components.bench.beans.Medium;
import org.talend.components.bench.beans.Small;
import org.talend.components.bench.config.Dataset;
import org.talend.components.bench.config.Dataset.ObjectSize;
import org.talend.components.bench.config.Dataset.ObjectType;
import org.talend.components.common.stream.input.json.JsonToRecord;
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
public class GenericService {

    @Service
    private RecordBuilderFactory recordBuilderFactory;

    private final Mapper mapper = new MapperBuilder().build();

    private final Small objectSmall = Small.builder().build();

    private final Medium objectMedium = Medium.builder().build();

    private final Large objectLarge = Large.builder().build();

    private final FlatLarge flatLargeObject = new FlatLarge();

    public GenericService() {
        this.objectSmall.setC1("value 1");
        this.objectSmall.setC2("value 2");
        this.objectSmall.setC3("value 3");
        this.objectSmall.setD1(23.3d);
        this.objectSmall.setB1(Boolean.TRUE);

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

        this.flatLargeObject.setText1("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText2("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText3("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText4("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText5("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText6("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText7("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText8("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText9("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText10("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText11("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText12("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText13("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText14("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText15("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText16("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText17("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText18("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText19("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText20("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText21("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText22("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText23("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText24("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText25("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText26("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText27("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText28("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText29("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText30("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText31("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText32("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText33("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText34("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText35("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText36("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText37("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText38("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText39("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText40("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText41("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText42("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText43("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText44("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText45("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText46("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText47("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText48("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText49("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText50("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText51("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText52("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText53("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText54("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText55("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText56("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText57("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText58("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText59("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText60("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText61("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText62("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText63("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText64("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText65("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText66("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText67("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText68("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText69("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText70("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText71("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText72("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText73("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText74("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText75("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText76("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText77("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText78("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText79("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText80("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText81("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText82("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText83("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText84("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText85("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText86("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText87("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText88("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText89("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText90("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText91("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText92("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText93("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText94("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText95("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText96("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText97("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText98("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText99("big text to test flat large object\nfor test that real test performance");
        this.flatLargeObject.setText100("big text to test flat large object\nfor test that real test performance");

    }

    public Object generate(Dataset.ObjectType ot, Dataset.ObjectSize sz) {
        final Object object = this.findJavaObject(sz);

        if (ot == ObjectType.JAVA_CLASS) {
            return object;
        }
        final JsonValue jsonValue = this.mapper.toStructure(object);
        if (ot == ObjectType.JSON) {
            return jsonValue;
        }

        if (ot == ObjectType.RECORD) {
            JsonToRecord toRecord = new JsonToRecord(this.recordBuilderFactory);
            return toRecord.toRecord(jsonValue.asJsonObject());
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
        if (sz == ObjectSize.LARGE_FLAT) {
            return this.flatLargeObject;
        }
        return this.objectSmall; // default.
    }

}
