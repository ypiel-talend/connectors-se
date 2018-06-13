package org.talend.components.jms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.talend.components.jms.activemq.ActiveMQBroker;
import org.talend.components.jms.activemq.WithActiveMQBroker;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.junit5.WithComponents;

@WithActiveMQBroker(anonymousAccessAllowed = false, users = {
        @WithActiveMQBroker.User(user = "broker1", password = "pwd", groups = "consumer") })
@WithComponents("org.talend.components.jms") //
class JmsServiceTest {

    @Service
    private JmsService jmsService;

    @Test
    void healthCheckAnonymousUser(final ActiveMQBroker broker) {
        final JmsDatastore datastore = new JmsDatastore();
        datastore.setContextProvider(broker.getContextProvider());
        datastore.setServerUrl("vm://" + broker.getBrokerName());
        datastore.setUserIdentityRequired(false);
        final HealthCheckStatus status = jmsService.doHealthChecks(datastore);
        assertNotNull(status);
        assertEquals(HealthCheckStatus.Status.KO, status.getStatus());
    }

    @Test
    void healthCheckWithCredentials(final ActiveMQBroker broker) {
        final JmsDatastore datastore = new JmsDatastore();
        datastore.setContextProvider(broker.getContextProvider());
        datastore.setServerUrl("vm://" + broker.getBrokerName());
        datastore.setUserIdentityRequired(true);
        datastore.setUserId("broker1");
        datastore.setPassword("pwd");
        final HealthCheckStatus status = jmsService.doHealthChecks(datastore);
        assertNotNull(status);
        assertEquals(HealthCheckStatus.Status.OK, status.getStatus());
    }
}
