package org.talend.components.couchbase.datastore;

import lombok.Data;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@Version(1)
@Data
@DataStore("CouchbaseDataStore")
@Checkable("healthCheck")
@GridLayout({ @GridLayout.Row({ "bucket" }), @GridLayout.Row({ "password" }), @GridLayout.Row({ "bootstrapNodes" }) })
@Documentation("Couchbase connection")
public class CouchbaseDataStore implements Serializable {

    @Option
    @Required
    @Documentation("Bucket name")
    private String bucket;

    @Required
    @Option
    @Credential
    @Documentation("Password")
    private String password;

    @Option
    @Required
    @Documentation("Bootstrap nodes")
    private String bootstrapNodes;
}