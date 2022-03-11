package org.talend.components.common.service.http.bearer;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.talend.components.common.service.http.ConfConnectionFake;
import org.talend.sdk.component.api.service.http.Configurer;

class BearerAuthConfigurerTest {

    @Test
    void configure() {
        final BearerAuthConfigurer configurer = new BearerAuthConfigurer();

        Configurer.ConfigurerConfiguration cfg = new Configurer.ConfigurerConfiguration() {
            @Override
            public Object[] configuration() {
                return new Object[0];
            }

            @Override
            public <T> T get(String name, Class<T> type) {
                if (type == String.class) {
                    return (T) "zzz";
                }
                return null;
            }
        };

        final ConfConnectionFake cnx = new ConfConnectionFake("GET", "http://test", "content");
        Assertions.assertNull(cnx.getHeaders().get("Authorization"));
        configurer.configure(cnx, cfg);
        final List<String> authorization = cnx.getHeaders().get("Authorization");
        Assertions.assertNotNull(authorization);
        Assertions.assertEquals(1, authorization.size());
        Assertions.assertEquals("Bearer zzz", authorization.get(0));
    }
}