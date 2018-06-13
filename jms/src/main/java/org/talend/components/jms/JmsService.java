// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.components.jms;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JmsService {

    @Service
    private MessagesI18n i18n;

    @HealthCheck("")
    public HealthCheckStatus doHealthChecks(@Option final JmsDatastore datastore) {
        Connection connection = null;
        try {
            if (datastore.isUserIdentityRequired()) {
                connection = getConnectionFactory(datastore).createConnection(datastore.getUserId(), datastore.getPassword());
            } else {
                connection = getConnectionFactory(datastore).createConnection();
            }
            connection.start();
            return new HealthCheckStatus(HealthCheckStatus.Status.OK, i18n.connectionSuccessful());
        } catch (JMSException e) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, i18n.connectionFailed(e.getMessage()));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    log.warn(i18n.connectionCantCloseGracefully(e.getMessage()));
                }
            }
        }
    }

    public ConnectionFactory getConnectionFactory(final JmsDatastore datastore) {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, datastore.getContextProvider());
        env.put(Context.PROVIDER_URL, datastore.getServerUrl());
        try {
            return (ConnectionFactory) new InitialContext(env).lookup("ConnectionFactory");
        } catch (NamingException e) {
            throw new IllegalStateException(e);
        }
    }
}
