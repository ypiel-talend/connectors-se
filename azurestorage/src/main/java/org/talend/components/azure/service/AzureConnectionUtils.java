package org.talend.components.azure.service;

import com.microsoft.azure.storage.OperationContext;
import lombok.Data;

import java.util.HashMap;

@Data
public class AzureConnectionUtils {

    private static OperationContext talendOperationContext;

    private static final String USER_AGENT_KEY = "User-Agent";

    private static final String USER_AGENT_VALUE = "APN/1.0 Talend/7.1 TaCoKit/1.0.3";

    public static final String TABLE_TIMESTAMP = "Timestamp";

    public static OperationContext getTalendOperationContext() {
        if (talendOperationContext == null) {
            talendOperationContext = new OperationContext();
            HashMap<String, String> talendUserHeaders = new HashMap<>();
            talendUserHeaders.put(USER_AGENT_KEY, USER_AGENT_VALUE);
            talendOperationContext.setUserHeaders(talendUserHeaders);
        }

        return talendOperationContext;
    }
}
