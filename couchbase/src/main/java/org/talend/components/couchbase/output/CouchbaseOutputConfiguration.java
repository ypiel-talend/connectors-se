package org.talend.components.couchbase.output;

import lombok.extern.slf4j.Slf4j;
import org.talend.components.couchbase.dataset.CouchbaseDataSet;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@Slf4j
@GridLayout({
        // the generated layout put one configuration entry per line,
        // customize it as much as needed
        @GridLayout.Row({ "configuration1" }) })
@Documentation("TODO fill the documentation for this configuration")
public class CouchbaseOutputConfiguration implements Serializable {

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private CouchbaseDataSet configuration1;

    public CouchbaseDataSet getConfiguration1() {
        return configuration1;
    }

    public CouchbaseOutputConfiguration setConfiguration1(CouchbaseDataSet configuration1) {
        this.configuration1 = configuration1;
        return this;
    }
}