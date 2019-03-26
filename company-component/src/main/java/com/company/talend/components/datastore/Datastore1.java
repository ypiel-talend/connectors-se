package com.company.talend.components.datastore;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@DataStore("Datastore1")
@GridLayout({
    // the generated layout put one configuration entry per line,
    // customize it as much as needed
    @GridLayout.Row({ "configuration1" })
})
@Documentation("TODO fill the documentation for this configuration")
public class Datastore1 implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String configuration1;

    public String getConfiguration1() {
        return configuration1;
    }

    public Datastore1 setConfiguration1(String configuration1) {
        this.configuration1 = configuration1;
        return this;
    }
}