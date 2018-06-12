package org.talend.components.localio.fixed;

import static org.talend.sdk.component.api.component.Icon.IconType.FLOW_SOURCE_O;

import java.io.Serializable;

import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.ui.OptionsOrder;
import org.talend.sdk.component.api.configuration.ui.widget.Code;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@Version
@Icon(FLOW_SOURCE_O)
@OptionsOrder({ "schema", "nbRows", "values" })
public class FixedFlowInputConfiguration implements Serializable {

    @Option
    @Required
    @Code("json")
    @Documentation("The avro schema of the values.")
    private String schema;

    @Option
    @Documentation("The number of rows.")
    private int nbRows = 1;

    @Option
    @Code("json")
    @Documentation("Values to use.")
    private String values = "";
}
