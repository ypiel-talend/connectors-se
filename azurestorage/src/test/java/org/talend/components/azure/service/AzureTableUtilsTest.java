package org.talend.components.azure.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

import com.microsoft.azure.storage.OperationContext;

public class AzureTableUtilsTest {

    @Test
    public void testOpContextCreatedForFirstTime() {
        OperationContext context = AzureTableUtils.getTalendOperationContext();

        assertNotNull(context);
        assertFalse(context.getUserHeaders().isEmpty());
        assertNotNull(context.getUserHeaders().get("User-Agent"));
    }

    @Test
    public void testOpContextIsSingleTone() {
        OperationContext contextFirst = AzureTableUtils.getTalendOperationContext();

        assertEquals(contextFirst, AzureTableUtils.getTalendOperationContext());
    }
}