package org.talend.components.mongodb.source;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talend.components.mongodb.service.UIMongoDBService;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@GridLayout({ @GridLayout.Row({ "column", "order" }) })
@Documentation("This is the mapping for input schema.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sort implements Serializable {

    @Option
    @Suggestable(value = UIMongoDBService.GET_SCHEMA_FIELDS, parameters = { "../../dataset" })
    @Documentation("TODO fill the documentation for this parameter")
    private String column;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private SortingOrder order;

    public enum SortingOrder {
        asc, desc;
    }
}
