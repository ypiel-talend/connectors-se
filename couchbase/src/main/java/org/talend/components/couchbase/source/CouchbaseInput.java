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
import com.couchbase.client.java.Cluster;
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

    private Bucket bucket;

    public CouchbaseInput(@Option("configuration") final CouchbaseInputConfiguration configuration,
            final CouchbaseService service, final RecordBuilderFactory builderFactory, final I18nMessage i18n) {
        this.configuration = configuration;
        this.service = service;
        this.builderFactory = builderFactory;
        this.i18n = i18n;
    }

    @PostConstruct
    public void init() {
        Cluster cluster = service.openConnection(configuration.getDataSet().getDatastore());
        bucket = service.openBucket(cluster, configuration.getDataSet().getBucket());
        bucket.bucketManager().createN1qlPrimaryIndex(true, false);

        columnsSet = new HashSet<>();

        N1qlQueryResult n1qlQueryRows;
        if (configuration.isUseN1QLQuery()) {
            n1qlQueryRows = bucket.query(N1qlQuery.simple(configuration.getQuery()));
        } else {
            n1qlQueryRows = bucket.query(N1qlQuery.simple("SELECT * FROM `" + bucket.name() + "`" + getLimit()));
        }
        checkErrors(n1qlQueryRows);
        index = n1qlQueryRows.rows();
    }

    private String getLimit() {
        if (configuration.getLimit().isEmpty()) {
            return "";
        } else {
            return " LIMIT " + configuration.getLimit().trim();
        }
    }

    private void checkErrors(N1qlQueryResult n1qlQueryRows) {
        if (!n1qlQueryRows.errors().isEmpty()) {
            LOG.error(i18n.queryResultError());
            throw new IllegalArgumentException(n1qlQueryRows.errors().toString());
        }
    }

    @Producer
    public Record next() {
        if (!index.hasNext()) {
            return null;
        } else {
            JsonObject jsonObject = index.next().value();

            if (!configuration.isUseN1QLQuery() || service.isResultNeedWrapper(configuration.getQuery())) {
                // unwrap JSON (because we use SELECT * all values will be wrapped with bucket name)
                jsonObject = (JsonObject) jsonObject.get(configuration.getDataSet().getBucket());
            }

            if (columnsSet.isEmpty() && configuration.getDataSet().getSchema() != null
                    && !configuration.getDataSet().getSchema().isEmpty()) {
                columnsSet.addAll(configuration.getDataSet().getSchema());
            }
            if (schema == null) {
                schema = service.defineSchema(jsonObject, columnsSet);
            }
            final Record.Builder recordBuilder = builderFactory.newRecordBuilder(schema);
            JsonObject finalJsonObject = jsonObject;
            schema.getEntries().stream()
                    .forEach(entry -> addColumn(recordBuilder, entry, service.getJsonValue(entry.getName(), finalJsonObject)));

            return recordBuilder.build();
        }
    }

    @PreDestroy
    public void release() {
        service.closeBucket(bucket);
        service.closeConnection();
    }

    private void addColumn(Record.Builder recordBuilder, final Schema.Entry entry, Object value) {
        final Schema.Entry.Builder entryBuilder = builderFactory.newEntryBuilder();
        Schema.Type type = entry.getType();
        entryBuilder.withName(entry.getName()).withNullable(true).withType(type);

        switch (type) {
        case ARRAY:
            List<?> listValue = ((JsonArray) value).toList();
            entryBuilder.withElementSchema(entry.getElementSchema());
            recordBuilder.withArray(entryBuilder.build(), listValue);
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
        case RECORD:
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
        }
    }
}