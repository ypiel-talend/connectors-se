package org.talend.components.couchbase.source;

import java.io.Serializable;

import org.talend.components.couchbase.dataset.BaseDataSet;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@GridLayout({
        // the generated layout put one configuration entry per line,
        // customize it as much as needed
        @GridLayout.Row({ "dataset" }) })
@Documentation("TODO fill the documentation for this configuration")
public class CouchbaseInputMapperConfiguration implements Serializable {

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private BaseDataSet dataset;

    public BaseDataSet getDataset() {
        return dataset;
    }

    public CouchbaseInputMapperConfiguration setDataset(BaseDataSet dataset) {
        this.dataset = dataset;
        return this;
    }
}