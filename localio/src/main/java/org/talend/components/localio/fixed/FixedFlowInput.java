package org.talend.components.localio.fixed;

import static org.talend.sdk.component.api.component.Icon.IconType.FILE_O;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.io.Read;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.talend.components.localio.runtime.fixedflowinput.FixedFlowInputBoundedSource;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.meta.Documentation;

@Version
@Icon(FILE_O)
@PartitionMapper(name = "FixedFlowInput")
@Documentation("This component duplicates an input a configured number of times.")
public class FixedFlowInput extends PTransform<PBegin, PCollection<IndexedRecord>> {

    private final FixedFlowInputConfiguration configuration;

    public FixedFlowInput(@Option("configuration") final FixedFlowInputConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public PCollection<IndexedRecord> expand(final PBegin input) {
        return input.apply(
                Read.from(new FixedFlowInputBoundedSource().withSchema(new Schema.Parser().parse(configuration.getSchema()))
                        .withValues(configuration.getValues()).withNbRows(configuration.getNbRows())));
    }

}
