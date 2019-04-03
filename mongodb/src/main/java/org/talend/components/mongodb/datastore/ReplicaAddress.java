package org.talend.components.mongodb.datastore;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@GridLayout({ @GridLayout.Row({ "address", "port" }) })
@Documentation("Replica server addresses for MongoDB components")
public class ReplicaAddress {

    @Option
    @Documentation("Replica server")
    private String address;

    @Option
    @Documentation("Replica port")
    @DefaultValue("27017")
    private int port;
}
