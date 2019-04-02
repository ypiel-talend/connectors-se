package org.talend.components.couchbase.source;

import lombok.Data;
import org.talend.components.couchbase.dataset.CouchbaseDataSet;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@Version(1)
@Data
@GridLayout({
        // the generated layout put one configuration entry per line,
        // customize it as much as needed
        @GridLayout.Row({ "dataSet" }) })
@Documentation("Couchbase input Mapper Configuration")
public class CouchbaseInputMapperConfiguration implements Serializable {

    @Option
    @Documentation("dataset")
    private CouchbaseDataSet dataSet;

    public CouchbaseDataSet getDataSet() {
        return dataSet;
    }

    public CouchbaseInputMapperConfiguration setDataSet(CouchbaseDataSet dataSet) {
        this.dataSet = dataSet;
        return this;
    }
}