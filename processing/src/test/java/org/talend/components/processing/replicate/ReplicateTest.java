package org.talend.components.processing.replicate;

import org.junit.jupiter.api.Test;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.junit.ComponentsHandler;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;

import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.talend.sdk.component.runtime.manager.chain.Job.components;

@WithComponents("org.talend.components.processing")
class ReplicateTest {

    @Injected
    private ComponentsHandler handler;

    @Service
    private JsonBuilderFactory factory;

    @Test
    void replicate() {

        final JsonObject object1 = factory.createObjectBuilder().add("index", 1).build();
        final JsonObject object2 = factory.createObjectBuilder().add("index", 2).build();
        final JsonObject object3 = factory.createObjectBuilder().add("index", 3).build();
        handler.setInputData(asList(object1, object2, object3));
        components().component("input", "test://emitter").component("replicate", "Processing://Replicate")
                .component("output", "test://collector").connections().from("input").to("replicate").from("replicate")
                .to("output").build().run();

        final List<JsonObject> values = handler.getCollectedData(JsonObject.class);
        assertEquals(1, values.size());
        // assertEquals(2, values.iterator().next().getInt("index"));
    }
}
