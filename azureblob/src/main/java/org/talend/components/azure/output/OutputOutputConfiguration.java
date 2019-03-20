package org.talend.components.azure.output;

import java.io.Serializable;

import org.talend.components.azure.common.FileFormat;
import org.talend.components.azure.dataset.AzureBlobDataset;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@GridLayout({
        // the generated layout put one configuration entry per line,
        // customize it as much as needed
        @GridLayout.Row({ "dataset" }) })
@Documentation("TODO fill the documentation for this configuration")
@Data
public class OutputOutputConfiguration implements Serializable {

    @Option
    @Documentation("Azure Blob connection")
    private AzureBlobDataset dataset;
}