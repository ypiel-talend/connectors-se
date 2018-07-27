package org.talend.components.azure.table.input;

import java.io.Serializable;
import java.util.List;

import org.talend.components.azure.common.AzureConnection;
import org.talend.components.azure.common.AzureTableConnection;
import org.talend.components.azure.common.NameMapping;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Structure;
import org.talend.sdk.component.api.meta.Documentation;

@GridLayout(value = {
    @GridLayout.Row("azureConnection"),
        @GridLayout.Row("useFilterExpression"),
        @GridLayout.Row("filterExpressions"),
        @GridLayout.Row("dieOnError")
}, names = GridLayout.FormType.MAIN)

@GridLayout(value = {
        @GridLayout.Row("nameMappings"),
        @GridLayout.Row("schema")
}, names = GridLayout.FormType.ADVANCED)
@Documentation("TODO fill the documentation for this configuration")
@DataSet("Input")
public class InputTableMapperConfiguration implements Serializable {
    @Option
    @Documentation("bl")
    private AzureTableConnection azureConnection;

    @Option
    @Documentation("bol bl")
    private boolean useFilterExpression;

    @Option
    @Documentation("table")
    @ActiveIf(target = "useFilterExpression", value = "true")
    //TODO sort columns
    private List<FilterExpression> filterExpressions;

    @Option
    @Documentation("die")
    private boolean dieOnError = true;


    //TODO sort columns
    @Option
    @Documentation("ah")
    private List<NameMapping> nameMappings;

    @Option
    @Structure(discoverSchema = "guess", type = Structure.Type.IN)
    @Documentation("SOS")
    private List<String> schema;

    private enum Function {
        EQUAL,
        NOT_EQUAL,
        GREATER_THAN,
        GT_OR_EQ,
        LESS_THAN,
        LT_OR_EQ
    }


    private enum Predicate {
        AND,
        OR
    }


    private enum FieldType {
        STRING,
        NUMERIC,
        DATE,
        GUID,
        BOOLEAN
    }

    private class FilterExpression {
        @Option
        @Documentation("column name")
        //TODO take column list from schema
        private String column;

        @Option
        @Documentation("func")
        private Function function = Function.EQUAL;

        @Option
        @Documentation("value")
        private String value;

        @Option
        @Documentation("doc")
        private Predicate predicate = Predicate.AND;

        @Option
        @Documentation("fieldType")
        private FieldType fieldType = FieldType.STRING;

    }
}
