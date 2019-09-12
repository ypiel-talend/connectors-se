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

import org.talend.components.jms.configuration.BasicConfiguration;
import org.talend.components.jms.datastore.JmsDataStore;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.DynamicValues;
import org.talend.sdk.component.api.service.completion.Values;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.api.service.schema.DiscoverSchema;
import org.talend.sdk.component.api.record.Schema;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.NamingException;
import java.net.URLClassLoader;

import static java.util.stream.Collectors.toList;
import static org.talend.components.jms.MessageConst.MESSAGE_CONTENT;

@Service
public class ActionService {

    public static final String ACTION_LIST_SUPPORTED_BROKER = "ACTION_LIST_SUPPORTED_BROKER";

    public static final String ACTION_BASIC_HEALTH_CHECK = "ACTION_BASIC_HEALTH_CHECK";

    public static final String DISCOVER_SCHEMA = "discoverSchema";

    @Service
    private JmsService jmsService;

    @Service
    private I18nMessage i18n;

    @Service
    private RecordBuilderFactory recordBuilderFactory;

    @DynamicValues(ACTION_LIST_SUPPORTED_BROKER)
    public Values loadSupportedJMSProviders() {
        return new Values(jmsService.getProviders().keySet().stream().map(id -> new Values.Item(id, id)).collect(toList()));
    }

    // @DiscoverSchema(DISCOVER_SCHEMA)
    // public Schema guessSchema(BasicConfiguration config) {
    // return recordBuilderFactory.newSchemaBuilder(Schema.Type.RECORD)
    // .withEntry(recordBuilderFactory.newEntryBuilder().withName(MESSAGE_CONTENT).withType(Schema.Type.STRING).build())
    // .build();
    // }

    @HealthCheck(ACTION_BASIC_HEALTH_CHECK)
    public HealthCheckStatus validateBasicDatastore(@Option final JmsDataStore datastore) {
        if (datastore.getUrl() == null || datastore.getUrl().isEmpty()) {
            throw new IllegalArgumentException(i18n.errorEmptyURL());
        }
        final URLClassLoader loader = jmsService.getProviderClassLoader(datastore.getModuleList());
        if (loader == null) {
            throw new IllegalStateException(i18n.errorLoadProvider(datastore.getModuleList(), null));
        }

        Context jndiContext = null;
        Connection connection = null;

        try {
            // create JNDI context
            jndiContext = jmsService.getJNDIContext(datastore.getUrl(), datastore.getModuleList());
            // create ConnectionFactory from JNDI
            ConnectionFactory connectionFactory = jmsService.getConnectionFactory(jndiContext);

            try {
                connection = jmsService.getConnection(connectionFactory, datastore.isUserIdentity(), datastore.getUserName(),
                        datastore.getPassword());
                connection.start();
            } catch (JMSException e) {
                return new HealthCheckStatus(HealthCheckStatus.Status.KO, i18n.errorInvalidConnection());
            }

        } catch (ClassNotFoundException | NamingException | IllegalAccessException | InstantiationException e) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, i18n.errorInstantiateConnectionFactory(e.getMessage()));
        } finally {
            jmsService.closeConnection(connection);
            jmsService.closeContext(jndiContext);
        }

        return new HealthCheckStatus(HealthCheckStatus.Status.OK, i18n.successConnection());
    }

}
