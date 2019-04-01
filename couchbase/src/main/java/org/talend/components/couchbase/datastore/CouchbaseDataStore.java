package org.talend.components.couchbase.datastore;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Data
@DataStore("CouchbaseDataStore")
@Checkable("healthCheck")
@GridLayout({ @GridLayout.Row({ "bucket" }), @GridLayout.Row({ "password" }), @GridLayout.Row({ "bootstrapNodes" }) })
@Documentation("Couchbase connection")
public class CouchbaseDataStore implements Serializable {

    @Option
    @Required
    @Documentation("TODO fill the documentation for this parameter")
    private String bucket;

    @Required
    @Option
    @Credential
    @Documentation("TODO fill the documentation for this parameter")
    private String password;

    @Option
    @Required
    @Documentation("TODO fill the documentation for this parameter")
    private String bootstrapNodes;
}