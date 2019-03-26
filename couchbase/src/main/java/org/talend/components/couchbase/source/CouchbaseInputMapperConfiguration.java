package org.talend.components.couchbase.source;

import java.io.Serializable;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.talend.components.couchbase.dataset.CouchbaseDataSet;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@Slf4j
@Data
@GridLayout({
        // the generated layout put one configuration entry per line,
        // customize it as much as needed
        @GridLayout.Row({ "dataSet" }) })
@Documentation("TODO fill the documentation for this configuration")
public class CouchbaseInputMapperConfiguration implements Serializable {

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private CouchbaseDataSet dataSet;

    public CouchbaseDataSet getDataSet() {
        return dataSet;
    }

    public CouchbaseInputMapperConfiguration setDataSet(CouchbaseDataSet dataSet) {
        this.dataSet = dataSet;
        return this;
    }
}