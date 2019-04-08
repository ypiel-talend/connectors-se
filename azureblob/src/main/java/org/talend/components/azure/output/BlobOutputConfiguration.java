package org.talend.components.azure.output;

import java.io.Serializable;

import org.talend.components.azure.common.FileFormat;
import org.talend.components.azure.dataset.AzureBlobDataset;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@GridLayout({ @GridLayout.Row({ "dataset" }) })

@GridLayout(names = GridLayout.FormType.ADVANCED, value = { @GridLayout.Row("blobNameTemplate") })
@Documentation("TODO fill the documentation for this configuration")
@Data
public class BlobOutputConfiguration implements Serializable {

    @Option
    @Documentation("Azure Blob connection")
    private AzureBlobDataset dataset;

    @Option
    @Documentation("Generated blob name template")
    private String blobNameTemplate = "data";
}