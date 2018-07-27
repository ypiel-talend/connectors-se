package org.talend.components.azure.service;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.StorageCredentialsAccountAndKey;
import com.microsoft.azure.storage.StorageCredentialsSharedAccessSignature;
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
            //TODO partner tag
            final OperationContext operationContext = null;
            for (String tableName : storageAccount.createCloudTableClient().listTables(null, null, operationContext)) {
                tableNames.add(new SuggestionValues.Item(tableName, tableName));
            }

        } catch (Exception e) {
            throw new RuntimeException("Can't get tableNames", e);
        }

        return new SuggestionValues(true, tableNames);
    }


    @DiscoverSchema("guess")
    public Schema guessSchema(@Option final InputTableMapperConfiguration configuration) {
        List<Schema.Entry> columns = new ArrayList<>();
        columns.add(new Schema.Entry("someId", Type.STRING));
        return new Schema(columns);
    }

    private CloudStorageAccount createStorageAccount(@Option AzureConnection azureConnection) throws URISyntaxException {
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