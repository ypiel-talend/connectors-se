// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.azure.table.input;

import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.talend.components.azure.common.AzureConnection;
import org.talend.components.azure.common.AzureTableConnection;
import org.talend.components.azure.service.AzureComponentServices;
import org.talend.components.azure.service.MessageService;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.maven.MavenDecrypter;
import org.talend.sdk.component.maven.Server;
import org.talend.sdk.component.runtime.manager.chain.Job;

@WithComponents("org.talend.components.azure")
public class AzureInputITTest {

    @Service
    private AzureComponentServices componentService;

    @ClassRule
    public static final SimpleComponentRule COMPONENT = new SimpleComponentRule("org.talend.components.azure");

    private static InputProperties inputProperties;

    @BeforeEach
    public void init() {
        Server account;
        AzureTableConnection dataSet = new AzureTableConnection();
        AzureConnection dataStore = new AzureConnection();
        final MavenDecrypter decrypter = new MavenDecrypter();
        account = decrypter.find("azure.account");
        dataStore.setAccountName(account.getUsername());
        dataStore.setAccountKey(account.getPassword());

        dataSet.setConnection(dataStore);
        dataSet.setTableName("myTable");
        inputProperties = new InputProperties();
        inputProperties.setAzureConnection(dataSet);
        Schema tableSchema = componentService.guessSchema(dataSet);
        inputProperties.setSchema(tableSchema.getEntries().stream().map(Schema.Entry::getName).collect(Collectors.toList()));
    }

    @Test
    public void pipelineInputTest() {
        String inputConfig = configurationByExample().forInstance(inputProperties).configured().toQueryString();
        Job.components().component("azureInput", "AzureStorage://InputTable?" + inputConfig)
                .component("collector", "test://collector").connections().from("azureInput").to("collector").build().run();

        List<Record> records = COMPONENT.getCollectedData(Record.class);

        Assertions.assertNotNull(records);
        Assertions.assertTrue(records.size() > 0);
    }

}
