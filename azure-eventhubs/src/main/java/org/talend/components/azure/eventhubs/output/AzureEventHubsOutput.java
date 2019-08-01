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
 *
 */

package org.talend.components.azure.eventhubs.output;

import static org.talend.components.azure.eventhubs.common.AzureEventHubsConstant.DEFAULT_CHARSET;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collector;

import com.microsoft.azure.eventhubs.BatchOptions;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventDataBatch;
import com.microsoft.azure.eventhubs.EventHubException;
import org.talend.components.azure.eventhubs.service.ClientService;
import org.talend.components.azure.eventhubs.service.Messages;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.AfterGroup;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Version
@RequiredArgsConstructor
@Icon(Icon.IconType.DEFAULT)
@Processor(name = "AzureEventHubsOutput")
@Documentation("AzureEventHubs output")
public class AzureEventHubsOutput implements Serializable {
    private final AzureEventHubsOutputConfiguration configuration;
    private final ClientService clientService;
    private final Messages messages;

    @AfterGroup
    public void onRecords(final List<Record> records) {
        if (records.isEmpty()) {
            return;
        }
        try (final ClientService.AzClient client = clientService.create(configuration.getDataset(), 1)) {
            final BatchOptions options = new BatchOptions();
            if (AzureEventHubsOutputConfiguration.PartitionType.COLUMN.equals(configuration.getPartitionType())) {
                options.partitionKey = configuration.getKeyColumn();
            }
            final EventDataBatch events = records.stream()
                    .map(this::recordToCsvByteArray)
                    .map(EventData::create)
                    .collect(Collector.of(
                            () -> {
                                try {
                                    return client.unwrap().createBatch(options);
                                } catch (final EventHubException e) {
                                    throw new IllegalStateException(e);
                                }
                            },
                            (b, it) -> {
                                try {
                                    b.tryAdd(it); // todo: what in case of error?
                                } catch (final EventHubException e) {
                                    throw new IllegalStateException(e);
                                }
                            },
                            (b1, b2) -> {
                                throw new IllegalStateException("merge not supported");
                            }));
            if (AzureEventHubsOutputConfiguration.PartitionType.SPECIFY_PARTITION_ID.equals(configuration.getPartitionType())) {
                client.unwrap().createPartitionSenderSync(configuration.getPartitionId()).sendSync(events);
            } else {
                client.unwrap().sendSync(events);
            }
        } catch (final Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private byte[] recordToCsvByteArray(Record record) {
        // TODO make delimited configurable
        StringBuilder sb = new StringBuilder();
        Schema schema = record.getSchema();
        boolean isFirstColumn = true;
        for (Schema.Entry field : schema.getEntries()) {
            if (!isFirstColumn) {
                sb.append(";");
            } else {
                isFirstColumn = false;
            }
            sb.append(getStringValue(record, field));

        }
        byte[] bytes = sb.toString().getBytes(DEFAULT_CHARSET);
        sb.setLength(0);
        return bytes;
    }

    private String getStringValue(Record record, Schema.Entry field) {
        Object value = null;
        switch (field.getType()) {
        case STRING:
            value = record.getString(field.getName());
            break;
        case BOOLEAN:
            if (record.getOptionalBoolean(field.getName()).isPresent()) {
                value = record.getBoolean(field.getName());
            }
            break;
        case DOUBLE:
            if (record.getOptionalDouble(field.getName()).isPresent()) {
                value = record.getDouble(field.getName());
            }
            break;
        case FLOAT:
            if (record.getOptionalFloat(field.getName()).isPresent()) {
                value = record.getFloat(field.getName());
            }
            break;
        case LONG:
            if (record.getOptionalLong(field.getName()).isPresent()) {
                value = record.getLong(field.getName());
            }
            break;
        case INT:
            if (record.getOptionalInt(field.getName()).isPresent()) {
                value = record.getInt(field.getName());
            }
            break;
        case DATETIME:
            value = record.getDateTime(field.getName());
            break;
        case BYTES:
            if (record.getOptionalBytes(field.getName()).isPresent()) {
                value = new String(record.getBytes(field.getName()), DEFAULT_CHARSET);
            }
            break;
        default:
            throw new IllegalStateException(messages.errorUnsupportedType(field.getType().name(), field.getName()));
        }
        return value == null ? "" : String.valueOf(value);

    }
}