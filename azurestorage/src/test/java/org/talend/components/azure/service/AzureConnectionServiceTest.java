package org.talend.components.azure.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.junit.Test;
import org.mockito.Mockito;
import org.talend.components.azure.common.AzureConnection;
import org.talend.components.azure.common.AzureTableConnection;
import org.talend.components.azure.table.input.InputTableMapperConfiguration;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.api.service.schema.Schema;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.DynamicTableEntity;
import com.microsoft.azure.storage.table.EntityProperty;
import com.microsoft.azure.storage.table.TableQuery;

public class AzureConnectionServiceTest {

    @Test
    public void testHealthCheckOK() throws Exception {
        CloudTableClient mockedTableClient = Mockito.mock(CloudTableClient.class);
        AzureConnection connection = new AzureConnection();
        CloudStorageAccount mockedStorageAccount = Mockito.mock(CloudStorageAccount.class);
        Mockito.when(mockedStorageAccount.createCloudTableClient()).thenReturn(mockedTableClient);
        AzureConnectionService connectionService = Mockito.mock(AzureConnectionService.class);
        Mockito.when(connectionService.testConnection(any())).thenCallRealMethod();
        Mockito.when(connectionService.createStorageAccount(connection)).thenReturn(mockedStorageAccount);
        HealthCheckStatus status = connectionService.testConnection(connection);

        assertEquals(HealthCheckStatus.Status.OK, status.getStatus());
    }

    @Test
    public void testHealthCheckFailed() throws Exception {
        CloudTableClient mockedTableClient = Mockito.mock(CloudTableClient.class);
        Mockito.when(mockedTableClient.listTablesSegmented(null, 1, null, null, AzureConnectionUtils.getTalendOperationContext()))
                .thenThrow(RuntimeException.class);
        AzureConnection connection = new AzureConnection();
        CloudStorageAccount mockedStorageAccount = Mockito.mock(CloudStorageAccount.class);
        Mockito.when(mockedStorageAccount.createCloudTableClient()).thenReturn(mockedTableClient);
        AzureConnectionService connectionService = Mockito.mock(AzureConnectionService.class);
        Mockito.when(connectionService.testConnection(any())).thenCallRealMethod();
        Mockito.when(connectionService.createStorageAccount(connection)).thenReturn(mockedStorageAccount);
        HealthCheckStatus status = connectionService.testConnection(connection);

        assertEquals(HealthCheckStatus.Status.KO, status.getStatus());
    }

    @Test
    public void testGetTableList() throws Exception {
        CloudTableClient mockedTableClient = Mockito.mock(CloudTableClient.class);
        String expectedTableName = "someTableName";
        Mockito.when(mockedTableClient.listTables(null, null, AzureConnectionUtils.getTalendOperationContext()))
                .thenReturn(Collections.singletonList(expectedTableName));
        AzureConnection connection = new AzureConnection();
        CloudStorageAccount mockedStorageAccount = Mockito.mock(CloudStorageAccount.class);
        Mockito.when(mockedStorageAccount.createCloudTableClient()).thenReturn(mockedTableClient);
        AzureConnectionService connectionService = Mockito.mock(AzureConnectionService.class);
        Mockito.when(connectionService.getTableNames(connection)).thenCallRealMethod();
        Mockito.when(connectionService.createStorageAccount(connection)).thenReturn(mockedStorageAccount);

        SuggestionValues tableList = connectionService.getTableNames(connection);

        assertNotNull(tableList);
        assertTrue(tableList.isCacheable());
        assertEquals(1, tableList.getItems().size());
        assertEquals(expectedTableName, tableList.getItems().iterator().next().getId());
    }

    @Test(expected = RuntimeException.class)
    public void testGetTableListFailed() throws Exception {
        CloudTableClient mockedTableClient = Mockito.mock(CloudTableClient.class);
        Mockito.when(mockedTableClient.listTables(null, null, AzureConnectionUtils.getTalendOperationContext()))
                .thenThrow(new RuntimeException());
        AzureConnection connection = new AzureConnection();
        CloudStorageAccount mockedStorageAccount = Mockito.mock(CloudStorageAccount.class);
        Mockito.when(mockedStorageAccount.createCloudTableClient()).thenReturn(mockedTableClient);
        AzureConnectionService connectionService = Mockito.mock(AzureConnectionService.class);
        Mockito.when(connectionService.getTableNames(connection)).thenCallRealMethod();
        Mockito.when(connectionService.createStorageAccount(connection)).thenReturn(mockedStorageAccount);

        connectionService.getTableNames(connection);
    }

    @Test
    public void testGuessSchema() throws Exception {
        String testTableName = "someTableName";
        InputTableMapperConfiguration testDataSet = new InputTableMapperConfiguration();
        testDataSet.setAzureConnection(new AzureTableConnection());
        testDataSet.getAzureConnection().setTableName(testTableName);
        AzureConnectionService connectionService = Mockito.mock(AzureConnectionService.class);
        CloudStorageAccount mockedStorageAccount = Mockito.mock(CloudStorageAccount.class);
        Mockito.when(connectionService.createStorageAccount(testDataSet.getAzureConnection().getConnection()))
                .thenReturn(mockedStorageAccount);
        HashMap<String, EntityProperty> propertyHashMap = new HashMap<>();
        propertyHashMap.put("booleanColumn", new EntityProperty(true));
        propertyHashMap.put("intColumn", new EntityProperty(123));
        propertyHashMap.put("stringColumn", new EntityProperty("String value"));
        propertyHashMap.put("doubleColumn", new EntityProperty(12.3));
        DynamicTableEntity tableEntity = new DynamicTableEntity("someKey", "someKey2", propertyHashMap);
        Mockito.when(connectionService.executeQuery(any(), any(), any())).thenReturn(Collections.singletonList(tableEntity));
        Mockito.when(connectionService.guessSchema(any())).thenCallRealMethod();

        Schema schema = connectionService.guessSchema(testDataSet);

        assertEquals(3 + propertyHashMap.size(), schema.getEntries().size());
        Iterator<Schema.Entry> iterator = schema.getEntries().iterator();
        Schema.Entry expectedPartitionKey = iterator.next();
        Schema.Entry expectedRowKey = iterator.next();
        Schema.Entry expectedTimeStamp = iterator.next();

        assertEquals("PartitionKey", expectedPartitionKey.getName());
        assertEquals("RowKey", expectedRowKey.getName());
        assertEquals("Timestamp", expectedTimeStamp.getName());
    }

    @Test(expected = RuntimeException.class)
    public void testGuessSchemaFailing() throws Exception {
        String testTableName = "someTableName";
        InputTableMapperConfiguration testDataSet = new InputTableMapperConfiguration();
        testDataSet.setAzureConnection(new AzureTableConnection());
        testDataSet.getAzureConnection().setTableName(testTableName);
        AzureConnectionService connectionService = Mockito.mock(AzureConnectionService.class);
        CloudStorageAccount mockedStorageAccount = Mockito.mock(CloudStorageAccount.class);
        Mockito.when(connectionService.createStorageAccount(testDataSet.getAzureConnection().getConnection()))
                .thenReturn(mockedStorageAccount);
        Mockito.when(connectionService.executeQuery(any(), any(), any())).thenThrow(RuntimeException.class);
        Mockito.when(connectionService.guessSchema(any())).thenCallRealMethod();

        connectionService.guessSchema(testDataSet);
    }

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

        Mockito.verify(mockedTable).execute(mockedQuery, null, AzureConnectionUtils.getTalendOperationContext());
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