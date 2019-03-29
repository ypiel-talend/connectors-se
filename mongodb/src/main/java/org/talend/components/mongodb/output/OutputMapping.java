package org.talend.components.mongodb.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talend.components.mongodb.service.UIMongoDBService;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@GridLayout({ @GridLayout.Row({ "column", "parentNodePath", "removeNullField" }) })
@Documentation("This is the mapping for input schema.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutputMapping {

    @Option
    @Suggestable(value = UIMongoDBService.GET_SCHEMA_FIELDS, parameters = { "../../dataset" })
    @Documentation("TODO fill the documentation for this parameter")
    private String column;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String parentNodePath;

    @Option
    @Documentation("Remove null valued fields")
    private boolean removeNullField;

}
