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
package org.talend.components.bigquery.output;

import com.google.api.services.bigquery.model.TableReference;
import com.google.cloud.bigquery.*;
import lombok.extern.slf4j.Slf4j;
import org.talend.components.bigquery.datastore.BigQueryConnection;
import org.talend.components.bigquery.service.BigQueryService;
import org.talend.components.bigquery.service.I18nMessage;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.AfterGroup;
import org.talend.sdk.component.api.processor.BeforeGroup;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.talend.sdk.component.api.component.Icon.IconType.BIGQUERY;

@Slf4j
@Version(1)
@Icon(BIGQUERY)
@Processor(name = "BigQueryOutput")
@Documentation("This component writes into BigQuery.")
public class BigQueryOutput implements Serializable {

    @Service
    private I18nMessage i18n;

    private final BigQueryOutputConfig configuration;

    private final BigQueryConnection connection;

    private Schema tableSchema;

    private transient List<Record> records;

    private transient boolean init;

    private transient BigQuery bigQuery;

    private transient TableId tableId;

    private BigQueryService service;

    public BigQueryOutput(@Option("configuration") final BigQueryOutputConfig configuration, BigQueryService bigQueryService) {
        this.configuration = configuration;
        this.connection = configuration.getDataSet().getConnection();
        this.tableSchema = bigQueryService.guessSchema(configuration);
        this.service = bigQueryService;
    }

    @PostConstruct
    public void init() {

    }

    @ElementListener
    public void onElement(Record record) {
        if (!init) {
            lazyInit();
        }
        records.add(record);
    }

    private void lazyInit() {
        init = true;
        bigQuery = service.createClient(connection);
        tableId = TableId.of(connection.getProjectName(), configuration.getDataSet().getBqDataset(),
                configuration.getDataSet().getTableName());
        if (configuration.getTableOperation() == BigQueryOutputConfig.TableOperation.CREATE_IF_NOT_EXISTS) {

            try {
                Table table = bigQuery.getTable(tableId);
                if (table == null) {
                    log.info(i18n.infoTableNoExists(), configuration.getDataSet().getTableName());
                    TableInfo tableInfo = TableInfo.newBuilder(tableId, StandardTableDefinition.of(tableSchema)).build();
                    table = bigQuery.create(tableInfo);
                    log.info(i18n.infoTableCreated(), tableId.getTable());
                }
            } catch (BigQueryException e) {
                log.error(i18n.errorCreationTable() + e.getMessage(), e);
            }
        }
    }

    @BeforeGroup
    public void beforeGroup() {
        records = new ArrayList<>();
    }

    @AfterGroup
    public void afterGroup() {
        TacoKitRecordToTableRowConverter converter = new TacoKitRecordToTableRowConverter(tableSchema, i18n);
        InsertAllRequest.Builder insertAllRequestBuilder = InsertAllRequest.newBuilder(tableId);
        records.stream().map(converter::apply).forEach(insertAllRequestBuilder::addRow);
        InsertAllResponse response = bigQuery.insertAll(insertAllRequestBuilder.build());

        if (response.hasErrors()) {
            // response.getInsertErrors();
            // rejected no handled by TCK
            log.warn(i18n.warnRejected(), response.getInsertErrors().size());
        }
    }

    private TableReference createTableReference() {
        TableReference table = new TableReference();
        table.setProjectId(connection.getProjectName());
        table.setDatasetId(configuration.getDataSet().getBqDataset());
        table.setTableId(configuration.getDataSet().getTableName());
        return table;
    }

}
