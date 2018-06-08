package org.talend.components.localio.devnull;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@DataSet("DevNull")
public class DevNullDataSet {

    @Option
    @Documentation("datastore configuration - not used for now.")
    private DevNullDataStore datastore;
}
