package org.talend.components.mongodb.source;

import java.io.Serializable;

import org.talend.components.mongodb.dataset.MongoDBDataset;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@GridLayout({
        // the generated layout put one configuration entry per line,
        // customize it as much as needed
        @GridLayout.Row({ "dataset" }) })
@Documentation("TODO fill the documentation for this configuration")
public class MongoDBInputMapperConfiguration implements Serializable {

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private MongoDBDataset dataset;

    public MongoDBDataset getDataset() {
        return dataset;
    }

    public MongoDBInputMapperConfiguration setDataset(MongoDBDataset dataset) {
        this.dataset = dataset;
        return this;
    }
}