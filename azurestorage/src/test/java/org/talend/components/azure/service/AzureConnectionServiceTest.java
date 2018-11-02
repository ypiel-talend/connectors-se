package org.talend.components.azure.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Base64;

import org.junit.Test;
import org.mockito.Mockito;
import org.talend.components.azure.common.AzureConnection;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableQuery;

public class AzureConnectionServiceTest {

    @Test
    public void testExecuteQuery() throws Exception {
        String someTableName = "someTableName";
        CloudStorageAccount mockedAccount = Mockito.mock(CloudStorageAccount.class);
        TableQuery mockedQuery = Mockito.mock(TableQuery.class);
        CloudTableClient mockedTableClient = Mockito.mock(CloudTableClient.class);
        CloudTable mockedTable = Mockito.mock(CloudTable.class);
        Mockito.when(mockedTableClient.getTableReference(someTableName)).thenReturn(mockedTable);
        Mockito.when(mockedAccount.createCloudTableClient()).thenReturn(mockedTableClient);
        new AzureConnectionService().executeQuery(mockedAccount, someTableName, mockedQuery);

        Mockito.verify(mockedTable).execute(mockedQuery, null, AzureConnectionService.getTalendOperationContext());
    }

    @Test
    public void testCreateStorageAccountWithPass() throws Exception {
        AzureConnection connectionProperties = new AzureConnection();
        String expectedAccountKey = "someAccountName";
        connectionProperties.setUseAzureSharedSignature(false);
        connectionProperties.setAccountName(expectedAccountKey);
        String accountKey = "someKey";
        connectionProperties.setAccountKey(new String(Base64.getEncoder().encode(accountKey.getBytes())));
        CloudStorageAccount account = new AzureConnectionService().createStorageAccount(connectionProperties);

        assertNotNull(account);
        assertEquals(expectedAccountKey, account.getCredentials().getAccountName());
    }

}