package org.talend.components.azure.service;

import org.talend.components.azure.datastore.AzureConnection;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

@Service
public class ComponentAzureBlobService {

    public static final String TEST_CONNECTION = "testConnection";

    @Service
    private MessageService i18nService;

    @HealthCheck(TEST_CONNECTION)
    public HealthCheckStatus testConnection(@Option AzureConnection azureConnection) {
        try {
            System.out.println(azureConnection.getAccountKey());
            // NOOP for now
        } catch (Exception e) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, e.getMessage());
        }
        return new HealthCheckStatus(HealthCheckStatus.Status.OK, i18nService.connected());
    }

}