package org.talend.components.couchbase.output;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.talend.components.couchbase.dataset.CouchbaseDataSet;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@Slf4j
@Data
@GridLayout({ @GridLayout.Row({ "dataSet" }), @GridLayout.Row({ "idFieldName" }), @GridLayout.Row({ "dieOnError" }) })
@Documentation("TODO fill the documentation for this configuration")
public class CouchbaseOutputConfiguration implements Serializable {

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private CouchbaseDataSet dataSet;

    @Option
    @Documentation("")
    private String idFieldName;

    @Option
    @Documentation("")
    private boolean dieOnError;
}