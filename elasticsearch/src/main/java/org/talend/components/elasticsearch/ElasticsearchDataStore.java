package org.talend.components.elasticsearch;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.OptionsOrder;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@DataStore("ElasticsearchDataStore")
@Documentation("Elasticsearch connection.")
@OptionsOrder("nodes")
public class ElasticsearchDataStore implements Serializable {

    @Option
    @Required
    @Documentation("A comma separated list of bootstrap nodes.")
    private String nodes; // todo: List<String>? ui is different then
}
