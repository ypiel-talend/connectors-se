package org.talend.components.azure.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.ClassRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.talend.components.azure.common.AzureConnection;
import org.talend.components.azure.common.AzureTableConnection;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.maven.MavenDecrypter;
import org.talend.sdk.component.maven.Server;

@WithComponents("org.talend.components.azure")
public class AzureServicesTestIT {

    @Service private AzureComponentServices componentServices;

    @Service private AzureConnectionService connectionService;

    private static AzureTableConnection dataSet;

    private static Server account;

    private boolean isCredentialsBroken = false;

    @BeforeAll
    public static void init() {
        dataSet = new AzureTableConnection();
        AzureConnection dataStore = new AzureConnection();
        final MavenDecrypter decrypter = new MavenDecrypter();
        account = decrypter.find("azure.account");
        dataStore.setAccountName(account.getUsername());
        dataStore.setAccountKey(account.getPassword());

        dataSet.setConnection(dataStore);
    }

    @AfterEach
    public void recover() {
        if (isCredentialsBroken) {
            dataSet.getConnection().setAccountName(account.getUsername());
            dataSet.getConnection().setAccountKey(account.getPassword());
            dataSet.getConnection().setUseAzureSharedSignature(false);
        }
    }

    @ClassRule
    public static final SimpleComponentRule COMPONENT = new SimpleComponentRule("org.talend.components.azure");

    @Test
    public void testHealthCheckOK() {
        System.out.println(dataSet);
        assertEquals(HealthCheckStatus.Status.OK, componentServices.testConnection(dataSet.getConnection()).getStatus());
    }

    @Test
    public void testHealthCheckFailing() {
        String notExistingAccountName = "testNotExistingAccountName";
        dataSet.getConnection().setAccountName(notExistingAccountName);
        isCredentialsBroken = true;
        assertEquals(HealthCheckStatus.Status.KO, componentServices.testConnection(dataSet.getConnection()).getStatus());
    }

    @Test
    public void testGetTableNamesIsNotEmpty() {
        assertFalse(componentServices.getTableNames(dataSet.getConnection()).getItems().isEmpty());
    }

    @Test
    public void testGetSchema() {
        String tableName = "mytable";
        dataSet.setTableName(tableName);
        Schema schema = componentServices.guessSchema(dataSet);

        assertTrue(schema.getEntries().size() >= 3);
    }

}
