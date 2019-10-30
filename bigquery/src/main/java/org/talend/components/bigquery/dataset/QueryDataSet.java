/*
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.talend.components.bigquery.dataset;

import lombok.Data;
import org.talend.components.bigquery.datastore.BigQueryConnection;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Code;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@Data
@DataSet("BigQueryDataSetQueryType")
@Documentation("Dataset of a BigQuery component with query type.")
@GridLayout({ @GridLayout.Row("connection"), @GridLayout.Row({ "query", "useLegacySql" }),
        @GridLayout.Row({ "useSpecificLocation", "location" }) })
public class QueryDataSet implements Serializable {

    @Option
    @Documentation("The BigQuery connection")
    private BigQueryConnection connection;

    @Option
    @Code("sql")
    @Documentation("The BigQuery query")
    private String query;

    @Option
    @Documentation("Should the query use legacy SQL")
    private boolean useLegacySql;

    @Option
    @Documentation("Location must be specified for queries not executed in US or EU")
    @DefaultValue("false")
    private boolean useSpecificLocation;

    @Option
    @Documentation("Regional locations")
    @DefaultValue("US")
    @ActiveIf(target = "useSpecificLocation", value = "true")
    private Location location;

    /**
     * BigQuery geographic location where the query <a
     * href="https://cloud.google.com/bigquery/docs/reference/rest/v2/jobs">job</a> will be
     * executed. If not specified, Beam tries to determine the location by examining the tables
     * referenced by the query. Location must be specified for queries not executed in US or EU. See
     * <a href="https://cloud.google.com/bigquery/docs/locations">Dataset locations</a>.
     */
    public enum Location {
        US("US"),
        EU("EU"),
        NORTH_AMERICA_NORTHEAST("northamerica-northeast1"),
        US_EAST("us-east4"),
        EU_NORTH("europe-north1"),
        EU_WEST("europe-west2"),
        ASIA_SOUTH("asia-south1"),
        ASIA_EAST("asia-east1"),
        ASIA_NORTHEAST("asia-northeast1"),
        ASIA_SOUTHEAST("asia-southeast1"),
        AUSTRALIA_SOUTHEAST("australia-southeast1");

        private final String regionName;

        private Location(String regionName) {
            this.regionName = regionName;
        }

        public String getRegionName() {
            return regionName;
        }
    }
}