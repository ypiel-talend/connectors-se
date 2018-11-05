package org.talend.components.azure.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.microsoft.azure.storage.OperationContext;

public class AzureTableUtilsTest {

    @Test
    public void testOpContextCreatedForFirstTime() {
        OperationContext context = AzureConnectionService.getTalendOperationContext();

        assertNotNull(context);
        assertFalse(context.getUserHeaders().isEmpty());
        assertNotNull(context.getUserHeaders().get(AzureConnectionService.USER_AGENT_KEY));
    }

    @Test
    public void testOpContextIsSingleTone() {
        OperationContext contextFirst = AzureConnectionService.getTalendOperationContext();

        assertEquals(contextFirst, AzureConnectionService.getTalendOperationContext());
    }
}