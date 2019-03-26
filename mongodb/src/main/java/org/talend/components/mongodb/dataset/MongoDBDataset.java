package org.talend.components.mongodb.dataset;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import org.talend.components.mongodb.datastore.MongoDBDatastore;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Structure;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@DataSet("MongoDBDataset")
@GridLayout({
        // the generated layout put one configuration entry per line,
        // customize it as much as needed
        @GridLayout.Row({ "datastore" }), @GridLayout.Row({ "collection" }), @GridLayout.Row({ "schema" }) })
@Documentation("TODO fill the documentation for this configuration")
public class MongoDBDataset implements Serializable {

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private MongoDBDatastore datastore;

    @Option
    @Documentation("collection")
    private String collection;

    @Option
    @Structure
    @Documentation("schema")
    private List<String> schema;
}