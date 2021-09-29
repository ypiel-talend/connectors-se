/*
 * Copyright (C) 2006-2021 Talend Inc. - www.talend.com
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
package org.talend.components.docdb.service;

import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonJavaScript;
import org.bson.Document;
import org.bson.json.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.docdb.datastore.Address;
import org.talend.components.docdb.datastore.Auth;
import org.talend.components.docdb.datastore.ConnectionParameter;
import org.talend.components.docdb.datastore.DocDBDataStore;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Version(1)
@Slf4j
@Service
public class DocDBService {

    private static final transient Logger LOG = LoggerFactory.getLogger(DocDBService.class);

    @Service
    private I18nMessage i18n;

    @Service
    private RecordBuilderFactory builderFactory;

    @Service
    private DocDBConnectionService connectionService;

    @HealthCheck("healthCheck")
    public HealthCheckStatus healthCheck(@Option("configuration.dataset.connection") final DocDBDataStore dataStore) {
        try (MongoClient client = connectionService.createClient(dataStore)) {
            String database = dataStore.getDataBase();

            MongoDatabase md = client.getDatabase(database);
            if (md == null) {// TODO remove it as seems never go in even no that database exists
                return new HealthCheckStatus(HealthCheckStatus.Status.KO, "Can't find the database : " + database);
            }

            Document document = getDatabaseStats(md);
            // TODO use it later

            return new HealthCheckStatus(HealthCheckStatus.Status.OK, "Connection OK");
        } catch (Exception exception) {
            String message = exception.getMessage();
            LOG.error(message, exception);
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, message);
        }
    }

    private Document getDatabaseStats(MongoDatabase database) {
        BsonDocument commandDocument =
                (new BsonDocument("dbStats", new BsonInt32(1))).append("scale", new BsonInt32(1));
        return database.runCommand(commandDocument);
    }

    public void closeClient(MongoClient client) {
        try {
            if (client != null) {
                client.close();
            }
        } catch (Exception e) {
            LOG.warn("Error closing MongoDB client", e);
        }
    }

    public BsonDocument getBsonDocument(String bson) {
        try {
            return Document.parse(bson).toBsonDocument(BasicDBObject.class, MongoClient.getDefaultCodecRegistry());
        } catch (JsonParseException e) {
            Pattern pattern = Pattern.compile("^\\s*\\{\\s*\\$where\\s*:\\s*(function.+)\\}\\s*$", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(bson);
            if (matcher.find()) {
                String result = matcher.group(1);
                return new BsonDocument("$where", new BsonJavaScript(result));
            } else {
                throw e;
            }

        }
    }
}