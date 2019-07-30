/**
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.talend.components.mail.service;

import static java.util.Optional.ofNullable;

import java.util.OptionalInt;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.talend.components.mail.configuration.MailConnection;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

@Service
public class MailService {

    @HealthCheck("MailConnectionCheck")
    public HealthCheckStatus check(final MailConnection connection) {
        try {
            final Session session = createSession(connection);
            final String protocol = session.getProperty("mail.transport.protocol");
            final javax.mail.Service service;
            switch (connection.getTransport()) {
            case POP3:
            case POP3S:
            case IMAP:
            case IMAPS:
                service = session.getStore();
                break;
            default:
                service = session.getTransport();
            }
            try {
                service.connect(session.getProperty("mail." + protocol + ".host"),
                        Integer.parseInt(session.getProperty("mail." + protocol + ".port")),
                        session.getProperty("mail." + protocol + ".user"), session.getProperty("mail." + protocol + ".password"));
                return new HealthCheckStatus(HealthCheckStatus.Status.OK, "Connected successfully.");
            } finally {
                if (service.isConnected()) {
                    service.close();
                }
            }
        } catch (final MessagingException | RuntimeException me) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, me.getMessage());
        }
    }

    public Session createSession(final MailConnection connection) {
        final String protocol = ofNullable(connection.getTransport()).map(MailConnection.Transport::getProtocol).orElse("smtp");
        final Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", protocol);
        properties.setProperty("mail.store.protocol", protocol);
        properties.setProperty("mail." + protocol + ".host", connection.getHost());
        properties.setProperty("mail." + protocol + ".port", Integer.toString(OptionalInt.of(connection.getPort()).orElse(25)));
        if (connection.isTls()) {
            properties.setProperty("mail." + protocol + ".starttls.enable", "true");
        }
        if (connection.getUsername() != null && !connection.getUsername().isEmpty()) {
            properties.setProperty("mail." + protocol + ".user", connection.getUsername());
        }
        if (connection.isAuth() || (connection.getPassword() != null && !connection.getPassword().isEmpty())) {
            properties.setProperty("mail." + protocol + ".auth", "true");
        }
        if (connection.getTimeout() > 0) {
            properties.setProperty("mail." + protocol + ".timeout", Integer.toString(connection.getTimeout()));
            properties.setProperty("mail." + protocol + ".connectiontimeout", Integer.toString(connection.getTimeout()));
            properties.setProperty("mail." + protocol + ".writetimeout", Integer.toString(connection.getTimeout()));
        }
        if (connection.getProperties() != null) {
            connection.getProperties().forEach(p -> properties.setProperty(p.getName(), p.getValue()));
        }
        if ((connection.getPassword() != null && !connection.getPassword().isEmpty())
                && (connection.getUsername() != null && !connection.getUsername().isEmpty())) {
            properties.setProperty("password", connection.getPassword());

            final PasswordAuthentication passwordAuthentication = new PasswordAuthentication(connection.getUsername(),
                    connection.getPassword());
            return Session.getInstance(properties, new Authenticator() {

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return passwordAuthentication;
                }
            });
        }
        return Session.getInstance(properties);
    }
}
