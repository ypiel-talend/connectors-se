package org.talend.components.azure.table.input;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.talend.components.azure.service.AzureConnectionService;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.DynamicTableEntity;
import com.microsoft.azure.storage.table.EntityProperty;
import com.microsoft.azure.storage.table.TableQuery;

@Documentation("TODO fill the documentation for this source")
public class InputTableSource implements Serializable {

    private final InputTableMapperConfiguration configuration;

    private final AzureConnectionService service;

    private String filter;

    private transient CloudStorageAccount connection;

    private transient Iterator<DynamicTableEntity> recordsIterator;

    private DynamicTableEntity current;

    private RecordBuilderFactory recordBuilderFactory;

    public InputTableSource(@Option("configuration") final InputTableMapperConfiguration configuration,
            final AzureConnectionService service, final RecordBuilderFactory recordBuilderFactory) {
        this.configuration = configuration;
        this.service = service;
        this.recordBuilderFactory = recordBuilderFactory;
    }

    @PostConstruct
    public void init() {
        try {
            connection = service.createStorageAccount(configuration.getAzureConnection().getConnection());
            if (configuration.isUseFilterExpression()) {
                filter = configuration.generateCombinedFilterConditions();
            }
            executeSelect();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Can't establish connection", e);
        }
    }

    private void executeSelect() {
        try {
            TableQuery<DynamicTableEntity> partitionQuery;
            if (StringUtils.isEmpty(filter)) {
                partitionQuery = TableQuery.from(DynamicTableEntity.class);
            } else {
                partitionQuery = TableQuery.from(DynamicTableEntity.class).where(filter);
            }
            // Using execute will automatically and lazily follow the continuation tokens from page to page of results.
            // So, we bypass the 1000 entities limit.
            Iterable<DynamicTableEntity> entities = service.executeQuery(connection,
                    configuration.getAzureConnection().getTableName(), partitionQuery);
            recordsIterator = entities.iterator();
            if (recordsIterator.hasNext()) {
                current = recordsIterator.next();
            }
        } catch (URISyntaxException | StorageException e) {
            throw new RuntimeException("Can't retrieve table content", e);
        }
    }

    @Producer
    public Record next() {
        Record currentRecord = null;
        Record.Builder builder = recordBuilderFactory.newRecordBuilder();
        if (current != null) {
            builder.withString("PartitionKey", current.getPartitionKey()).withString("RowKey", current.getRowKey())
                    .withDateTime("Timestamp", current.getTimestamp());
            for (Map.Entry<String, EntityProperty> pair : current.getProperties().entrySet()) {
                String columnName = pair.getKey();
                EntityProperty columnValue = pair.getValue();
                if (configuration.getSchema().contains(columnName)) {
                    addValue(builder, columnName, columnValue);
                }
            }
            currentRecord = builder.build();
        }
        if (recordsIterator.hasNext()) {
            current = recordsIterator.next();
        } else {
            current = null;
        }
        // HOW???
        recordBuilderFactory.newRecordBuilder();
        return currentRecord;
    }

    private void addValue(Record.Builder currentRecordBuilder, String columnName, EntityProperty columnValue) {
        switch (columnValue.getEdmType()) {
        case BOOLEAN:
            currentRecordBuilder.withBoolean(columnName, columnValue.getValueAsBoolean());
            break;
        case BYTE:
        case SBYTE:
        case INT16:
        case INT32:
            currentRecordBuilder.withInt(columnName, columnValue.getValueAsInteger());
            break;
        case INT64:
            currentRecordBuilder.withLong(columnName, columnValue.getValueAsInteger());
            break;
        case DECIMAL:
        case SINGLE:
        case DOUBLE:
            currentRecordBuilder.withDouble(columnName, columnValue.getValueAsDouble());
            break;
        case TIME:
        case DATE_TIME:
            currentRecordBuilder.withDateTime(columnName, columnValue.getValueAsDate());
        default:
            currentRecordBuilder.withString(columnName, columnValue.getValueAsString());
        }
    }

    @PreDestroy
    public void release() {
        // NOOP
    }
}