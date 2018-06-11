package org.talend.components.bigquery;

import static org.talend.sdk.component.api.component.Icon.IconType.BIGQUERY;

import java.io.Serializable;

import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.OptionsOrder;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@Icon(BIGQUERY)
@DataStore("BigQueryDataStore")
@Documentation("Datastore of a BigQuery component.")
@OptionsOrder({ "projectName", "serviceAccountFile", "tempGsFolder" })
public class BigQueryDataStore implements Serializable {

    @Option
    @Required
    @Documentation("The BigQuery project")
    private String projectName;

    /**
     * service account need to set on pipeline options, so it's kind of global setting
     * refer to: https://developers.google.com/identity/protocols/OAuth2ServiceAccount
     */
    @Option
    @Required
    @Documentation("The BigQuery account file")
    private String serviceAccountFile;

    /**
     * temp gs folder need to set on pipeline options, so it's kind of global setting
     */
    @Option
    @Required
    @Documentation("The BigQuery account file")
    private String tempGsFolder;
}
