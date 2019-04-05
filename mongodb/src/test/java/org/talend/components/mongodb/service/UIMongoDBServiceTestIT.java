package org.talend.components.mongodb.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.talend.components.mongodb.utils.MongoDBTestExtension;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.junit5.WithComponents;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WithComponents("org.talend.components.mongodb")
@ExtendWith(MongoDBTestExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UIMongoDBServiceTestIT {

    @Service
    private UIMongoDBService uiMongoDBService;

    private MongoDBTestExtension.TestContext testContext;

    @BeforeAll
    private void init(MongoDBTestExtension.TestContext testContext) {
        this.testContext = testContext;
    }

    @Test
    public void testSuccessfulConnection() {
        HealthCheckStatus status = uiMongoDBService.testConnection(testContext.getDataStore());

        assertEquals(HealthCheckStatus.Status.OK, status.getStatus());
    }

}
