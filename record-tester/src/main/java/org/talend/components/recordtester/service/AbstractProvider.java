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
package org.talend.components.recordtester.service;

import lombok.Data;
import org.talend.components.recordtester.service.RecordProvider;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.spi.JsonProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;

@Data
public abstract class AbstractProvider implements RecordProvider {

    private RecordBuilderFactory recordBuilderFactory;

    private JsonReaderFactory jsonReaderFactory;

    private JsonBuilderFactory jsonBuilderFactory;

    private JsonProvider jsonProvider;

    private Map<Class, Object> services;

    @Override
    public void setServices(Map<Class, Object> services) {
        this.services = services;
        this.recordBuilderFactory = (RecordBuilderFactory) services.get(RecordBuilderFactory.class);
        this.jsonReaderFactory = (JsonReaderFactory) services.get(JsonReaderFactory.class);
        this.jsonBuilderFactory = (JsonBuilderFactory) services.get(JsonBuilderFactory.class);
        this.jsonProvider = (JsonProvider) services.get(JsonProvider.class);
    }

    public Schema.Entry newArrayEntry(String name, Schema nested) {
        return recordBuilderFactory.newEntryBuilder().withType(Schema.Type.ARRAY).withName(name).withNullable(true)
                .withElementSchema(nested).build();
    }

    public Schema.Entry newEntry(Schema.Type type, String name) {
        return recordBuilderFactory.newEntryBuilder().withName(name).withType(type).withNullable(true).build();
    }

    protected JsonObject loadJsonFile(String fileName) {
        final String json = getFileAsString(fileName);

        try (final JsonReader reader = jsonReaderFactory.createReader(new StringReader(json))) {
            return reader.read().asJsonObject();
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON parsing failed.", e);
        }
    }

    private String getFileAsString(String fileName) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

        final StringBuilder sb = new StringBuilder();
        try {
            byte[] buffer = new byte[512];
            int n = 0;
            while (n >= 0) {
                n = inputStream.read(buffer);
                if (n >= 0) {
                    sb.append(new String(Arrays.copyOf(buffer, n), Charset.defaultCharset()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
