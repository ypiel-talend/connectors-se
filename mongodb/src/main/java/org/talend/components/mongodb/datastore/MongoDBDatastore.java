package org.talend.components.mongodb.datastore;

import java.io.Serializable;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@DataStore("MongoDBDatastore")
@GridLayout({
        // the generated layout put one configuration entry per line,
        // customize it as much as needed
        @GridLayout.Row({ "server" }), @GridLayout.Row({ "port" }) })
@Documentation("TODO fill the documentation for this configuration")
public class MongoDBDatastore implements Serializable {

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String server;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private int port;

}