package org.talend.components.mongodb.datastore;

import java.io.Serializable;

import lombok.Data;
import org.talend.components.mongodb.service.UIMongoDBService;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.condition.ActiveIfs;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@DataStore("MongoDBDatastore")
@Checkable(UIMongoDBService.HEALTH_CHECK)
@GridLayout({
        // the generated layout put one configuration entry per line,
        // customize it as much as needed
        @GridLayout.Row({ "server", "port" }), @GridLayout.Row({ "database" }), @GridLayout.Row({ "useSSL" }),
        @GridLayout.Row({ "authentication" }), @GridLayout.Row({ "authenticationMechanism" }),
        @GridLayout.Row({ "kerberosCreds" }), @GridLayout.Row({ "username", "password" }),
        @GridLayout.Row({ "setAuthenticationDatabase" }), @GridLayout.Row({ "authenticationDatabase" }) })
@Documentation("TODO fill the documentation for this configuration")
public class MongoDBDatastore implements Serializable {

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String server;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    @DefaultValue("27017")
    private int port;

    @Option
    @Documentation("Database")
    private String database;

    @Option
    @Documentation("Enable the SSL or TLS encrypted connection.")
    private boolean useSSL;

    @Option
    @Documentation("Enable the database authentication.")
    private boolean authentication;

    @Option
    @ActiveIf(target = "authentication", value = "true")
    @Documentation("Among the mechanisms listed on the Authentication mechanism drop-down list, the NEGOTIATE one is recommended if you are not using Kerberos, because it automatically select the authentication mechanism the most adapted to the MongoDB version you are using.")
    @DefaultValue("NEGOTIATE_MEC")
    private AuthenticationMechanism authenticationMechanism;

    public enum AuthenticationMechanism {
        NEGOTIATE_MEC,
        PLAIN_MEC,
        SCRAMSHA1_MEC,
        KERBEROS_MEC
    }

    @Option
    @ActiveIfs(value = { @ActiveIf(target = "authentication", value = "true"),
            @ActiveIf(target = "authenticationMechanism", value = { "NEGOTIATE_MEC", "SCRAMSHA1_MEC" }) })
    @Documentation("If the username to be used to connect to MongoDB has been created in a specific Authentication database of MongoDB, select this check box to enter the name of this Authentication database in the Authentication database field that is displayed.")
    private boolean setAuthenticationDatabase;

    @Option
    @ActiveIfs(value = { @ActiveIf(target = "authentication", value = "true"),
            @ActiveIf(target = "setAuthenticationDatabase", value = "true"),
            @ActiveIf(target = "authenticationMechanism", value = { "NEGOTIATE_MEC", "SCRAMSHA1_MEC" }) })
    @Documentation("Set MongoDB Authentication database")
    private String authenticationDatabase;

    @Option
    @ActiveIfs(value = { @ActiveIf(target = "authentication", value = "true"),
            @ActiveIf(target = "authenticationMechanism", value = { "NEGOTIATE_MEC", "SCRAMSHA1_MEC", "PLAIN_MEC" }) })
    @Documentation("Enter the username")
    private String username;

    @Option
    @Credential
    @ActiveIfs(value = { @ActiveIf(target = "authentication", value = "true"),
            @ActiveIf(target = "authenticationMechanism", value = { "NEGOTIATE_MEC", "SCRAMSHA1_MEC", "PLAIN_MEC" }) })
    @Documentation("Enter the password")
    private String password;

    @Option
    @Credential
    @ActiveIfs(value = { @ActiveIf(target = "authentication", value = "true"),
            @ActiveIf(target = "authenticationMechanism", value = { "KERBEROS_MEC" }) })
    @Documentation("Credentials for Kerberos authentication")
    private KerberosCredentials kerberosCreds;

}