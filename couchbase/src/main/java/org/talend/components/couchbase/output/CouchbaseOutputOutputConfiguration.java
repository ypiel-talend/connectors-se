package org.talend.components.couchbase.output;

import java.io.Serializable;

import org.talend.components.couchbase.dataset.BaseDataSet;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@GridLayout({
        // the generated layout put one configuration entry per line,
        // customize it as much as needed
        @GridLayout.Row({ "configuration1" }) })
@Documentation("TODO fill the documentation for this configuration")
public class CouchbaseOutputOutputConfiguration implements Serializable {

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private BaseDataSet configuration1;

    public BaseDataSet getConfiguration1() {
        return configuration1;
    }

    public CouchbaseOutputOutputConfiguration setConfiguration1(BaseDataSet configuration1) {
        this.configuration1 = configuration1;
        return this;
    }
}