package org.talend.components.azure.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

import com.microsoft.azure.storage.OperationContext;

public class AzureConnectionUtilsTest {

    @Test
    public void testOpContextCreatedForFirstTime() {
        OperationContext context = AzureConnectionUtils.getTalendOperationContext();

        assertNotNull(context);
        assertFalse(context.getUserHeaders().isEmpty());
        assertNotNull(context.getUserHeaders().get("User-Agent"));
    }

    @Test
    public void testOpContextIsSingleTone() {
        OperationContext contextFirst = AzureConnectionUtils.getTalendOperationContext();

        assertEquals(contextFirst, AzureConnectionUtils.getTalendOperationContext());
    }
}