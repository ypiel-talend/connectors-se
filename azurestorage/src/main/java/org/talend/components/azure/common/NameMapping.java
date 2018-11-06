// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.azure.common;

import org.talend.components.azure.service.AzureComponentServices;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@GridLayout(value = { @GridLayout.Row({ "schemaColumnName", "entityPropertyName" }) }, names = GridLayout.FormType.ADVANCED)
// TODO replace with optionsorder instead of gridlayout when it would be fixed
// @OptionsOrder({ "schemaColumnName", "entityPropertyName" })
public class NameMapping {

    @Option
    @Documentation("The column name of the component schema between double quotation marks")
    @Suggestable(AzureComponentServices.COLUMN_NAMES)
    private String schemaColumnName;

    @Option
    @Documentation("The property name of the Azure table entity between double quotation marks")
    private String entityPropertyName;
}
