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
package org.talend.components.common.schema;

import javax.json.JsonObject;
import javax.json.JsonValue;

import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.record.Schema.Builder;
import org.talend.sdk.component.api.record.Schema.Type;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

public class ArrayGuessWork {

    private RecordGuessWork record = null;

    private final EntryGuessWork entryGuess = new EntryGuessWork("");

    public void add(JsonValue value) {
        if (value == null) {
            return;
        }
        if (value instanceof JsonObject) {
            if (this.record == null) {
                this.record = new RecordGuessWork();
            }
            this.record.add(value.asJsonObject());
        } else {
            this.entryGuess.add(value);
        }
    }

    public Schema generateSchema(RecordBuilderFactory factory) {
        if (this.record != null) {
            return this.record.generateSchema(factory);
        }
        final Schema.Entry entry = this.entryGuess.generate(factory);
        final Builder schemaBuilder = factory.newSchemaBuilder(entry.getType());
        if (entry.getType() == Type.ARRAY) {
            schemaBuilder.withElementSchema(entry.getElementSchema());
        }
        return schemaBuilder.build();
    }

}
