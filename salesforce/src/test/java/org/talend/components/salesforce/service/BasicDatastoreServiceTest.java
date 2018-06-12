package org.talend.components.salesforce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.talend.components.salesforce.datastore.BasicDataStore;
import org.talend.sdk.component.api.DecryptedServer;
import org.talend.sdk.component.api.service.configuration.LocalConfiguration;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit.http.api.HttpApiHandler;
import org.talend.sdk.component.junit.http.junit5.HttpApi;
import org.talend.sdk.component.junit.http.junit5.HttpApiInject;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.junit5.WithMavenServers;
import org.talend.sdk.component.maven.MavenDecrypter;
import org.talend.sdk.component.maven.Server;

@WithComponents("org.talend.components.salesforce")
@HttpApi(useSsl = true)
@WithMavenServers //
class BasicDatastoreServiceTest {

    static {
        // System.setProperty("talend.junit.http.capture", "true");
        System.setProperty("talend.junit.http.passthrough", "false");
    }

    @Injected
    private BaseComponentsHandler componentsHandler;

    @HttpApiInject
    private HttpApiHandler<?> httpApiHandler;

    private final static MavenDecrypter mavenDecrypter = new MavenDecrypter();

    @DecryptedServer(value = "salesforce-password")
    private Server serverWithPassword;

    @DecryptedServer(value = "salesforce-securitykey")
    private Server serverWithSecuritykey;

    @Test
    void validateBasicConnectionOK() {
        final BasicDatastoreService service = componentsHandler.findService(BasicDatastoreService.class);
        final Messages i18n = componentsHandler.findService(Messages.class);
        final LocalConfiguration configuration = componentsHandler.findService(LocalConfiguration.class);
        final BasicDataStore datasore = new BasicDataStore();
        datasore.setUserId(serverWithPassword.getUsername());
        datasore.setPassword(serverWithPassword.getPassword());
        datasore.setSecurityKey(serverWithSecuritykey.getPassword());
        final HealthCheckStatus status = service.validateBasicConnection(datasore, i18n, configuration);
        assertEquals(i18n.healthCheckOk(), status.getComment());
        assertEquals(HealthCheckStatus.Status.OK, status.getStatus());

    }

    @Test
    void validateBasicConnectionFailed() {
        final BasicDatastoreService service = componentsHandler.findService(BasicDatastoreService.class);
        final Messages i18n = componentsHandler.findService(Messages.class);
        final LocalConfiguration configuration = componentsHandler.findService(LocalConfiguration.class);
        final HealthCheckStatus status = service.validateBasicConnection(new BasicDataStore(), i18n, configuration);
        assertNotNull(status);
        assertEquals(HealthCheckStatus.Status.KO, status.getStatus());
        assertFalse(status.getComment().isEmpty());
    }

}
