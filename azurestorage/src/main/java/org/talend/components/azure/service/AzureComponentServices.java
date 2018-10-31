package org.talend.components.azure.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.talend.components.azure.common.AzureConnection;
import org.talend.components.azure.common.AzureTableConnection;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Suggestions;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.api.service.schema.DiscoverSchema;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.RetryLinearRetry;
import com.microsoft.azure.storage.RetryNoRetry;
import com.microsoft.azure.storage.RetryPolicy;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.DynamicTableEntity;
import com.microsoft.azure.storage.table.EntityProperty;
import com.microsoft.azure.storage.table.TableQuery;
import com.microsoft.azure.storage.table.TableRequestOptions;

@Service
public class AzureComponentServices {

    public static final String COLUMN_NAMES = "COLUMN_NAMES";

    @Service
    RecordBuilderFactory factory;

    @Service
    AzureConnectionService connectionService = new AzureConnectionService();

    @HealthCheck("testConnection")
    public HealthCheckStatus testConnection(@Option AzureConnection azureConnection) {
        try {
            CloudStorageAccount cloudStorageAccount = connectionService.createStorageAccount(azureConnection);
            TableRequestOptions options = new TableRequestOptions();
            options.setRetryPolicyFactory(new RetryNoRetry());
            final int MAX_TABLES = 1;
            final OperationContext operationContext = AzureTableUtils.getTalendOperationContext();
            CloudTableClient tableClient = cloudStorageAccount.createCloudTableClient();
            tableClient.setDefaultRequestOptions(options);
            // will throw an exception if not authorized or account not exist
            tableClient.listTablesSegmented(null, MAX_TABLES, null, null, operationContext);
        } catch (Exception e) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, e.getMessage());
        }
        // TODO i18n
        return new HealthCheckStatus(HealthCheckStatus.Status.OK, "Connected");
    }

    @Suggestions("getTableNames")
    public SuggestionValues getTableNames(@Option AzureConnection azureConnection) {
        List<SuggestionValues.Item> tableNames = new ArrayList<>();
        try {
            CloudStorageAccount storageAccount = connectionService.createStorageAccount(azureConnection);
            final OperationContext operationContext = AzureTableUtils.getTalendOperationContext();
            for (String tableName : storageAccount.createCloudTableClient().listTables(null, null, operationContext)) {
                tableNames.add(new SuggestionValues.Item(tableName, tableName));
            }

        } catch (Exception e) {
            throw new RuntimeException("Can't get tableNames", e);
        }

        return new SuggestionValues(true, tableNames);
    }

    @Suggestions(value = COLUMN_NAMES)
    public SuggestionValues getColumnNames(@Option List<String> schema) {
        List<SuggestionValues.Item> suggestionItemList = new ArrayList<>();

        schema.stream().map(s -> new SuggestionValues.Item(s, s)).forEach(suggestionItemList::add);

        return new SuggestionValues(false, suggestionItemList);
    }

    @DiscoverSchema("guessSchema")
    public Schema guessSchema(@Option final AzureTableConnection configuration) {
        final Schema.Entry.Builder entryBuilder = factory.newEntryBuilder();
        final Schema.Builder schemaBuilder = factory.newSchemaBuilder(Schema.Type.RECORD);
        // add 3 default columns
        schemaBuilder.withEntry(entryBuilder.withName("PartitionKey").withType(Schema.Type.STRING).build())
                .withEntry(entryBuilder.withName("RowKey").withType(Schema.Type.STRING).build())
                .withEntry(entryBuilder.withName("Timestamp").withType(Schema.Type.DATETIME).build());
        String tableName = configuration.getTableName();
        try {
            AzureConnection connection = configuration.getConnection();
            TableQuery<DynamicTableEntity> partitionQuery = TableQuery.from(DynamicTableEntity.class).take(1);
            CloudStorageAccount account = connectionService.createStorageAccount(connection);
            Iterable<DynamicTableEntity> entities = connectionService.executeQuery(account, tableName, partitionQuery);
            if (entities.iterator().hasNext()) {
                DynamicTableEntity result = entities.iterator().next();
                for (Map.Entry<String, EntityProperty> f : result.getProperties().entrySet()) {
                    schemaBuilder.withEntry(entryBuilder.withName(f.getKey())
                            .withType(AzureTableUtils.getAppropriateType(f.getValue().getEdmType())).withNullable(true).build());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Can't get schema", e);
        }
        return schemaBuilder.build();
    }
}
