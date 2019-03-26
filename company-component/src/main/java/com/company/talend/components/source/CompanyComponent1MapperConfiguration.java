package com.company.talend.components.source;

import java.io.Serializable;

import com.company.talend.components.dataset.Dataset1;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@GridLayout({
    // the generated layout put one configuration entry per line,
    // customize it as much as needed
    @GridLayout.Row({ "dataset" })
})
@Documentation("TODO fill the documentation for this configuration")
public class CompanyComponent1MapperConfiguration implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private Dataset1 dataset;

    public Dataset1 getDataset() {
        return dataset;
    }

    public CompanyComponent1MapperConfiguration setDataset(Dataset1 dataset) {
        this.dataset = dataset;
        return this;
    }
}