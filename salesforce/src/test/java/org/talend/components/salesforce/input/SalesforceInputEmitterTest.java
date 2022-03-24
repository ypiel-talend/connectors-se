/*
 * Copyright (C) 2006-2022 Talend Inc. - www.talend.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.talend.components.salesforce.input;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.talend.components.salesforce.service.SalesforceService.URL;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.*;
import org.talend.components.salesforce.SalesforceTestBase;
import org.talend.components.salesforce.configuration.InputModuleConfig;
import org.talend.components.salesforce.configuration.InputSOQLConfig;
import org.talend.components.salesforce.configuration.OutputConfig;
import org.talend.components.salesforce.dataset.ModuleDataSet;
import org.talend.components.salesforce.dataset.SOQLQueryDataSet;
import org.talend.components.salesforce.datastore.BasicDataStore;
import org.talend.sdk.component.api.exception.ComponentException;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.manager.chain.Job;

@Disabled("Salesforce credentials is not ready on ci")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WithComponents("org.talend.components.salesforce")
public class SalesforceInputEmitterTest extends SalesforceTestBase {

    private static final String UNIQUE_ID;

    static {
        UNIQUE_ID = Integer.toString(ThreadLocalRandom.current().nextInt(1, 100000));
    }

    @Service
    private RecordBuilderFactory factory;

    @BeforeAll
    public void testprepareData() {

        // 1. prepare Account data
        final OutputConfig configuration = new OutputConfig();
        final ModuleDataSet moduleDataSet = new ModuleDataSet();
        moduleDataSet.setModuleName("Account");
        moduleDataSet.setDataStore(getDataStore());
        configuration.setOutputAction(OutputConfig.OutputAction.INSERT);
        configuration.setModuleDataSet(moduleDataSet);

        RecordBuilderFactory factory = getComponentsHandler().findService(RecordBuilderFactory.class);

        List<Record> records = new ArrayList<>(10);
        for (int i = 0; i < 300; i++) {
            Record.Builder recordBuilder = factory.newRecordBuilder();
            recordBuilder.withString("Name", "TestName_" + i + "_" + UNIQUE_ID);
            if (i % 40 == 0) {
                recordBuilder.withInt("NumberOfEmployees", 8000);
            }
            if (i % 80 == 0) {
                factory.newRecordBuilder().withDouble("AnnualRevenue", 5000.0);
            }
            records.add(recordBuilder.build());
        }

        final String config = configurationByExample().forInstance(configuration).configured().toQueryString();
        getComponentsHandler().setInputData(records);
        Job
                .components()
                .component("emitter", "test://emitter")
                .component("salesforce-output", "Salesforce://SalesforceOutput?" + config)
                .connections()
                .from("emitter")
                .to("salesforce-output")
                .build()
                .run();
        getComponentsHandler().resetState();
        // 2. prepare Contact data

        moduleDataSet.setModuleName("Contact");
        moduleDataSet.setDataStore(getDataStore());
        configuration.setOutputAction(OutputConfig.OutputAction.INSERT);
        configuration.setModuleDataSet(moduleDataSet);
        configuration.setBatchMode(false);
        configuration.setExceptionForErrors(true);

        Record record = factory
                .newRecordBuilder()
                .withString("FirstName", "F_test_types_" + UNIQUE_ID)
                .withString("LastName", "L_test_types_" + UNIQUE_ID)
                .withString("Email", "testalltype_" + UNIQUE_ID + "@test.com")
                .withDouble("MailingLongitude", 115.7)
                .withDouble("MailingLatitude", 39.4)
                .withDateTime("Birthdate", new Date())
                .build();
        final String outputContactConfig =
                configurationByExample().forInstance(configuration).configured().toQueryString();
        getComponentsHandler().setInputData(asList(record));
        Job
                .components()
                .component("emitter", "test://emitter")
                .component("salesforce-output", "Salesforce://SalesforceOutput?" + outputContactConfig)
                .connections()
                .from("emitter")
                .to("salesforce-output")
                .build()
                .run();
        getComponentsHandler().resetState();
    }

    // Module Query part
    @Test
    @DisplayName("Test query with module name")
    public void testModuleQuery() {
        final ModuleDataSet moduleDataSet = new ModuleDataSet();
        moduleDataSet.setModuleName("Account");
        moduleDataSet.setSelectColumnNames(Arrays.asList("Id", "Name"));
        moduleDataSet.setDataStore(getDataStore());
        moduleDataSet.setCondition("Name Like 'TestName_%" + UNIQUE_ID + "%'");

        final InputModuleConfig inputConfig = new InputModuleConfig();
        inputConfig.setDataSet(moduleDataSet);

        final String config = configurationByExample().forInstance(inputConfig).configured().toQueryString();
        Job
                .components()
                .component("salesforce-input", "Salesforce://ModuleQueryInput?" + config)
                .component("collector", "test://collector")
                .connections()
                .from("salesforce-input")
                .to("collector")
                .build()
                .run();
        final List<Record> records = getComponentsHandler().getCollectedData(Record.class);
        assertEquals(300, records.size());
        Record record = records.get(0);
        Schema schema = record.getSchema();
        List<Schema.Entry> entries = schema.getEntries();
        assertEquals(2, entries.size());
    }

    @Test
    @DisplayName("Test query null value with primitive type")
    public void testQueryNullValues() {
        final ModuleDataSet moduleDataSet = new ModuleDataSet();
        moduleDataSet.setModuleName("Account");
        moduleDataSet.setSelectColumnNames(Arrays.asList("Id", "Name", "NumberOfEmployees", "AnnualRevenue"));
        moduleDataSet.setDataStore(getDataStore());
        moduleDataSet
                .setCondition(
                        "NumberOfEmployees = null and AnnualRevenue = null and Name Like 'TestName_%" + UNIQUE_ID
                                + "%' limit 5");

        final InputModuleConfig inputConfig = new InputModuleConfig();
        inputConfig.setDataSet(moduleDataSet);

        final String config = configurationByExample().forInstance(inputConfig).configured().toQueryString();
        Job
                .components()
                .component("salesforce-input", "Salesforce://ModuleQueryInput?" + config)
                .component("collector", "test://collector")
                .connections()
                .from("salesforce-input")
                .to("collector")
                .build()
                .run();
        final List<Record> records = getComponentsHandler().getCollectedData(Record.class);
        assertEquals(5, records.size());
        Record record = records.get(0);
        Schema schema = record.getSchema();
        List<Schema.Entry> entries = schema.getEntries();
        assertEquals(4, entries.size());
        assertEquals(Schema.Type.INT, entries.get(2).getType());
        assertEquals(Schema.Type.DOUBLE, entries.get(3).getType());
        assertNull(record.get(Object.class, "NumberOfEmployees"));
        assertNull(record.get(Object.class, "AnnualRevenue"));
    }

    @Test
    @DisplayName("Test query module record with limit")
    public void testModuleQueryWithLimit() {
        final ModuleDataSet moduleDataSet = new ModuleDataSet();
        moduleDataSet.setModuleName("Account");
        moduleDataSet.setSelectColumnNames(Arrays.asList("Name"));
        moduleDataSet.setDataStore(getDataStore());
        moduleDataSet.setCondition("Name Like 'TestName_%" + UNIQUE_ID + "%' limit 10");

        final InputModuleConfig inputConfig = new InputModuleConfig();
        inputConfig.setDataSet(moduleDataSet);

        final String config = configurationByExample().forInstance(inputConfig).configured().toQueryString();
        Job
                .components()
                .component("salesforce-input", "Salesforce://ModuleQueryInput?" + config)
                .component("collector", "test://collector")
                .connections()
                .from("salesforce-input")
                .to("collector")
                .build()
                .run();
        final List<Record> records = getComponentsHandler().getCollectedData(Record.class);
        Assertions.assertEquals(10, records.size());

        records.stream().forEach(r -> assertTrue(records.iterator().next().getString("Name").contains("TestName")));
    }

    @Test
    @DisplayName("Test query module record with all types")
    public void testQueryAllType() {
        final ModuleDataSet moduleDataSet = new ModuleDataSet();
        moduleDataSet.setModuleName("Account");
        moduleDataSet
                .setSelectColumnNames(Arrays
                        .asList("Id", "IsDeleted", "MasterRecordId", "Name", "Type", "ParentId",
                                "BillingStreet", "BillingCity", "BillingState", "BillingPostalCode", "BillingCountry",
                                "BillingLatitude",
                                "BillingLongitude", "ShippingStreet", "ShippingCity", "ShippingState",
                                "ShippingPostalCode", "ShippingCountry",
                                "ShippingLatitude", "ShippingLongitude", "Phone", "Fax", "AccountNumber", "Website",
                                "PhotoUrl", "Sic",
                                "Industry", "AnnualRevenue", "NumberOfEmployees", "Ownership", "TickerSymbol",
                                "Description", "Rating", "Site",
                                "OwnerId", "CreatedDate", "CreatedById", "LastModifiedDate", "LastModifiedById",
                                "SystemModstamp",
                                "LastActivityDate", "LastViewedDate", "LastReferencedDate", "Jigsaw", "JigsawCompanyId",
                                "AccountSource",
                                "SicDesc"));
        moduleDataSet.setDataStore(getDataStore());
        moduleDataSet.setCondition("Name Like 'TestName_%" + UNIQUE_ID + "%' and NumberOfEmployees != null");

        final InputModuleConfig inputConfig = new InputModuleConfig();
        inputConfig.setDataSet(moduleDataSet);

        final String config = configurationByExample().forInstance(inputConfig).configured().toQueryString();
        Job
                .components()
                .component("salesforce-input", "Salesforce://ModuleQueryInput?" + config)
                .component("collector", "test://collector")
                .connections()
                .from("salesforce-input")
                .to("collector")
                .build()
                .run();
        final List<Record> records = getComponentsHandler().getCollectedData(Record.class);
        Assertions.assertEquals(8, records.size());
        Record record = records.get(0);
        assertEquals(8000, record.getInt("NumberOfEmployees"));
    }

    @Test
    @DisplayName("Test query with bad credential")
    public void testQueryWithBadCredential() {
        final BasicDataStore datstore = new BasicDataStore();
        datstore.setEndpoint(URL);
        datstore.setUserId("badUser");
        datstore.setPassword("badPasswd");
        datstore.setSecurityKey("badSecurityKey");
        final ModuleDataSet moduleDataSet = new ModuleDataSet();
        moduleDataSet.setModuleName("account");
        moduleDataSet.setDataStore(datstore);

        final InputModuleConfig inputConfig = new InputModuleConfig();
        inputConfig.setDataSet(moduleDataSet);

        final String config = configurationByExample().forInstance(inputConfig).configured().toQueryString();
        assertThrows(IllegalStateException.class,
                () -> Job
                        .components()
                        .component("salesforce-input", "Salesforce://Input?" + config)
                        .component("collector", "test://collector")
                        .connections()
                        .from("salesforce-input")
                        .to("collector")
                        .build()
                        .run());
    }

    @Test
    @DisplayName("Test module query with invalid name")
    public void testModuleQueryWithInvalideName() {
        final ModuleDataSet moduleDataSet = new ModuleDataSet();
        moduleDataSet.setModuleName("invalid0");
        moduleDataSet.setDataStore(getDataStore());

        final InputModuleConfig inputConfig = new InputModuleConfig();
        inputConfig.setDataSet(moduleDataSet);

        final String config = configurationByExample().forInstance(inputConfig).configured().toQueryString();
        ComponentException ex = assertThrows(ComponentException.class,
                () -> Job
                        .components()
                        .component("salesforce-input", "Salesforce://ModuleQueryInput?" + config)
                        .component("collector", "test://collector")
                        .connections()
                        .from("salesforce-input")
                        .to("collector")
                        .build()
                        .run());
        assertTrue(ex.getMessage().contains("java.lang.IllegalStateException"), ex.getMessage());
    }

    @Test
    @DisplayName("Test module query with invalid field")
    public void testModuleQueryWithInvalidField() {
        final ModuleDataSet moduleDataSet = new ModuleDataSet();
        moduleDataSet.setModuleName("account");
        moduleDataSet.setSelectColumnNames(singletonList("InvalidField10x"));
        moduleDataSet.setDataStore(getDataStore());

        final InputModuleConfig inputConfig = new InputModuleConfig();
        inputConfig.setDataSet(moduleDataSet);

        final String config = configurationByExample().forInstance(inputConfig).configured().toQueryString();
        assertThrows(IllegalStateException.class,
                () -> Job
                        .components()
                        .component("salesforce-input", "Salesforce://ModuleQueryInput?" + config)
                        .component("collector", "test://collector")
                        .connections()
                        .from("salesforce-input")
                        .to("collector")
                        .build()
                        .run());
    }

    // SOQL Query part
    @Test
    @DisplayName("Test basic SOQL query")
    public void testSOQLQueryBasicCase() {
        final SOQLQueryDataSet soqlQueryDataSet = new SOQLQueryDataSet();
        soqlQueryDataSet
                .setQuery("select Id,Name,IsDeleted from account where Name Like 'TestName_%" + UNIQUE_ID + "%'");
        soqlQueryDataSet.setDataStore(getDataStore());

        final InputSOQLConfig inputConfig = new InputSOQLConfig();
        inputConfig.setDataSet(soqlQueryDataSet);

        final String config = configurationByExample().forInstance(inputConfig).configured().toQueryString();
        Job
                .components()
                .component("salesforce-input", "Salesforce://SOQLQueryInput?" + config)
                .component("collector", "test://collector")
                .connections()
                .from("salesforce-input")
                .to("collector")
                .build()
                .run();
        final List<Record> records = getComponentsHandler().getCollectedData(Record.class);
        Assertions.assertEquals(300, records.size());
        Record record = records.get(0);
        Schema schema = record.getSchema();
        assertNotNull(schema);
        assertEquals(3, schema.getEntries().size());
        assertTrue(record.getString("Name").contains("TestName"));
    }

    @Test
    @DisplayName("Test SOQL query return empty result")
    public void testSOQLQueryWithEmptyResult() {
        final SOQLQueryDataSet soqlQueryDataSet = new SOQLQueryDataSet();
        soqlQueryDataSet.setQuery("select  name from account where name = 'this name will never exist $'");
        soqlQueryDataSet.setDataStore(getDataStore());

        final InputSOQLConfig inputConfig = new InputSOQLConfig();
        inputConfig.setDataSet(soqlQueryDataSet);

        final String config = configurationByExample().forInstance(inputConfig).configured().toQueryString();
        Job
                .components()
                .component("salesforce-input", "Salesforce://SOQLQueryInput?" + config)
                .component("collector", "test://collector")
                .connections()
                .from("salesforce-input")
                .to("collector")
                .build()
                .run();
        final List<Record> records = getComponentsHandler().getCollectedData(Record.class);
        Assertions.assertEquals(0, records.size());
    }

    @Test
    @DisplayName("Test SOQL query with Invalid query")
    public void testSOQLQueryWithInvalidQuery() {
        final SOQLQueryDataSet soqlQueryDataSet = new SOQLQueryDataSet();
        soqlQueryDataSet.setQuery("from account");
        soqlQueryDataSet.setDataStore(getDataStore());

        final InputSOQLConfig inputConfig = new InputSOQLConfig();
        inputConfig.setDataSet(soqlQueryDataSet);

        final String config = configurationByExample().forInstance(inputConfig).configured().toQueryString();
        assertThrows(IllegalStateException.class,
                () -> Job
                        .components()
                        .component("salesforce-input", "Salesforce://SOQLQueryInput?" + config)
                        .component("collector", "test://collector")
                        .connections()
                        .from("salesforce-input")
                        .to("collector")
                        .build()
                        .run());
    }

    @Test
    @DisplayName("Test SOQL query with relationship")
    public void testSOQLQueryChildToParent() {
        final SOQLQueryDataSet soqlQueryDataSet = new SOQLQueryDataSet();
        soqlQueryDataSet
                .setQuery(
                        "select Id,Name,CreatedBy.Name from Account where Name Like 'TestName_100%" + UNIQUE_ID + "%'");
        soqlQueryDataSet.setDataStore(getDataStore());

        final InputSOQLConfig inputConfig = new InputSOQLConfig();
        inputConfig.setDataSet(soqlQueryDataSet);

        final String config = configurationByExample().forInstance(inputConfig).configured().toQueryString();
        Job
                .components()
                .component("salesforce-input", "Salesforce://SOQLQueryInput?" + config)
                .component("collector", "test://collector")
                .connections()
                .from("salesforce-input")
                .to("collector")
                .build()
                .run();
        final List<Record> records = getComponentsHandler().getCollectedData(Record.class);
        Assertions.assertEquals(1, records.size());
        Record record = records.get(0);
        assertNotNull(record.getString("CreatedBy_Name"));
    }

    @Test
    @DisplayName("Test SOQL query with relationship without name")
    public void testSOQLQueryChildToParentWithoutName() {
        final SOQLQueryDataSet soqlQueryDataSet = new SOQLQueryDataSet();
        soqlQueryDataSet
                .setQuery(
                        "select Contact.Name, Account.Name from Contact where MailingLongitude != null and MailingLongitude !=null and Birthdate !=null and Name Like 'F_test_types_%"
                                + UNIQUE_ID + "%'");
        soqlQueryDataSet.setDataStore(getDataStore());

        final InputSOQLConfig inputConfig = new InputSOQLConfig();
        inputConfig.setDataSet(soqlQueryDataSet);

        final String config = configurationByExample().forInstance(inputConfig).configured().toQueryString();
        Job
                .components()
                .component("salesforce-input", "Salesforce://SOQLQueryInput?" + config)
                .component("collector", "test://collector")
                .connections()
                .from("salesforce-input")
                .to("collector")
                .build()
                .run();
        final List<Record> records = getComponentsHandler().getCollectedData(Record.class);
        Assertions.assertEquals(1, records.size());
        Record record = records.get(0);
        assertEquals("F_test_types_" + UNIQUE_ID + " " + "L_test_types_" + UNIQUE_ID, record.getString("Contact_Name"));
        assertNull(record.getString("Account_Name"));
    }

    @Test
    @DisplayName("Test query module with empty selected fields")
    public void testModuleQueryEmptySelectedFields() {
        final ModuleDataSet moduleDataSet = new ModuleDataSet();
        moduleDataSet.setModuleName("Account");
        moduleDataSet.setSelectColumnNames(Collections.emptyList());
        moduleDataSet.setDataStore(getDataStore());
        moduleDataSet.setCondition("Name Like 'TestName_%" + UNIQUE_ID + "%' limit 10");

        final InputModuleConfig inputConfig = new InputModuleConfig();
        inputConfig.setDataSet(moduleDataSet);

        final String config = configurationByExample().forInstance(inputConfig).configured().toQueryString();
        Job
                .components()
                .component("salesforce-input", "Salesforce://ModuleQueryInput?" + config)
                .component("collector", "test://collector")
                .connections()
                .from("salesforce-input")
                .to("collector")
                .build()
                .run();
        final List<Record> records = getComponentsHandler().getCollectedData(Record.class);
        Assertions.assertEquals(10, records.size());
        Schema schema = records.iterator().next().getSchema();
        assertTrue(schema.getEntries().size() > 50, "schema fields list size: " + schema.getEntries().size());
    }

    @AfterAll
    public void test99_cleanupTestRecords() {
        cleanTestRecords("Account", "Name Like '%" + UNIQUE_ID + "%'");
        checkModuleData("Account", "Name Like '%" + UNIQUE_ID + "%'", 0);

        cleanTestRecords("Contact", "Email Like '%" + UNIQUE_ID + "%'");
        checkModuleData("Contact", "Email Like '%" + UNIQUE_ID + "%'", 0);
    }

}