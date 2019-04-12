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

package org.talend.components.couchbase.output;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.couchbase.service.CouchbaseService;
import org.talend.components.couchbase.service.I18nMessage;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Version(1)
@Slf4j
@Icon(value = Icon.IconType.CUSTOM, custom = "CouchbaseOutput")
@Processor(name = "Output")
@Documentation("This component writes data to Couchbase")
public class CouchbaseOutput implements Serializable {

    private I18nMessage i18n;

    private Bucket bucket;

    private String idFieldName;

    private final CouchbaseOutputConfiguration configuration;

    private final CouchbaseService service;

    private static final transient Logger LOG = LoggerFactory.getLogger(CouchbaseOutput.class);

    public CouchbaseOutput(@Option("configuration") final CouchbaseOutputConfiguration configuration,
            final CouchbaseService service, final I18nMessage i18n) {
        this.configuration = configuration;
        this.service = service;
        this.i18n = i18n;
    }

    @PostConstruct
    public void init() {
        try {
            bucket = service.openConnection(configuration.getDataSet().getDatastore());
        } catch (Exception e) {
            LOG.error(i18n.connectionKO());
        }
        idFieldName = configuration.getIdFieldName();
    }

    @ElementListener
    public void onNext(@Input final Record defaultInput) {
        bucket.upsert(toJsonDocument(idFieldName, defaultInput));
    }

    @PreDestroy
    public void release() {
        service.closeConnection();
    }

    private JsonDocument toJsonDocument(String idFieldName, Record record) {
        List<Schema.Entry> entries = record.getSchema().getEntries();
        JsonObject jsonObject = JsonObject.create();
        for (Schema.Entry entry : entries) {
            String entryName = entry.getName();

            Object value = null;

            switch (entry.getType()) {
            case INT:
                value = record.getInt(entryName);
                break;
            case LONG:
                value = record.getLong(entryName);
                break;
            case BYTES:
                value = record.getBytes(entryName);
                break;
            case FLOAT:
                value = record.getFloat(entryName);
                break;
            case DOUBLE:
                value = record.getDouble(entryName);
                break;
            case STRING:
                value = record.getString(entryName);
                break;
            case BOOLEAN:
                value = record.getBoolean(entryName);
                break;
            case ARRAY:
                value = record.getArray(List.class, entryName);
                break;
            case DATETIME:
                value = record.getDateTime(entryName);
                break;
            case RECORD:
                value = record.getRecord(entryName);
                break;
            default:
                throw new IllegalArgumentException("Unknown Type " + entry.getType());
            }

            if (entryName.equals(idFieldName)) {
                value = String.valueOf(value);
            }
            if (value instanceof byte[]) {
                jsonObject.put(entryName, new String((byte[]) value));
                // TODO: decide what to do with byte array
            } else if (value instanceof Float) {
                jsonObject.put(entryName, Double.parseDouble(value.toString()));
            } else if (value instanceof ZonedDateTime) {
                jsonObject.put(entryName, value.toString());
            } else if (value instanceof List) {
                JsonArray jsonArray = JsonArray.from((List<?>) value);
                jsonObject.put(entryName, jsonArray);
            } else {
                jsonObject.put(entryName, value);
            }
        }
        return JsonDocument.create(String.valueOf(jsonObject.get(idFieldName)), jsonObject);
    }
}