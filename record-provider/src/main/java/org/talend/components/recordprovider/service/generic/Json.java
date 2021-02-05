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
package org.talend.components.recordprovider.service.generic;

import org.talend.components.common.stream.input.json.JsonToRecord;
import org.talend.components.recordprovider.conf.CodingConfig;
import org.talend.components.recordprovider.service.AbstractProvider;

import javax.json.JsonObject;
import javax.json.JsonPointer;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Json extends AbstractProvider {

    @Override
    public List<Object> get(CodingConfig config) {

        final JsonReader reader = this.getJsonReaderFactory().createReader(new StringReader(config.getJson()));

        String path = config.getJsonPointer();
        if (path == null || "/".equals(path)) {
            path = "";
        }

        final JsonPointer pointer = this.getJsonProvider().createPointer(path);

        final JsonStructure structure = reader.read();
        final JsonValue value = pointer.getValue(structure);

        final List<Object> records = new ArrayList<>();
        if (value.getValueType() == JsonValue.ValueType.ARRAY) {
            final Object[] values = value.asJsonArray().toArray();
            records.addAll(Arrays.asList(values));
        } else if (value.getValueType() == JsonValue.ValueType.OBJECT) {
            records.add(value);
        } else {
            final JsonObject object = this.getJsonBuilderFactory().createObjectBuilder().add("value", value).build();
            records.add(object);
        }

        if (config.isCommonio()) {
            final JsonToRecord converter = new JsonToRecord(this.getRecordBuilderFactory());
            final List<Object> tckRecords = records.stream().map(o -> converter.toRecord((JsonObject) o))
                    .collect(Collectors.toList());
            return tckRecords;
        }

        return records;
    }

}
