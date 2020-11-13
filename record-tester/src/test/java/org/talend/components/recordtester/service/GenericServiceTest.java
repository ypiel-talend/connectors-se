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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.talend.components.common.stream.input.json.JsonToRecord;
import org.talend.components.recordtester.conf.CodingConfig;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.junit5.WithComponents;

import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@WithComponents(value = "org.talend.components.recordtester")
class GenericServiceTest {

    @Service
    GenericService service;

    @Service
    RecordBuilderFactory recordBuilderFactory;

    @Service
    JsonReaderFactory jsonReaderFactory;

    @Test
    void getListNames() {
        final CodingConfig codingConfig = new CodingConfig();
        final SuggestionValues listFiles = service.getListFiles();
        assertEquals(4, listFiles.getItems().size());
    }

    @Test
    void dumpSchema() {
        final InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("fd.json");
        final JsonReader reader = jsonReaderFactory.createReader(resourceAsStream, Charset.forName("utf-8"));
        final JsonToRecord jsonToRecord = new JsonToRecord(recordBuilderFactory);
        final Record record = jsonToRecord.toRecord(reader.readObject());
        final Schema schema = record.getSchema();

        final String dump = service.dumpSchema(schema);

        assertEquals("RECORD\n" + "  menu : RECORD(nullable : false)\n" + "  RECORD\n" + "    id : STRING(nullable : true)\n"
                + "    value : STRING(nullable : true)\n" + "    recA : RECORD(nullable : false)\n" + "    RECORD\n"
                + "      attr1 : STRING(nullable : true)\n" + "      attr2 : LONG(nullable : false)\n"
                + "      recB : RECORD(nullable : false)\n" + "      RECORD\n" + "        attr3 : BOOLEAN(nullable : false)\n"
                + "        attr4 : DOUBLE(nullable : false)\n" + "    popup : RECORD(nullable : false)\n" + "    RECORD\n"
                + "      menuitem : ARRAY(nullable : false)\n" + "      RECORD\n" + "        value : STRING(nullable : true)\n"
                + "        onclick : STRING(nullable : true)\n" + "  arrayOfArray : ARRAY(nullable : false)\n" + "  ARRAY\n"
                + "    RECORD\n" + "      aaa : STRING(nullable : true)\n" + "      bbb : DOUBLE(nullable : false)\n", dump);
    }

}
