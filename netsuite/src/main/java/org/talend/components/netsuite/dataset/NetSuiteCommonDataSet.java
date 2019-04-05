package org.talend.components.netsuite.dataset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talend.components.netsuite.datastore.NetsuiteDataStore;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayouts;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@AllArgsConstructor
@NoArgsConstructor
@GridLayouts({ @GridLayout({ @GridLayout.Row({ "dataStore" }), @GridLayout.Row({ "recordType" }) }),
        @GridLayout(names = { GridLayout.FormType.ADVANCED }, value = { @GridLayout.Row({ "dataStore" }) }) })
public class NetSuiteCommonDataSet {

    @Option
    @Documentation("")
    private NetsuiteDataStore dataStore;

    @Option
    @Suggestable(value = "loadRecordTypes", parameters = { "dataStore" })
    @Documentation("TODO fill the documentation for this parameter")
    private String recordType;
}
