package org.talend.components.jms;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@GridLayout({ @GridLayout.Row("datastore"), @GridLayout.Row("msgType"), @GridLayout.Row("processingMode"),
        @GridLayout.Row("queueTopicName"), })
@Documentation("")
public class JmsCommon implements Serializable {

    @Option
    @Required
    @Documentation("")
    private JmsDatastore datastore;

    @Option
    @Required
    @Documentation("")
    private JmsMessageType msgType;

    @Option
    @Documentation("")
    private JmsProcessingMode processingMode = JmsProcessingMode.CONTENT;

    @Option
    @Documentation("")
    private String queueTopicName;

    public enum JmsProcessingMode {
        RAW,
        CONTENT
    }

}
