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
        System.out.println("Connection to rabbit: " + conn.getId() + ":" + conn);
        return conn;
    }

    public Channel createChannel(Connection connection) {
        System.out.println("create channel on connection" + (connection == null ? null : connection.getId()) + ":" + connection);

        Channel channel;
        try {
            channel = connection.createChannel();
        } catch (IOException e) {
            throw new CreateChannelException(i18n.errorCantCreateChannel(), e);
        }

        System.out.println("finish create channel on connection" + (connection == null ? null : connection.getId()) + ":"
                + connection + ":" + channel);
        return channel;
    }

    public void closeConnection(Connection connection) {
        System.out.println("closing connection" + (connection == null ? null : connection.getId()) + ":" + connection);
        if (connection != null) {
            try {
                System.out.println("closing connection wait" + (connection == null ? null : connection.getId()) + ":" + connection);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("closing connection stop waiting" + (connection == null ? null : connection.getId()) + ":" + connection);
                connection.close();
                System.out.println("closing connection finish" + (connection == null ? null : connection.getId()) + ":" + connection);

            } catch (IOException e) {
                log.warn(i18n.warnConnectionCantBeClosed(), e);
            }
        }
    }

}
