package org.talend.components.netsuite.datastore;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayouts;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@DataStore("NetsuiteConnection")
@Checkable("connection.healthcheck")
@GridLayouts({
        @GridLayout({ @GridLayout.Row({ "endpoint" }), @GridLayout.Row({ "email" }), @GridLayout.Row({ "password" }),
                @GridLayout.Row({ "role" }), @GridLayout.Row({ "account" }), @GridLayout.Row({ "applicationId" }) }),
        @GridLayout(names = { GridLayout.FormType.ADVANCED }, value = { @GridLayout.Row({ "enableCustomization" }) }) })
public class NetsuiteDataStore {

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String endpoint = "https://webservices.na2.netsuite.com/services/NetSuitePort_2018_2";

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String email;

    @Option
    @Credential
    @Documentation("TODO fill the documentation for this parameter")
    private String password;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private int role;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String account;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String applicationId;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    @DefaultValue(value = "true")
    private boolean enableCustomization;
}
