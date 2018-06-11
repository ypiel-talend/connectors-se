package org.talend.components.bigquery;

import static org.talend.sdk.component.api.component.Icon.IconType.BIGQUERY;

import java.io.Serializable;

import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Code;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@Icon(BIGQUERY)
@DataSet("BigQueryDataSet")
@Documentation("Dataset of a BigQuery component.")
@GridLayout({ @GridLayout.Row("dataStore"), @GridLayout.Row("bqDataset"), @GridLayout.Row("sourceType"),
        @GridLayout.Row("tableName"), @GridLayout.Row({ "query", "useLegacySql" }) })
public class BigQueryDataSet implements Serializable {

    @Option
    @Documentation("The BigQuery datastore")
    private BigQueryDataStore dataStore;

    @Option
    @Required
    @Suggestable(value = "BigQueryDataSet", parameters = {})
    @Documentation("The BigQuery dataset")
    private String bqDataset;

    @Option
    @Required
    @Documentation("The BigQuery source type")
    private SourceType sourceType = SourceType.QUERY;

    @Option
    @Suggestable(value = "BigQueryTables", parameters = {})
    @ActiveIf(target = "../sourceType", value = "TABLE_NAME")
    @Documentation("The BigQuery table name if `sourceType` == TABLE_NAME")
    private String tableName;

    @Option
    @Code("sql")
    @ActiveIf(target = "../sourceType", value = "QUERY")
    @Documentation("The BigQuery query if `sourceType` == QUERY")
    private String query;

    @Option
    @ActiveIf(target = "../sourceType", value = "QUERY")
    @Documentation("Should the query use legacy SQL")
    private boolean useLegacySql;

    public enum SourceType {
        TABLE_NAME,
        QUERY
    }
}
