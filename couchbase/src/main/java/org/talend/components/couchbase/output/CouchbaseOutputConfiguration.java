package org.talend.components.couchbase.output;

import lombok.Data;
import org.talend.components.couchbase.dataset.CouchbaseDataSet;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@Version(1)
@Data
@GridLayout({ @GridLayout.Row({ "dataSet" }), @GridLayout.Row({ "idFieldName" }) })
@Documentation("Couchbase input configuration")
public class CouchbaseOutputConfiguration implements Serializable {

    @Option
    @Documentation("Dataset")
    private CouchbaseDataSet dataSet;

    @Option
    @Required
    @Documentation("Field to use as ID")
    private String idFieldName;

}