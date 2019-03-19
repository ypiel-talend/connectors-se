package org.talend.components.couchbase.dataset;

import java.io.Serializable;

import org.talend.components.couchbase.datastore.CouchBaseConnection;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@DataSet("BaseDataSet")
@GridLayout({
        // the generated layout put one configuration entry per line,
        // customize it as much as needed
        @GridLayout.Row({ "datastore" }) })
@Documentation("TODO fill the documentation for this configuration")
public class BaseDataSet implements Serializable {

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private CouchBaseConnection datastore;

    public CouchBaseConnection getDatastore() {
        return datastore;
    }

    public BaseDataSet setDatastore(CouchBaseConnection datastore) {
        this.datastore = datastore;
        return this;
    }
}