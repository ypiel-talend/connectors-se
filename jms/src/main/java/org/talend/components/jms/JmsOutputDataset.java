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
@DataSet("JmsOutputDataset")
@GridLayout(names = GridLayout.FormType.MAIN, value = { @GridLayout.Row("common"), })
@GridLayout(names = GridLayout.FormType.ADVANCED, value = { @GridLayout.Row("deliveryMode"), @GridLayout.Row("poolMaxTotal"),
        @GridLayout.Row("poolMaxWait"), @GridLayout.Row("poolMinIdle"), @GridLayout.Row("poolMaxIdle"),
        @GridLayout.Row("poolUseEviction"), @GridLayout.Row("poolTimeBetweenEviction"),
        @GridLayout.Row("poolEvictionMinIdleTime"), @GridLayout.Row("poolEvictionSoftMinIdleTime"), })
public class JmsOutputDataset implements Serializable {

    public enum JmsAdvancedDeliveryMode {
        NON_PERSISTENT,
        PERSISTENT
    }

    @Option
    @Required
    @Documentation("")
    private JmsAdvancedDeliveryMode deliveryMode = JmsAdvancedDeliveryMode.NON_PERSISTENT;

    @Option
    @Documentation("")
    private JmsCommon common;

    @Option
    @Documentation("")
    private String poolMaxTotal = "8";

    @Option
    @Documentation("")
    private String poolMaxWait = "-1";

    @Option
    @Documentation("")
    private String poolMinIdle = "0";

    @Option
    @Documentation("")
    private String poolMaxIdle = "8";

    @Option
    @Documentation("")
    private Boolean poolUseEviction = false;

    @Option
    @ActiveIf(target = "poolUseEviction", value = "true")
    @Documentation("")
    private String poolTimeBetweenEviction = "-1";

    @Option
    @ActiveIf(target = "poolUseEviction", value = "true")
    @Documentation("")
    private String poolEvictionMinIdleTime = "1800000";

    @Option
    @ActiveIf(target = "poolUseEviction", value = "true")
    @Documentation("")
    private String poolEvictionSoftMinIdleTime = "0";

}
