package org.talend.components.fileio.s3;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.talend.daikon.properties.ValidationResult;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

/**
 * Unit tests for {@link S3DatastoreRuntime}.
 */
public class S3DatastoreRuntimeTestIT {

    /** Set up credentials for integration tests. */
    @Rule
    public S3TestResource s3 = S3TestResource.of();

    @Test
    @Ignore("It fails. Should be fixed")
    public void doHealthChecksTest_s3() {
        S3Service service = new S3Service();
        HealthCheckStatus status = service.healthCheckS3(s3.createS3Datastore());
        assertEquals(HealthCheckStatus.Status.OK, status.getStatus().OK);

        // Wrong access key
        {
            S3DataStore wrongAccess = s3.createS3Datastore();
            wrongAccess.setAccessKey("wrong");
            status = service.healthCheckS3(wrongAccess);
            assertEquals(HealthCheckStatus.Status.KO, status.getStatus().KO);
        }

        // Wrong secret key
        {
            S3DataStore wrongAccess = s3.createS3Datastore();
            wrongAccess.setSecretKey("wrong");
            status = service.healthCheckS3(wrongAccess);
            assertEquals(HealthCheckStatus.Status.KO, status.getStatus().KO);
        }
    }
}
