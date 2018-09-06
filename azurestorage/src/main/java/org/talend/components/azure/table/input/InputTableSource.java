package org.talend.components.azure.table.input;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.DynamicTableEntity;
import com.microsoft.azure.storage.table.EntityProperty;
import com.microsoft.azure.storage.table.TableQuery;
import org.apache.commons.lang3.StringUtils;
import org.talend.components.azure.service.AzureConnectionService;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.service.schema.Type;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Documentation("TODO fill the documentation for this source")
public class InputTableSource implements Serializable {
    private final InputTableMapperConfiguration configuration;
    private final AzureConnectionService service;
    private final transient JsonBuilderFactory jsonBuilderFactory;

    private String filter;
    private transient CloudStorageAccount connection;

    private transient Iterator<DynamicTableEntity> recordsIterator;

    private DynamicTableEntity current;

    public InputTableSource(@Option("configuration") final InputTableMapperConfiguration configuration,
                        final AzureConnectionService service,
                        final JsonBuilderFactory jsonBuilderFactory) {
        this.configuration = configuration;
        this.service = service;
        this.jsonBuilderFactory = jsonBuilderFactory;
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
            Iterable<DynamicTableEntity> entities = service.executeQuery(connection, configuration.getAzureConnection().getTableName(), partitionQuery);
            recordsIterator = entities.iterator();
            if (recordsIterator.hasNext()) {
                current = recordsIterator.next();
            }
        } catch (URISyntaxException | StorageException e) {
            throw new RuntimeException("Can't retrieve table content", e);
        }
    }

    @Producer
    public JsonObject next() {
        JsonObject currentRecord = null;
        if (current != null) {
            JsonObjectBuilder currentRecordBuilder = jsonBuilderFactory.createObjectBuilder().add("PartitionKey", current.getPartitionKey()).add("RowKey", current.getRowKey())
                    .add("Timestamp", current.getTimestamp().toString());
            for (Map.Entry<String, EntityProperty> pair : current.getProperties().entrySet()) {
                String columnName = pair.getKey();
                EntityProperty columnValue = pair.getValue();
                if (configuration.getSchema().contains(columnName)) {
                    addValue(currentRecordBuilder, columnName, columnValue);
                }
            }
            currentRecord = currentRecordBuilder.build();
        }
        if (recordsIterator.hasNext()) {
            current = recordsIterator.next();
        } else {
            current = null;
        }
        return currentRecord;
    }

    private void addValue(JsonObjectBuilder currentRecordBuilder, String columnName, EntityProperty columnValue) {
        switch (columnValue.getEdmType()) {
        case BOOLEAN:
            currentRecordBuilder.add(columnName, columnValue.getValueAsBoolean());
            break;
        case BYTE:
        case SBYTE:
        case INT16:
        case INT32:
            currentRecordBuilder.add(columnName, columnValue.getValueAsInteger());
            break;
        case INT64:
        case DECIMAL:
        case SINGLE:
        case DOUBLE:
            currentRecordBuilder.add(columnName, columnValue.getValueAsDouble());
            break;
        default:
            currentRecordBuilder.add(columnName, columnValue.getValueAsString());
        }
    }

    @PreDestroy
    public void release() {
        //NOOP
    }
}