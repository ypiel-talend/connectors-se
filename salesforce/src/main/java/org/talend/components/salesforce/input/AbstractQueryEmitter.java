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
package org.talend.components.salesforce.input;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.sforce.async.AsyncApiException;
import com.sforce.async.BulkConnection;
import com.sforce.soap.partner.IField;
import com.sforce.ws.ConnectionException;

import org.talend.components.salesforce.commons.BulkResultSet;
import org.talend.components.salesforce.configuration.InputConfig;
import org.talend.components.salesforce.service.BulkQueryService;
import org.talend.components.salesforce.service.Messages;
import org.talend.components.salesforce.service.SalesforceService;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.configuration.LocalConfiguration;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Documentation("Salesforce query input ")
public abstract class AbstractQueryEmitter implements Serializable {

    protected final SalesforceService service;

    protected final InputConfig inputConfig;

    protected final LocalConfiguration localConfiguration;

    private BulkQueryService bulkQueryService;

    private BulkResultSet bulkResultSet;

    private RecordBuilderFactory recordBuilderFactory;

    private Messages messages;

    private boolean preBuildSchema;

    public AbstractQueryEmitter(final InputConfig inputConfig, final SalesforceService service,
            LocalConfiguration configuration,
            final RecordBuilderFactory recordBuilderFactory, final Messages messages) {
        this.service = service;
        this.inputConfig = inputConfig;
        this.localConfiguration = configuration;
        this.recordBuilderFactory = recordBuilderFactory;
        this.messages = messages;
    }

    @PostConstruct
    public void init() {
        try {
            final BulkConnection bulkConnection = service
                    .bulkConnect(inputConfig.getDataSet().getDataStore(),
                            localConfiguration);
            bulkQueryService = new BulkQueryService(bulkConnection, recordBuilderFactory, messages);
            bulkQueryService.doBulkQuery(getModuleName(), getQuery());
        } catch (ConnectionException e) {
            throw service.handleConnectionException(e);
        } catch (AsyncApiException e) {
            throw new IllegalStateException(e.getExceptionMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Producer
    public Record next() {
        try {
            if (!preBuildSchema) {
                this.preBuildSchema = true;
                Map<String, IField> fieldMap = service
                        .getFieldMap(inputConfig.getDataSet().getDataStore(), getModuleName(),
                                localConfiguration);
                bulkQueryService.setFieldMap(fieldMap);
                Schema schema = service.guessSchema(getColumnNames(), fieldMap, recordBuilderFactory);
                bulkQueryService.setRecordSchema(schema);
            }
            if (bulkResultSet == null) {
                bulkResultSet = bulkQueryService.getQueryResultSet(bulkQueryService.nextResultId());
            }
            Map<String, String> currentRecord = bulkResultSet.next();
            if (currentRecord == null) {
                String resultId = bulkQueryService.nextResultId();
                if (resultId != null) {
                    bulkResultSet = bulkQueryService.getQueryResultSet(resultId);
                    currentRecord = bulkResultSet.next();
                }
            }
            return bulkQueryService.convertToRecord(currentRecord);
        } catch (ConnectionException e) {
            throw service.handleConnectionException(e);
        } catch (AsyncApiException e) {
            throw new IllegalStateException(e.getExceptionMessage(), e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @PreDestroy
    public void release() {
        try {
            bulkQueryService.closeJob();
        } catch (AsyncApiException | ConnectionException e) {
            log.error(e.getMessage());
        }
    }

    abstract String getQuery();

    abstract String getModuleName();

    abstract List<String> getColumnNames();

}
