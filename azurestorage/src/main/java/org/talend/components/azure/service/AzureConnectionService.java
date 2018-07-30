package org.talend.components.azure.service;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.StorageCredentialsAccountAndKey;
import com.microsoft.azure.storage.StorageCredentialsSharedAccessSignature;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.DynamicTableEntity;
import com.microsoft.azure.storage.table.EdmType;
import com.microsoft.azure.storage.table.EntityProperty;
import com.microsoft.azure.storage.table.TableQuery;
import org.talend.components.azure.common.AzureConnection;
import org.talend.components.azure.common.Protocol;
import org.talend.components.azure.table.input.InputTableMapperConfiguration;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Suggestions;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.api.service.schema.DiscoverSchema;
import org.talend.sdk.component.api.service.schema.Schema;
import org.talend.sdk.component.api.service.schema.Type;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AzureConnectionService {

    // you can put logic here you can reuse in components
    @HealthCheck("testConnection")
    public HealthCheckStatus testConnection(@Option AzureConnection azureConnection) {
        try {
            CloudStorageAccount cloudStorageAccount = createStorageAccount(azureConnection);
            final int MAX_TABLES = 1;
            //TODO partner tag
            final OperationContext operationContext = null;
            //will throw an exception if not authorized
            //FIXME too long if account not exists
            cloudStorageAccount.createCloudTableClient().listTablesSegmented(null, MAX_TABLES, null, null, operationContext);
        } catch (Exception e) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, e.getMessage());
        }
        //TODO i18n
        return new HealthCheckStatus(HealthCheckStatus.Status.OK, "Connected");
    }

    @Suggestions("getTableNames")
    public SuggestionValues getTableNames(@Option AzureConnection azureConnection) {
        List<SuggestionValues.Item> tableNames = new ArrayList<>();
        try {
            CloudStorageAccount storageAccount = createStorageAccount(azureConnection);
            final OperationContext operationContext = AzureConnectionUtils.getTalendOperationContext();
            for (String tableName : storageAccount.createCloudTableClient().listTables(null, null, operationContext)) {
                tableNames.add(new SuggestionValues.Item(tableName, tableName));
            }

        } catch (Exception e) {
            throw new RuntimeException("Can't get tableNames", e);
        }

        return new SuggestionValues(true, tableNames);
    }


    @DiscoverSchema("guessSchema")
    public Schema guessSchema(@Option final InputTableMapperConfiguration configuration) {
        List<Schema.Entry> columns = new ArrayList<>();
        //add 3 default columns
        columns.add(new Schema.Entry("PartitionKey", Type.STRING));
        columns.add(new Schema.Entry("RowKey", Type.STRING));
        columns.add(new Schema.Entry("Timestamp", Type.STRING));
        String tableName = configuration.getAzureConnection().getTableName();
        try {
            AzureConnection connection = configuration.getAzureConnection().getConnection();
            TableQuery<DynamicTableEntity> partitionQuery = TableQuery.from(DynamicTableEntity.class).take(1);
            Iterable<DynamicTableEntity> entities = executeQuery(createStorageAccount(connection), tableName, partitionQuery);
            if (entities.iterator().hasNext()) {
                DynamicTableEntity result = entities.iterator().next();
                for (Map.Entry<String, EntityProperty> f : result.getProperties().entrySet()) {
                    String fieldName = f.getKey();
                    columns.add(new Schema.Entry(fieldName, getAppropriateType(f.getValue().getEdmType())));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Can't get schema", e);
        }

        return new Schema(columns);
    }

    private Type getAppropriateType(EdmType edmType) {
        switch (edmType) {
        case BOOLEAN:
            return Type.BOOLEAN;
        case BYTE:
        case SBYTE:
        case INT16:
        case INT32:
            return Type.INT;
        case INT64:
        case DECIMAL:
        case SINGLE:
        case DOUBLE:
            return Type.DOUBLE;
        default:
            return Type.STRING;
        }

    }

    private Iterable<DynamicTableEntity> executeQuery(CloudStorageAccount storageAccount, String tableName, TableQuery<DynamicTableEntity> partitionQuery)
            throws URISyntaxException, StorageException {

        CloudTable cloudTable = storageAccount.createCloudTableClient().getTableReference(tableName);
        return cloudTable.execute(partitionQuery, null, AzureConnectionUtils.getTalendOperationContext());
    }

    public CloudStorageAccount createStorageAccount(AzureConnection azureConnection) throws URISyntaxException {
        StorageCredentials credentials = null;
        if (!azureConnection.isUseAzureSharedSignature()) {
            credentials = new StorageCredentialsAccountAndKey(azureConnection.getAccountName(), azureConnection.getAccountKey());
        } else {
            //TODO test it
            credentials = new StorageCredentialsSharedAccessSignature(azureConnection.getAzureSharedAccessSignature());
        }
        return new CloudStorageAccount(credentials, azureConnection.getProtocol() == Protocol.HTTPS);
    }

}