package org.talend.components.bigquery;

import static org.talend.sdk.component.api.component.Icon.IconType.BIGQUERY;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.talend.components.bigquery.input.BigQueryInputProperties;
import org.talend.components.bigquery.runtime.BigQueryInputRuntime;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.meta.Documentation;

@Version
@Icon(BIGQUERY)
@PartitionMapper(name = "BigQueryInput")
@Documentation("This component reads a dataset from BigQuery.")
public class BigQueryInput extends PTransform<PBegin, PCollection<IndexedRecord>> {

    private final BigQueryDataSet configuration;

    private final BigQueryConfigurationService service;

    public BigQueryInput(@Option("configuration") final BigQueryDataSet configuration,
            final BigQueryConfigurationService service) {
        this.configuration = configuration;
        this.service = service;
    }

    @Override
    public PCollection<IndexedRecord> expand(final PBegin input) {
        final BigQueryInputRuntime runtime = new BigQueryInputRuntime();
        final BigQueryInputProperties properties = new BigQueryInputProperties("name");
        properties.setDatasetProperties(service.toDataSet(configuration));
        runtime.initialize(service.newRuntimeContainer(), properties);
        return runtime.expand(input);
    }
}
