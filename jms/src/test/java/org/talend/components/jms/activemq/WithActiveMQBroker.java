package org.talend.components.jms.activemq;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(ActiveMQBrokerExtension.class)
public @interface WithActiveMQBroker {

    boolean persistent() default false;

    String brokerName() default "embedded-broker";

    boolean anonymousAccessAllowed() default true;

    User[] users() default @User(user = "anonymous");

    @interface User {

        String user();

        String password() default "";

        String groups() default "default";
    }
}
