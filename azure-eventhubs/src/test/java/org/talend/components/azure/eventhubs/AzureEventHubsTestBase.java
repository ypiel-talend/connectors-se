/*
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */

package org.talend.components.azure.eventhubs;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

import org.talend.components.azure.eventhubs.datastore.AzureEventHubsDataStore;
import org.talend.sdk.component.api.DecryptedServer;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithMavenServers;
import org.talend.sdk.component.maven.Server;

import lombok.Data;

@Data
@WithMavenServers
public class AzureEventHubsTestBase implements Serializable {

    public static String ENDPOINT = "sb://" +
            System.getProperty("talend.components.azure.event-hubs.namespace", "comp-test") +
            ".servicebus.windows.net";

    protected static final String SHARED_EVENTHUB_NAME = "eh-test";

    protected static final String CONSUME_GROUP = "consumer-group-1";

    @DecryptedServer("azure-eventhubs-saskey") // todo: passthrough/mocked mode to enable local run without credentials?
    protected Server server;

    @Injected
    private BaseComponentsHandler componentsHandler;

    protected AzureEventHubsDataStore getDataStore() {
        final AzureEventHubsDataStore dataStore = new AzureEventHubsDataStore();
        dataStore.setEndpoint(ENDPOINT);
        dataStore.setSasKeyName(server.getUsername());
        dataStore.setSasKey(server.getPassword());
        return dataStore;
    }

    protected String getUniqueID() {
        return Integer.toString(ThreadLocalRandom.current().nextInt(1, 100000));
    }
}