/*
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
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

package org.talend.components.couchbase.source;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.couchbase.service.CouchbaseService;
import org.talend.components.couchbase.service.I18nMessage;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.*;

import lombok.extern.slf4j.Slf4j;

import static org.talend.sdk.component.api.record.Schema.Type.*;

@Slf4j
@Documentation("This component reads data from Couchbase.")
public class CouchbaseInput implements Serializable {

    private I18nMessage i18n;

    private static final transient Logger LOG = LoggerFactory.getLogger(CouchbaseInput.class);

    private final CouchbaseInputConfiguration configuration;

    private final RecordBuilderFactory builderFactory;

    private CouchbaseService service;

    private transient Schema schema;

    private Set<String> columnsSet;

    private Iterator<N1qlQueryRow> index;

    public CouchbaseInput(@Option("configuration") final CouchbaseInputConfiguration configuration,
            final CouchbaseService service, final RecordBuilderFactory builderFactory, final I18nMessage i18n) {
        this.configuration = configuration;
        this.service = service;
        this.builderFactory = builderFactory;
        this.i18n = i18n;
    }

    @PostConstruct
    public void init() {
        Bucket bucket = null;
        try {
            bucket = service.openConnection(configuration.getDataSet().getDatastore());
        } catch (Exception e) {
            LOG.error(i18n.connectionKO());
        }

        bucket.bucketManager().createN1qlPrimaryIndex(true, false);

        N1qlQueryResult n1qlQueryRows = bucket.query(N1qlQuery.simple("SELECT * FROM " + bucket.name()));
        index = n1qlQueryRows.rows();
    }

    @Producer
    public Record next() {
        if (!index.hasNext()) {
            return null;
        } else {
            // unwrap JsonObject
            JsonObject jsonObject = (JsonObject) index.next().value().get(configuration.getDataSet().getDatastore().getBucket());
            if (schema == null) {
                schema = service.getSchema(jsonObject);
            }
            return createRecord(schema, jsonObject);
        }
    }

    @PreDestroy
    public void release() {
        service.closeConnection();
    }

    private Record createRecord(Schema schema, JsonObject jsonObject) {
        final Record.Builder recordBuilder = builderFactory.newRecordBuilder(schema);
        schema.getEntries().stream().forEach(entry -> addColumn(recordBuilder, entry, getValue(entry.getName(), jsonObject)));
        Record record = recordBuilder.build();
        return record;
    }

    public Object getValue(String currentName, JsonObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        return jsonObject.get(currentName);
    }

    private void addColumn(Record.Builder recordBuilder, final Schema.Entry entry, Object value) {
        final Schema.Entry.Builder entryBuilder = builderFactory.newEntryBuilder();
        Schema.Type type = entry.getType();
        entryBuilder.withName(entry.getName()).withNullable(true).withType(type);

        switch (type) {
        case ARRAY:
            Schema elementSchema = entry.getElementSchema();
            entryBuilder.withElementSchema(elementSchema);
            if (elementSchema.getType() == RECORD) {
                List<Record> recordList = new ArrayList<>();
                // schema of the first element
                Schema currentSchema = elementSchema.getEntries().get(0).getElementSchema();
                for (int i = 0; i < ((JsonArray) value).size(); i++) {
                    JsonObject currentJsonObject = (JsonObject) ((JsonArray) value).get(i);
                    recordList.add(createRecord(currentSchema, currentJsonObject));
                }
                recordBuilder.withArray(entryBuilder.build(), recordList);
            } else {
                recordBuilder.withArray(entryBuilder.build(), ((JsonArray) value).toList());
            }
            break;
        case FLOAT:
            recordBuilder.withFloat(entryBuilder.build(), value == null ? null : (Float) value);
            break;
        case DOUBLE:
            recordBuilder.withDouble(entryBuilder.build(), value == null ? null : (Double) value);
            break;
        case BYTES:
            throw new IllegalArgumentException("BYTES is unsupported");
        case STRING:
            recordBuilder.withString(entryBuilder.build(), value == null ? null : value.toString());
            break;
        case LONG:
            recordBuilder.withLong(entryBuilder.build(), value == null ? null : (Long) value);
            break;
        case INT:
            recordBuilder.withInt(entryBuilder.build(), value == null ? null : (Integer) value);
            break;
        case DATETIME:
            recordBuilder.withDateTime(entryBuilder.build(), value == null ? null : (ZonedDateTime) value);
            break;
        case BOOLEAN:
            recordBuilder.withBoolean(entryBuilder.build(), value == null ? null : (Boolean) value);
            break;
        case RECORD:
            entryBuilder.withElementSchema(entry.getElementSchema());
            recordBuilder.withRecord(entryBuilder.build(), createRecord(entry.getElementSchema(), (JsonObject) value));
            break;
        }
    }
}