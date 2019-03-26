package org.talend.components.mongodb.source;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import org.talend.components.mongodb.dataset.MongoDBDataset;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayouts;
import org.talend.sdk.component.api.configuration.ui.widget.TextArea;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@GridLayouts({
        @GridLayout(value = { @GridLayout.Row({ "dataset" }), @GridLayout.Row({ "setReadPreference" }),
                @GridLayout.Row({ "readPreference" }), @GridLayout.Row({ "queryType" }), @GridLayout.Row({ "aggregationStages" }),
                @GridLayout.Row({ "query" }), @GridLayout.Row({ "limit" }), @GridLayout.Row({ "mapping" }),
                @GridLayout.Row({ "sort" }) }, names = GridLayout.FormType.MAIN),
        @GridLayout(value = { @GridLayout.Row({ "noQueryTimeout" }),
                @GridLayout.Row({ "externalSort" }) }, names = GridLayout.FormType.ADVANCED), })
@Documentation("TODO fill the documentation for this configuration")
public class MongoDBInputMapperConfiguration implements Serializable {

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private MongoDBDataset dataset;

    @Option
    @Documentation("setReadPreference")
    private boolean setReadPreference;

    @Option
    @ActiveIf(target = "setReadPreference", value = "true")
    @Documentation("readPreference")
    private ReadPreference readPreference;

    public enum ReadPreference {
        PRIMARY,
        PRIMARY_PREFERRED,
        SECONDARY,
        SECONDARY_PREFERRED,
        NEAREST;
    }

    public enum QueryType {
        FIND_QUERY,
        AGGREGATION_PIPELINE_QUERY;
    }

    @Option
    @Documentation("")
    @DefaultValue("FIND_QUERY")
    private QueryType queryType;

    @Option
    @TextArea
    @Documentation("query")
    @ActiveIf(target = "queryType", value = "FIND_QUERY")
    private String query = "{}";

    @Option
    @Documentation("limit")
    @ActiveIf(target = "queryType", value = "FIND_QUERY")
    private int limit;

    @Option
    @Documentation("mapping")
    private List<InputMapping> mapping;

    @Option
    @Documentation("sort")
    @ActiveIf(target = "queryType", value = "FIND_QUERY")
    private List<Sort> sort;

    @Option
    @Documentation("No timeout for queries")
    @ActiveIf(target = "queryType", value = "FIND_QUERY")
    private boolean noQueryTimeout;

    @Option
    @Documentation("Aggregation stages")
    @ActiveIf(target = "queryType", value = "AGGREGATION_PIPELINE_QUERY")
    private List<AggregationStage> aggregationStages;

    @Option
    @Documentation("Enable external sort")
    @ActiveIf(target = "queryType", value = "AGGREGATION_PIPELINE_QUERY")
    private boolean externalSort;

}