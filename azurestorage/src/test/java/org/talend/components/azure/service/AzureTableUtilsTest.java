// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
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