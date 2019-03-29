package org.talend.components.mongodb.output;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import org.talend.components.mongodb.dataset.MongoDBDataset;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayouts;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@GridLayouts({ @GridLayout(value = { @GridLayout.Row({ "dataset" }), @GridLayout.Row({ "setWriteConcern" }),
        @GridLayout.Row({ "writeConcern" }), @GridLayout.Row({ "dropCollectionIfExists" }), @GridLayout.Row({ "actionOnData" }),
        @GridLayout.Row({ "outputMappingList" }) }, names = GridLayout.FormType.MAIN),
        @GridLayout(value = { @GridLayout.Row({ "bulkWrite" }),
                @GridLayout.Row({ "bulkWriteType" }) }, names = GridLayout.FormType.ADVANCED) })
@Documentation("TODO fill the documentation for this configuration")
public class MongoDBOutputConfiguration implements Serializable {

    public enum WriteConcern {
        ACKNOWLEDGED,
        UNACKNOWLEDGED,
        JOURNALED,
        REPLICA_ACKNOWLEDGED;
    }

    public enum BulkWriteType {
        ORDERED,
        UNORDERED;
    }

    public enum ActionOnData {
        INSERT,
        UPDATE,
        SET,
        UPSERT,
        UPSERT_WITH_SET,
        DELETE;
    }

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private MongoDBDataset dataset;

    @Option
    @Documentation("Set write concern")
    private boolean setWriteConcern;

    @Option
    @Documentation("Write concern")
    @ActiveIf(target = "setWriteConcern", value = "true")
    @DefaultValue("ACKNOWLEDGED")
    private WriteConcern writeConcern;

    @Option
    @Documentation("Set bulk write")
    private boolean bulkWrite;

    @Option
    @Documentation("Bulk write type")
    @ActiveIf(target = "bulkWrite", value = "true")
    @DefaultValue("ORDERED")
    private BulkWriteType bulkWriteType;

    @Option
    @Documentation("Drop collection if exists")
    private boolean dropCollectionIfExists;

    @Option
    @Documentation("Action on data")
    @DefaultValue("INSERT")
    private ActionOnData actionOnData;

    @Option
    @Documentation("Mapping for output data")
    private List<OutputMapping> outputMappingList;
}