package org.talend.components.couchbase.datastore;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

@DataStore("CouchBaseConnection")
@GridLayout({
        // the generated layout put one configuration entry per line,
        // customize it as much as needed
        @GridLayout.Row({ "bucket" }), @GridLayout.Row({ "password" }), @GridLayout.Row({ "bootstrapNodes" }) })
@Documentation("TODO fill the documentation for this configuration")
public class CouchBaseConnection implements Serializable {

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String bucket;

    @Credential
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String password;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String bootstrapNodes;

    public String getBucket() {
        return bucket;
    }

    public CouchBaseConnection setBucket(String bucket) {
        this.bucket = bucket;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public CouchBaseConnection setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getBootstrapNodes() {
        return bootstrapNodes;
    }

    public CouchBaseConnection setBootstrapNodes(String bootstrapNodes) {
        this.bootstrapNodes = bootstrapNodes;
        return this;
    }
}