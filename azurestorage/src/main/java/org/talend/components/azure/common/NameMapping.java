package org.talend.components.azure.common;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;

public class NameMapping {
    @Option
    @Documentation("b")
    private String schemaColumnName;

    @Option
    @Documentation("")
    private String entityPropertyName;
}
