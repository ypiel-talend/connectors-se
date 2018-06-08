package org.talend.components.localio.devnull;

import static java.util.Collections.emptyMap;

import javax.json.JsonBuilderFactory;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.transforms.Create;
import org.junit.jupiter.api.Test;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.junit.ComponentsHandler;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.beam.TalendIO;

@WithComponents("org.talend.components.localio")
class DevNullOutputTest {

    @Injected
    private ComponentsHandler handler;

    @Service
    private JsonBuilderFactory builder;

    @Test
    void ensureItRuns() {
        final Pipeline pipeline = Pipeline.create();
        final TalendIO.Write input = handler.asManager().findProcessor("LocalIO", "DevNullOutput", 1, emptyMap())
                .map(TalendIO::write).orElseThrow(() -> new IllegalArgumentException("No component for fixed flow input"));
        pipeline.apply(Create.of(builder.createObjectBuilder().build(), builder.createObjectBuilder().build())).apply(input);
        pipeline.run().waitUntilFinish();
    }
}
