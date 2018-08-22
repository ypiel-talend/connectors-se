package org.talend.components.azure.table.output;

import java.io.Serializable;
import java.util.List;

import org.talend.components.azure.common.AzureConnection;
import org.talend.components.azure.common.AzureTableConnection;
import org.talend.components.azure.common.NameMapping;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@GridLayout(value = {
        @GridLayout.Row("azureConnection"),
        @GridLayout.Row({"partitionName", "rowKey"}),
        @GridLayout.Row({"actionOnData", "actionOnTable"}),
        @GridLayout.Row("processInBatch"),
        @GridLayout.Row("dieOnError")

}, names = GridLayout.FormType.MAIN)

@GridLayout(value = {
        @GridLayout.Row("nameMappings")

}, names = GridLayout.FormType.ADVANCED)
@Documentation("TODO fill the documentation for this configuration")
@DataSet("Output")
public class OutputTableProcessorConfiguration implements Serializable {

    @Option
    @Documentation("")
    private AzureTableConnection azureConnection;

    //TODO make selection list from table columns
    @Option
    @Documentation("")
    private String partitionName;

    //TODO make selection list from table columns
    @Option
    @Documentation("")
    private String rowKey;

    @Option
    @Documentation("")
    private ActionOnData actionOnData = ActionOnData.INSERT;

    @Option
    @Documentation("")
    private ActionOnTable actionOnTable = ActionOnTable.DEFAULT;

    @Option
    @Documentation("")
    private boolean processInBatch;

    @Option
    @Documentation("")
    private boolean dieOnError = true;

    @Option
    @Documentation("")
    private List<NameMapping> nameMappings;

    private enum ActionOnData {
        INSERT,
        INSERT_OR_REPLACE,
        INSERT_OR_MERGE,
        MERGE,
        REPLACE,
        DELETE
    }

    private enum ActionOnTable {
        DEFAULT,
        DROP_AND_CREATE,
        CREATE,
        CREATE_IF_NOT_EXIST,
        DROP_IF_EXIST_CREATE

    }
}