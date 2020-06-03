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

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonObject;
import javax.json.JsonValue;

import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.record.Schema.Type;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

public class RecordGuessWork {

    private final Map<String, EntryGuessWork> guessFields = new HashMap<>();

    private boolean firstObject = true;

    public void add(JsonObject json) {
        for (Map.Entry<String, JsonValue> e : json.entrySet()) {
            final EntryGuessWork entryGuess = this.guessFields.computeIfAbsent(e.getKey(), this::createEntry);
            entryGuess.add(e.getValue());
        }
        this.firstObject = false;
        this.guessFields.values().stream().filter((EntryGuessWork e) -> !e.isNullable())
                .filter((EntryGuessWork e) -> !json.containsKey(e.getName())).forEach((EntryGuessWork e) -> e.setNullable(true));
    }

    private EntryGuessWork createEntry(String name) {
        final EntryGuessWork e = new EntryGuessWork(name);
        if (!this.firstObject) {
            e.setNullable(true);
        }
        return e;
    }

    public Schema generateSchema(RecordBuilderFactory factory) {
        final Schema.Builder builder = factory.newSchemaBuilder(Type.RECORD);
        this.completeBuilder(factory, builder);
        return builder.build();
    }

    void completeBuilder(RecordBuilderFactory factory, Schema.Builder builder) {
        guessFields.values().stream().map((EntryGuessWork e) -> e.generate(factory)).forEach(builder::withEntry);
    }

}
