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
package org.talend.components.jms.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.talend.components.jms.datastore.JmsDataStore;
import org.talend.components.jms.testutils.JMSTestExtention;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.junit5.WithComponents;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.talend.components.jms.testutils.JmsTestConstants.JMS_PROVIDER;
import static org.talend.components.jms.testutils.JmsTestConstants.PASSWORD;
import static org.talend.components.jms.testutils.JmsTestConstants.USERNAME;

@WithComponents("org.talend.components.jms") // component package
@ExtendWith(JMSTestExtention.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActionServiceTestIT {

    @Service
    private ActionService actionService;

    private JMSTestExtention.TestContext testContext;

    @BeforeAll
    private void init(JMSTestExtention.TestContext testContext) {
        this.testContext = testContext;
    }

    @Test
    public void testJMSSuccessfulConnection() throws InterruptedException {

        JmsDataStore dataStore = new JmsDataStore();
        dataStore.setModuleList(JMS_PROVIDER);
        dataStore.setUserIdentity(true);
        dataStore.setUrl(testContext.getURL());
        dataStore.setUserName(USERNAME);
        dataStore.setPassword(PASSWORD);
        HealthCheckStatus status = actionService.validateBasicDatastore(dataStore);

        assertEquals(HealthCheckStatus.Status.OK, status.getStatus());
    }

}
