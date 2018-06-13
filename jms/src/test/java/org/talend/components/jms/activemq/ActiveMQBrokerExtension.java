package org.talend.components.jms.activemq;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.security.AuthenticationUser;
import org.apache.activemq.security.SimpleAuthenticationPlugin;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActiveMQBrokerExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

    private BrokerService brokerService;

    @Override
    public void beforeAll(final ExtensionContext context) {
        final WithActiveMQBroker element = context.getElement().map(e -> e.getAnnotation(WithActiveMQBroker.class)).orElseThrow(
                () -> new IllegalArgumentException("No annotation @WithActiveMQBroker on " + context.getRequiredTestClass()));
        this.brokerService = new BrokerService();
        brokerService.setPersistent(element.persistent());
        brokerService.setBrokerName(element.brokerName());
        final List<AuthenticationUser> users = Stream.of(element.users()).filter(user -> !"anonymous".equals(user.user()))
                .map(u -> new AuthenticationUser(u.user(), u.password(), u.groups())).collect(Collectors.toList());
        if (!users.isEmpty()) {
            final SimpleAuthenticationPlugin simpleAuthenticationPlugin = new SimpleAuthenticationPlugin(users);
            simpleAuthenticationPlugin.setAnonymousAccessAllowed(element.anonymousAccessAllowed());
            brokerService.setPlugins(new BrokerPlugin[] { simpleAuthenticationPlugin });
        }
        try {
            brokerService.start();
            brokerService.waitUntilStarted();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void afterAll(final ExtensionContext context) {
        if (brokerService != null) {
            try {
                brokerService.stop();
            } catch (Exception e) {
                log.warn("Can't stop embedded broker gracefully ", e);
            }
        }
    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(ActiveMQBroker.class);
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return new ActiveMQBroker(brokerService.getBrokerName());
    }
}
