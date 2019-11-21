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
package org.talend.components.bigquery.service;

import com.google.api.services.bigquery.BigqueryScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.*;
import com.google.cloud.bigquery.BigQuery.DatasetListOption;
import com.google.cloud.bigquery.BigQuery.TableField;
import com.google.cloud.bigquery.BigQuery.TableOption;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.talend.components.bigquery.datastore.BigQueryConnection;
import org.talend.components.bigquery.output.BigQueryOutputConfig;
import org.talend.components.bigquery.output.BigQueryOutputConfig.TableOperation;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Suggestions;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.stream.StreamSupport;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class BigQueryService {

    public static final String ACTION_SUGGESTION_DATASET = "BigQueryDataSet";

    public static final String ACTION_SUGGESTION_TABLES = "BigQueryTables";

    public static final String ACTION_HEALTH_CHECK = "HEALTH_CHECK";

    private static final String FIELDS = "\"fields\"";

    private static final String SUB_FIELDS = "\"subFields\"";

    @Service
    private RecordBuilderFactory recordBuilderFactoryService;

    @Service
    private I18nMessage i18n;

    public RecordBuilderFactory getRecordBuilderFactoryService() {
        return recordBuilderFactoryService;
    }

    @HealthCheck(ACTION_HEALTH_CHECK)
    public HealthCheckStatus healthCheck(@Option BigQueryConnection connection) {
        try {
            BigQuery client = createClient(connection);
            client.listDatasets(DatasetListOption.pageSize(1));
        } catch (final Exception e) {
            log.error("[HealthCheckStatus] {}", e.getMessage());
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, e.getMessage());
        }
        return new HealthCheckStatus(HealthCheckStatus.Status.OK, i18n.successConnection());
    }

    @Suggestions(ACTION_SUGGESTION_DATASET)
    public SuggestionValues findDataSets(@Option("connection") final BigQueryConnection connection) {
        final BigQuery client = createClient(connection);
        return new SuggestionValues(true,
                StreamSupport
                        .stream(client.listDatasets(connection.getProjectName(), DatasetListOption.pageSize(100)).getValues()
                                .spliterator(), false)
                        .map(dataset -> new SuggestionValues.Item(dataset.getDatasetId().getDataset(),
                                ofNullable(dataset.getFriendlyName()).orElseGet(() -> dataset.getDatasetId().getDataset())))
                        .collect(toList()));
    }

    @Suggestions(ACTION_SUGGESTION_TABLES)
    public SuggestionValues findTables(@Option("connection") final BigQueryConnection connection,
            @Option("bqDataset") final String bqDataset) {
        final BigQuery client = createClient(connection);
        return new SuggestionValues(true, StreamSupport
                .stream(client
                        .listTables(DatasetId.of(connection.getProjectName(), bqDataset), BigQuery.TableListOption.pageSize(100))
                        .getValues().spliterator(), false)
                .map(table -> new SuggestionValues.Item(table.getTableId().getTable(),
                        ofNullable(table.getFriendlyName()).orElseGet(() -> table.getTableId().getTable())))
                .collect(toList()));
    }

    public BigQuery createClient(final BigQueryConnection connection) {

        BigQuery client = null;

        if (connection.getJsonCredentials() != null && !"".equals(connection.getJsonCredentials().trim())) {
            GoogleCredentials credentials = getCredentials(connection.getJsonCredentials());

            client = BigQueryOptions.newBuilder().setCredentials(credentials).setProjectId(connection.getProjectName()).build()
                    .getService();
        } else {
            client = BigQueryOptions.getDefaultInstance().getService();
        }

        return client;
    }

    public com.google.cloud.bigquery.Schema guessSchema(BigQueryOutputConfig configuration) {
        BigQuery client = createClient(configuration.getDataSet().getConnection());
        Table table = client.getTable(configuration.getDataSet().getBqDataset(), configuration.getDataSet().getTableName(),
                TableOption.fields(TableField.SCHEMA));
        if (table != null) {
            return table.getDefinition().getSchema();
        } else {
            if (configuration.getTableOperation() == TableOperation.CREATE_IF_NOT_EXISTS
                    && StringUtils.isNotBlank(configuration.getTableSchemaFields())) {
                String test = configuration.getTableSchemaFields().replaceAll("\\s", StringUtils.EMPTY);
                // Cut first \"fields\" occurrence and cut last 2 symbols.
                String inputSchema = test.replaceFirst(FIELDS, StringUtils.EMPTY);
                inputSchema = inputSchema.substring(0, inputSchema.length() - 1).substring(2);
                Field[] fields = createGsonBuilder().fromJson(inputSchema.replaceAll(FIELDS, SUB_FIELDS), Field[].class);
                return com.google.cloud.bigquery.Schema.of(fields);
            } else {
                throw new RuntimeException(i18n.schemaNotDefined());
            }
        }
    }

    private Gson createGsonBuilder() {
        JsonDeserializer<LegacySQLTypeName> typeDeserializer = (jsonElement, type, deserializationContext) -> {
            return LegacySQLTypeName.valueOf(jsonElement.getAsString());
        };

        JsonDeserializer<FieldList> subFieldsDeserializer = (jsonElement, type, deserializationContext) -> {
            Field[] fields = deserializationContext.deserialize(jsonElement.getAsJsonArray(), Field[].class);
            return FieldList.of(fields);
        };

        return new GsonBuilder().registerTypeAdapter(LegacySQLTypeName.class, typeDeserializer)
                .registerTypeAdapter(FieldList.class, subFieldsDeserializer).setLenient().create();
    }

    public static GoogleCredentials getCredentials(String credentials) {
        try {
            return GoogleCredentials.fromStream(new ByteArrayInputStream(credentials.getBytes()))
                    .createScoped(BigqueryScopes.all());
        } catch (IOException e) {
            throw new RuntimeException(
                    "Exception when read service account file: " + credentials + "\nMessage is:" + e.getMessage());
        }
    }

}
