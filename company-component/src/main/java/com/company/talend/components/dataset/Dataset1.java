package com.company.talend.components.dataset;

import java.io.Serializable;
import java.util.List;

import com.company.talend.components.datastore.Datastore1;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Structure;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@DataSet("Dataset1")
@GridLayout({
    // the generated layout put one configuration entry per line,
    // customize it as much as needed
    @GridLayout.Row({ "datastore" }),
    @GridLayout.Row({ "schema" })
})
@Documentation("TODO fill the documentation for this configuration")
public class Dataset1 implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private Datastore1 datastore;

    @Option
    @Documentation("Schema widget")
    @Structure
    private List<String> schema;

}