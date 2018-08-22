package org.talend.components.azure.common;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.OptionsOrder;
import org.talend.sdk.component.api.meta.Documentation;

@OptionsOrder({"schemaColumnName", "entityPropertyName"})
public class NameMapping {
    @Option
    @Documentation("")
    private String schemaColumnName;

    @Option
    @Documentation("")
    private String entityPropertyName;
}
