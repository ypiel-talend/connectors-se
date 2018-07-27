package org.talend.components.azure.common;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

@GridLayout({
        @GridLayout.Row({"accountName"}),
        @GridLayout.Row({"accountKey"}),
        @GridLayout.Row({"protocol"}),
        @GridLayout.Row({"useAzureSharedSignature"}),
        @GridLayout.Row({"azureSharedAccessSignature"})
})
@Data
@DataStore
@Checkable("testConnection")
public class AzureConnection {
    @Option("accountName")
    @Documentation("Blah-blah")
    @ActiveIf(target = "useAzureSharedSignature", value = "false")
    private String accountName;

    @Option
    @Documentation("Blah-blah-key")
    @Credential
    @ActiveIf(target = "useAzureSharedSignature", value = "false")
    private String accountKey;

    @Option
    @Documentation("Protocol")
    @ActiveIf(target = "useAzureSharedSignature", value = "false")
    @DefaultValue("HTTPS")
    private Protocol protocol = Protocol.HTTPS;

    @Option
    @Documentation("haha")
    private boolean useAzureSharedSignature = false;

    @Option
    @Documentation("Tutu")
    @ActiveIf(target = "useAzureSharedSignature", value = "true")
    private String azureSharedAccessSignature;
}
