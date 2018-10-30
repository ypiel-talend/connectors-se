package org.talend.components.azure.common;

import org.talend.components.azure.service.AzureComponentServices;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@GridLayout(value = { @GridLayout.Row({ "schemaColumnName", "entityPropertyName" }) }, names = GridLayout.FormType.ADVANCED)
// @OptionsOrder({ "schemaColumnName", "entityPropertyName" })
public class NameMapping {

    @Option
    @Documentation("Schema column to map")
    @Suggestable(AzureComponentServices.COLUMN_NAMES)
    private String schemaColumnName;

    @Option
    @Documentation("Mapped value")
    private String entityPropertyName;
}
