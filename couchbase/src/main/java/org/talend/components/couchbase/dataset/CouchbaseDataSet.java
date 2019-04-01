package org.talend.components.couchbase.dataset;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.talend.components.couchbase.datastore.CouchbaseDataStore;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Structure;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;
import java.util.List;

@Version(1)
@Slf4j
@Data
@DataSet("CouchbaseDataSet")
@GridLayout({ @GridLayout.Row({ "datastore" }), @GridLayout.Row({ "schema" }) })
@Documentation("CouchbaseDataSet")
public class CouchbaseDataSet implements Serializable {

    @Option
    @Documentation("")
    @Structure
    private List<String> schema;

    @Option
    @Documentation("Connection")
    private CouchbaseDataStore datastore;

}