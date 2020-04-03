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
package org.talend.components.jsonconn.source;

import lombok.extern.slf4j.Slf4j;
import org.talend.components.jsonconn.conf.Config;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import java.io.Serializable;
import java.util.Arrays;

@Slf4j
@Version(1)
@Icon(Icon.IconType.STAR)
@Emitter(name = "recordInput")
@Documentation("")
public class RecordEmitter implements Serializable {

    private final Config config;

    private final RecordBuilderFactory recordBuilderFactory;

    private transient boolean done = false;

    public RecordEmitter(@Option("configuration") final Config config, final RecordBuilderFactory recordBuilderFactory) {
        this.recordBuilderFactory = recordBuilderFactory;
        this.config = config;
    }

    @Producer
    public Record next() {
        if (done) {
            return null;
        }
        done = true;

        final Schema.Entry age = this.recordBuilderFactory.newEntryBuilder().withName("age").withType(Schema.Type.INT).build();
        final Schema.Entry name = this.recordBuilderFactory.newEntryBuilder().withName("name").withType(Schema.Type.STRING).build();
        final Schema.Entry addresse = this.recordBuilderFactory.newEntryBuilder().withName("addresse").withType(Schema.Type.STRING).withNullable(true).build();
        final Schema record = recordBuilderFactory.newSchemaBuilder(Schema.Type.RECORD)
                .withEntry(name).withEntry(age).withEntry(addresse).build();

        final Schema.Entry array = this.recordBuilderFactory.newEntryBuilder().withName("data").withType(Schema.Type.ARRAY).withElementSchema(record).build();


        final Schema.Entry bool = this.recordBuilderFactory.newEntryBuilder().withName("bool").withType(Schema.Type.BOOLEAN).build();
        final Schema schema = recordBuilderFactory.newSchemaBuilder(Schema.Type.RECORD)
                .withEntry(bool)
                .withEntry(array).build();

        final Record rec1 = this.recordBuilderFactory.newRecordBuilder().withString("name", "Yves").withString("addresse", null).withInt("age", 456).build();
        final Record full = this.recordBuilderFactory.newRecordBuilder().withArray(array, Arrays.asList(rec1, rec1, rec1)).withBoolean("bool", false).build();

        return full;
    }

}
