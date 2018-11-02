package org.talend.components.azure.table.output;

import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.talend.components.azure.common.AzureConnection;
import org.talend.components.azure.common.AzureTableConnection;
import org.talend.components.azure.service.AzureConnectionService;
import org.talend.components.azure.service.MessageService;
import org.talend.components.azure.table.input.InputProperties;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.maven.MavenDecrypter;
import org.talend.sdk.component.maven.Server;
import org.talend.sdk.component.runtime.manager.chain.Job;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;

@SuppressWarnings("ConstantConditions")
@WithComponents("org.talend.components.azure")
public class AzureOutputITTest {

    private String tableName;

    private static OutputProperties outputProperties;

    @Service
    private AzureConnectionService connectionService;

    @ClassRule
    public static final SimpleComponentRule COMPONENT = new SimpleComponentRule("org.talend.components.azure");

    private CloudStorageAccount storageAccount;

    @BeforeAll
    public static void init() {
        Server account;
        AzureTableConnection dataSet = new AzureTableConnection();
        AzureConnection dataStore = new AzureConnection();
        final MavenDecrypter decrypter = new MavenDecrypter();
        account = decrypter.find("azure.account");
        dataStore.setAccountName(account.getUsername());
        dataStore.setAccountKey(account.getPassword());

        dataSet.setConnection(dataStore);
        outputProperties = new OutputProperties();
        outputProperties.setAzureConnection(dataSet);
    }

    @BeforeEach
    public void prepare() throws Exception {
        tableName = "TestIT" + RandomStringUtils.randomAlphanumeric(10);
        outputProperties.getAzureConnection().setTableName(tableName);
        outputProperties.setPartitionName("PartitionKey");
        outputProperties.setRowKey("RowKey");
        storageAccount = connectionService.createStorageAccount(outputProperties.getAzureConnection().getConnection());
        connectionService.createTable(storageAccount, tableName);
    }

    @Test
    public void testWriteData() {
        boolean booleanValue = true;
        long longValue = Long.MAX_VALUE;
        int intValue = Integer.MIN_VALUE;
        double doubleValue = 0.01;
        Instant dateValue = Instant.now();
        List<String> schema = new ArrayList<>();
        schema.add("PartitionKey");
        schema.add("RowKey");
        schema.add("booleanValue");
        schema.add("longValue");
        schema.add("intValue");
        schema.add("doubleValue");
        schema.add("dateValue");
        outputProperties.setSchema(schema);
        Record testRecord = COMPONENT.findService(RecordBuilderFactory.class).newRecordBuilder()
                .withString("PartitionKey", "testKey").withString("RowKey", "SomeKey").withBoolean("booleanValue", booleanValue)
                .withLong("longValue", longValue).withInt("intValue", intValue).withDouble("doubleValue", doubleValue)
                .withDateTime("dateValue", Date.from(dateValue)).build();
        COMPONENT.findService(MessageService.class);

        COMPONENT.setInputData(Collections.singletonList(testRecord));

        String outputConfig = configurationByExample().forInstance(outputProperties).configured().toQueryString();
        Job.components().component("inputFlow", "test://emitter")
                .component("outputComponent", "AzureStorage://OutputTable?" + outputConfig).connections().from("inputFlow")
                .to("outputComponent").build().run();

        InputProperties readerProperties = new InputProperties();
        readerProperties.setAzureConnection(outputProperties.getAzureConnection());
        readerProperties.setSchema(outputProperties.getSchema());
        String inputConfig = configurationByExample().forInstance(readerProperties).configured().toQueryString();
        Job.components().component("azureInput", "AzureStorage://InputTable?" + inputConfig)
                .component("collector", "test://collector").connections().from("azureInput").to("collector").build().run();

        List<Record> insertedRecords = COMPONENT.getCollectedData(Record.class);
        Assertions.assertEquals(1, insertedRecords.size());
        Record insertedRecord = insertedRecords.get(0);
        Assertions.assertEquals(booleanValue, insertedRecord.getBoolean("booleanValue"));
        Assertions.assertEquals(longValue, insertedRecord.getLong("longValue"));
        Assertions.assertEquals(intValue, insertedRecord.getInt("intValue"));
        Assertions.assertEquals(doubleValue, insertedRecord.getDouble("doubleValue"));
        Assertions.assertEquals(dateValue, insertedRecord.getDateTime("dateValue").toInstant());
    }

    @AfterEach
    public void dropTestTable() throws Exception {
        CloudTable cloudTable = storageAccount.createCloudTableClient().getTableReference(tableName);
        cloudTable.delete(null, AzureConnectionService.getTalendOperationContext());
    }
}