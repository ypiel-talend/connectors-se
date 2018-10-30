package org.talend.components.azure.service;

import java.net.URISyntaxException;

import org.talend.components.azure.common.AzureConnection;
import org.talend.components.azure.common.Protocol;
import org.talend.sdk.component.api.service.Service;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.StorageCredentialsAccountAndKey;
import com.microsoft.azure.storage.StorageCredentialsSharedAccessSignature;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.DynamicTableEntity;
import com.microsoft.azure.storage.table.TableQuery;

@Service
public class AzureConnectionService {
    public Iterable<DynamicTableEntity> executeQuery(CloudStorageAccount storageAccount, String tableName,
            TableQuery<DynamicTableEntity> partitionQuery) throws URISyntaxException, StorageException {

        CloudTable cloudTable = storageAccount.createCloudTableClient().getTableReference(tableName);
        return cloudTable.execute(partitionQuery, null, AzureTableUtils.getTalendOperationContext());
    }

    public CloudStorageAccount createStorageAccount(AzureConnection azureConnection) throws URISyntaxException {
        StorageCredentials credentials = null;
        if (!azureConnection.isUseAzureSharedSignature()) {
            credentials = new StorageCredentialsAccountAndKey(azureConnection.getAccountName(), azureConnection.getAccountKey());
        } else {
            credentials = new StorageCredentialsSharedAccessSignature(azureConnection.getAzureSharedAccessSignature());
        }
        return new CloudStorageAccount(credentials, azureConnection.getProtocol() == Protocol.HTTPS);
    }
}