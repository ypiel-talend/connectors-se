/*
 * Copyright (C) 2006-2022 Talend Inc. - www.talend.com
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
package org.talend.components.adlsgen2.runtime.formatter;

import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import java.util.List;

import org.talend.components.common.stream.output.json.RecordToJson;
import org.talend.sdk.component.api.record.Record;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonContentFormatter extends AbstractContentFormatter {

    private final RecordToJson converter;

    private final JsonBuilderFactory jsonBuilderFactory;

    public JsonContentFormatter(final JsonBuilderFactory jsonBuilderFactory) {
        this.jsonBuilderFactory = jsonBuilderFactory;
        converter = new RecordToJson();
    }

    @Override
    public byte[] feedContent(List<Record> records) {
        JsonArrayBuilder b = jsonBuilderFactory.createArrayBuilder();
        for (Record record : records) {

            b.add(converter.fromRecord(record));
        }
        return b.build().toString().getBytes();
    }

    @Override
    public boolean hasHeader() {
        return true;
    }

    @Override
    public boolean hasFooter() {
        return true;
    }

    @Override
    public byte[] initializeContent() {
        return "[".getBytes();
    }

    @Override
    public byte[] finalizeContent() {
        return "]".getBytes();
    }
}
