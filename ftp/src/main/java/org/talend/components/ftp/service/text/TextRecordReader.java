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
package org.talend.components.ftp.service.text;

import lombok.extern.slf4j.Slf4j;
import org.talend.components.common.stream.api.input.RecordReader;
import org.talend.components.ftp.dataset.TextConfiguration;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Iterator;

@Slf4j
public class TextRecordReader implements RecordReader {

    private final TextConfiguration textConfiguration;

    private final RecordBuilderFactory recordBuilderFactory;

    private final Schema schema;

    public TextRecordReader(TextConfiguration textConfiguration, RecordBuilderFactory recordBuilderFactory) {
        this.recordBuilderFactory = recordBuilderFactory;
        this.textConfiguration = textConfiguration;
        schema = recordBuilderFactory.newSchemaBuilder(Schema.Type.RECORD)
                .withEntry(recordBuilderFactory.newEntryBuilder().withType(Schema.Type.STRING).withName("content").build())
                .build();
    }

    @Override
    public Iterator<Record> read(InputStream reader) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(reader, textConfiguration.getEncoding()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\r\n");
            }

            return Collections
                    .singletonList(recordBuilderFactory.newRecordBuilder(schema).withString("content", sb.toString()).build())
                    .iterator();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void close() {

    }
}
