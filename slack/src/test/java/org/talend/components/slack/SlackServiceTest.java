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
 */
package org.talend.components.slack;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.talend.components.slack.connection.SlackConnection;
import org.talend.components.slack.dataset.SlackDataset;
import org.talend.components.slack.service.SlackService;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.junit5.WithComponents;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

@Slf4j
@WithComponents("org.talend.components.slack")
public class SlackServiceTest extends SlackTestBase {

    @Service
    private SlackService service;

    @Test
    @DisplayName("Test check auth")
    void testCheckAuth() {
        SlackConnection connection = new SlackConnection();
        connection.setToken("failed");
        Boolean result = service.checkAuth(connection);
        assertFalse(result);

        connection.setToken(TOKEN);
        result = service.checkAuth(connection);
        assertTrue(result);
    }

    @Test()
    @DisplayName("Test list channels")
    void testListChannels() {
        SlackConnection connection = new SlackConnection();
        connection.setToken(TOKEN);
        Map<String, String> results = service.listChannels(connection, SlackDataset.ChannelType.PRIVATE_CHANNEL);
        assertTrue(results.size() > 0);

        // too much response, and easy to get rate limitation of slack api
        // results = service.listChannels(connection, SlackDataset.ChannelType.PUBLIC_CHANNEL);
        // assertTrue(results.size() > 0);
    }
}
