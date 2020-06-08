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

import javax.json.JsonObject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.talend.components.bench.config.Dataset;
import org.talend.components.bench.config.Dataset.ObjectSize;
import org.talend.components.bench.config.Dataset.ObjectType;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.junit5.WithComponents;

import static org.junit.jupiter.api.Assertions.*;

@WithComponents("org.talend.components.bench")
class GenericServiceTest {

    @Service
    private GenericService service;

    @Test
    void generate() {

        for (ObjectSize oz : ObjectSize.values()) {
            final Object record = service.generate(ObjectType.RECORD, oz);
            Assertions.assertNotNull(record, "null record with " + oz.name());
            Assertions.assertTrue(record instanceof Record);

            final Object object = service.generate(ObjectType.JAVA_CLASS, oz);
            Assertions.assertNotNull(object, "null java with " + oz.name());

            final Object json = service.generate(ObjectType.JSON, oz);
            Assertions.assertNotNull(json, "null json with " + oz.name());
            Assertions.assertTrue(json instanceof JsonObject, "type " + json.getClass().getName() + " for " + oz.name());
        }
    }
}