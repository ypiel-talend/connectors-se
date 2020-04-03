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
package org.talend.components.jsonconn.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.talend.sdk.component.api.service.Service;

import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import java.io.StringReader;

@Slf4j
@Service
@Data
public class JsonService {

    @Service
    private JsonReaderFactory jsonReaderFactory;

    public JsonObject toJsonObject(final String json) {
        try (final JsonReader reader = jsonReaderFactory.createReader(new StringReader(json))) {
            return reader.read().asJsonObject();
        } catch (Exception e) {
            log.info("JSON parsing failed.", e);
        }
        return null;
    }

}
