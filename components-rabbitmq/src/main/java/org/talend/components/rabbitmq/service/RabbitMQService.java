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
package org.talend.components.rabbitmq.service;

import com.rabbitmq.client.BlockedListener;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.talend.components.rabbitmq.datastore.RabbitMQDataStore;
import org.talend.components.rabbitmq.exception.CreateChannelException;
import org.talend.sdk.component.api.service.Service;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class RabbitMQService {

    @Service
    private I18nMessage i18n;

    public Connection getConnection(RabbitMQDataStore store) throws IOException, TimeoutException, NoSuchAlgorithmException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(store.getUserName());
        factory.setPassword(store.getPassword());
        factory.setHost(store.getHostname());
        factory.setPort(store.getPort());
        if (store.getTLS()) {
            factory.useSslProtocol(SSLContext.getDefault());
        }
        Connection conn = factory.newConnection();
        conn.addBlockedListener(new BlockedListener() {

            @Override
            public void handleBlocked(String s) throws IOException {
                System.out.println("The connection is Blocked");
            }

            @Override
            public void handleUnblocked() throws IOException {
                System.out.println("The connection is UnBlocked");
            }
        });
        log.debug("create connection to rabbit: " + ":" + conn);
        return conn;
    }

    public Channel createChannel(Connection connection) {
        log.debug("create channel on connection" + ":" + connection);

        Channel channel;
        try {
            channel = connection.createChannel();
        } catch (IOException e) {
            throw new CreateChannelException(i18n.errorCantCreateChannel(), e);
        }
        log.debug("create channel on connection finished" + ":" + connection + ":" + channel);
        return channel;
    }

    public void closeConnection(Connection connection) {
        log.debug("closing connection" + ":" + connection);
        if (connection != null) {
            try {
                connection.close();
                System.out.println("closing connection finish" + ":" + connection);

            } catch (IOException e) {
                log.warn(i18n.warnConnectionCantBeClosed(), e);
            }
        }
    }

}
