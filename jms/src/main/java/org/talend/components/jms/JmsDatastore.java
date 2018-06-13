package org.talend.components.jms;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@GridLayout({ @GridLayout.Row("contextProvider"), @GridLayout.Row("serverUrl"), @GridLayout.Row("connectionFactoryName"),
        @GridLayout.Row("userIdentityRequired"), @GridLayout.Row("userId"), @GridLayout.Row("password"),
        @GridLayout.Row("useHttps"), @GridLayout.Row("httpsSettings"), })
@DataStore("JmsDatastore")
public class JmsDatastore {

    @Option
    @Required
    @Documentation("")
    private String contextProvider = "com.tibco.tibjms.naming.TibjmsInitialContextFactory";

    @Option
    @Documentation("")
    private String serverUrl = "tibjmsnaming://localhost:7222"; // use a place holder instead

    @Option
    @Documentation("")
    private String connectionFactoryName = "GenericConnectionFactory";

    @Option
    @Documentation("")
    private boolean userIdentityRequired = false;

    @Option
    @ActiveIf(target = "userIdentityRequired", value = { "true" })
    @Documentation("")
    private String userId;

    @Option
    @Credential
    @ActiveIf(target = "userIdentityRequired", value = { "true" })
    @Documentation("")
    private String password;

    // Those advanced settings could be either in the datastore or in the dataset
    @Option
    @Documentation("")
    private Boolean useHttps = false;

    @Option
    @ActiveIf(target = "useHttps", value = { "true" })
    @Documentation("")
    private String httpsSettings;

}
