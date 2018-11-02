package org.talend.components.azure.table.output;

import java.io.Serializable;
import java.util.List;

import org.talend.components.azure.common.AzureTableConnection;
import org.talend.components.azure.common.NameMapping;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Structure;
import org.talend.sdk.component.api.meta.Documentation;

import static org.talend.components.azure.service.AzureComponentServices.COLUMN_NAMES;

import lombok.Data;

@GridLayout(value = { @GridLayout.Row("azureConnection"), @GridLayout.Row({ "partitionName", "rowKey" }),
        @GridLayout.Row({ "actionOnData", "actionOnTable" }), @GridLayout.Row("processInBatch"), @GridLayout.Row("dieOnError"),
        @GridLayout.Row("schema")

}, names = GridLayout.FormType.MAIN)

@GridLayout(value = { @GridLayout.Row("nameMappings") }, names = GridLayout.FormType.ADVANCED)
@Documentation("These properties are used to configure AzureStorageTableOutput")
@Data
public class OutputProperties implements Serializable {

    @Option
    @Documentation("Azure Table Connection")
    private AzureTableConnection azureConnection;

    // FIXME not working in studio
    @Option
    @Suggestable(value = COLUMN_NAMES, parameters = "schema")
    @Documentation("Schema column that holds the partition key value")
    private String partitionName;

    @Option
    @Documentation("Schema column that holds the row key value ")
    @Suggestable(value = COLUMN_NAMES, parameters = "schema")
    private String rowKey;

    @Option
    @Documentation("An action to be performed on data of the table defined")
    private ActionOnData actionOnData = ActionOnData.INSERT;

    @Option
    @Documentation("An operation to be performed on the table defined")
    private ActionOnTable actionOnTable = ActionOnTable.DEFAULT;

    @Option
    @Documentation("Process the input entities in batch.\n"
            + "Note that the entities to be processed in batch should belong to the same partition group, "
            + "which means, they should have the same partition key value.")
    private boolean processInBatch;

    @Option
    @Documentation("Stop the execution of the Job when an error occurs")
    private boolean dieOnError = true;

    @Option
    @Documentation("Mapping the column name of the component schema with the property name of the Azure table entity if they are different.")
    private List<NameMapping> nameMappings;

    @Option
    @Structure
    @Documentation("A schema is a row description. It defines the number of fields (columns) to be processed and passed on to the next component.")
    private List<String> schema;

    enum ActionOnData {
        INSERT,
        INSERT_OR_REPLACE,
        INSERT_OR_MERGE,
        MERGE,
        REPLACE,
        DELETE
    }

    enum ActionOnTable {
        DEFAULT,
        DROP_AND_CREATE,
        CREATE,
        CREATE_IF_NOT_EXIST,
        DROP_IF_EXIST_CREATE

    }
}