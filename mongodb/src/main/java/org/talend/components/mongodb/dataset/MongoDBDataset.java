package org.talend.components.mongodb.dataset;

import java.io.Serializable;

import org.talend.components.mongodb.datastore.MongoDBDatastore;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@DataSet("MongoDBDataset")
@GridLayout({
        // the generated layout put one configuration entry per line,
        // customize it as much as needed
        @GridLayout.Row({ "datastore" }) })
@Documentation("TODO fill the documentation for this configuration")
public class MongoDBDataset implements Serializable {

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private MongoDBDatastore datastore;

    public MongoDBDatastore getDatastore() {
        return datastore;
    }

    public MongoDBDataset setDatastore(MongoDBDatastore datastore) {
        this.datastore = datastore;
        return this;
    }
}