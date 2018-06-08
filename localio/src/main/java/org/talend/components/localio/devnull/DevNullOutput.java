package org.talend.components.localio.devnull;

import static org.talend.sdk.component.api.component.Icon.IconType.TRASH;

import java.io.Serializable;

import javax.json.JsonObject;

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
public class DevNullOutput implements Serializable {

    public DevNullOutput(@Option("configuration") final DevNullDataSet configuration) {
        // no-op
    }

    @ElementListener
    public void onElement(final JsonObject ignored) {
        // no-op
    }
}
