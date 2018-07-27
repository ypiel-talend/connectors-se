package org.talend.components.azure.common;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.OptionsOrder;
import org.talend.sdk.component.api.meta.Documentation;

@OptionsOrder({"prefix", "includeSubDirs"})
public class BlobTableGetting {
    @Option
    @Documentation("Blah-")
    private String prefix;

    //TODO make boolean
    @Option
    @Documentation("Blah-")
    private String includeSubDirs;
}
