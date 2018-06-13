package org.talend.components.jms;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@GridLayout({ @GridLayout.Row("common"), @GridLayout.Row("isStreaming"), @GridLayout.Row("timeout"), @GridLayout.Row("maxMsg"),
        @GridLayout.Row("msgSelector"), })
@DataSet("JmsInputDataset")
public class JmsInputDataset implements Serializable {

    @Option
    @Required
    @Documentation("")
    private JmsCommon common;

    // * Hidden property used to specify that this component generates unbounded input.
    @Option
    @ActiveIf(target = ".", value = "-98989898989")
    @Documentation("")
    private Boolean isStreaming = true;

    @Option
    @Documentation("")
    private Integer timeout = -1;

    @Option
    @Documentation("")
    private Integer maxMsg = -1;

    @Option
    @Documentation("")
    private String msgSelector = "";

}
