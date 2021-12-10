package org.talend.components.adlsgen2.migration;

import java.util.HashMap;
import java.util.Map;
import org.talend.sdk.component.api.component.MigrationHandler;

/**
 * This class is a workaround to migrate dataset parameters on pipelines since AdlsDataSetMigrationHandler
 * is not executed for runtime
 * (https://jira.talendforge.org/browse/TPRUN-486)
 */

public class AdlsRuntimeDatasetMigration implements MigrationHandler {

    @Override public Map<String, String> migrate(int incomingVersion, Map<String, String> incomingData) {
        Map<String, String> migratedConfiguration = new HashMap<>(incomingData);
        if (incomingVersion < 2) {
            AdlsDataSetMigrationHandler.migrateDataset(migratedConfiguration, "configuration.dataSet.");
        }
        if (incomingVersion < 3) {
            AdlsDataSetMigrationHandler.migrateCSVFieldDelimiterTabulation(migratedConfiguration,
                    "configuration.dataSet.csvConfiguration.csvFormatOptions.fieldDelimiter");
        }
        return migratedConfiguration;
    }

}
