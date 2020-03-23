package org.talend.components.ftp.output;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FTPOutputConfigurationTest {

    @Test
    public void testLimitByFlags() {
        Assertions.assertTrue(FTPOutputConfiguration.LimitBy.RECORDS.isLimitedByRecords(), "LimitBy.RECORDS should limit records");
        Assertions.assertFalse(FTPOutputConfiguration.LimitBy.RECORDS.isLimitedBySize(), "LimitBy.RECORDS should not limit size");
        Assertions.assertFalse(FTPOutputConfiguration.LimitBy.SIZE.isLimitedByRecords(), "LimitBy.SIZE should limit records");
        Assertions.assertTrue(FTPOutputConfiguration.LimitBy.SIZE.isLimitedBySize(), "LimitBy.SIZE should not limit size");
        Assertions.assertTrue(FTPOutputConfiguration.LimitBy.SIZE_AND_RECORDS.isLimitedByRecords(), "LimitBy.SIZE_AND_RECORDS should limit records");
        Assertions.assertTrue(FTPOutputConfiguration.LimitBy.SIZE_AND_RECORDS.isLimitedBySize(), "LimitBy.SIZE_AND_RECORDS should limit size");
    }
}
