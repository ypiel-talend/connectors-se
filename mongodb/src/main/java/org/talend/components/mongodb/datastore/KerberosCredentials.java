package org.talend.components.mongodb.datastore;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@Data
@GridLayout({ @GridLayout.Row({ "userPrincipal" }), @GridLayout.Row({ "realm" }), @GridLayout.Row({ "kdcServer" }) })
@Documentation("Credentials for Kerberos authentication type")
public class KerberosCredentials implements Serializable {

    @Option
    @Documentation("User principal")
    private String userPrincipal;

    @Option
    @Documentation("Kerberos Realm")
    private String realm;

    @Option
    @Documentation("KDC Server")
    private String kdcServer;

}
