package org.talend.components.jms.activemq;

import lombok.Data;

@Data
public class ActiveMQBroker {

    private final String contextProvider = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";

    private final String brokerName;

}
