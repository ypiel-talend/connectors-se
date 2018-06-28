package org.talend.components.localio.devnull;

import static org.talend.sdk.component.api.component.Icon.IconType.TRASH;

import java.io.Serializable;

import javax.json.JsonObject;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.talend.components.localio.fixed.FixedDataSetConfiguration;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Processor;

@Version
@Icon(TRASH)
@Processor(name = "DevNullOutput")
@Documentation("This component ignores any input.")
public class DevNullOutput extends PTransform<PCollection<IndexedRecord>, PCollection<IndexedRecord>> {

    public DevNullOutput(@Option("configuration") final FixedDataSetConfiguration configuration) {
        // no-op
    }

    @ElementListener
    public void onElement(final JsonObject ignored) {
        // no-op
    }

    @Override
    public PCollection<IndexedRecord> expand(PCollection<IndexedRecord> input) {
        return input;
    }
}
