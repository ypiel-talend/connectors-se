package org.talend.components.localio.devnull;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@DataSet("DevNull")
public class DevNullDataSet implements Serializable {

    @Option
    @Documentation("datastore configuration - not used for now.")
    private DevNullDataStore datastore;
}
