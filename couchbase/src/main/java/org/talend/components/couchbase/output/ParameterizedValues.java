package org.talend.components.couchbase.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.talend.components.couchbase.service.UICouchbaseDBService;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.util.List;

@GridLayout({ @GridLayout.Row({ "positionInQuery", "column" }) })
@Documentation("This is the mapping for input schema.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterizedValues {

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String positionInQuery;

    @Option
    @Suggestable(value = UICouchbaseDBService.GET_SCHEMA_FIELDS, parameters = { "../../dataset" })
    @Documentation("TODO fill the documentation for this parameter")
    private String column;

}
