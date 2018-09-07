package org.talend.components.azure.table.output;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.json.JsonObject;

import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.AfterGroup;
import org.talend.sdk.component.api.processor.BeforeGroup;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Output;
import org.talend.sdk.component.api.processor.OutputEmitter;
import org.talend.sdk.component.api.processor.Processor;

import org.talend.components.azure.service.AzureConnectionService;

@Version(1) // default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Icon(value = Icon.IconType.CUSTOM, custom = "outputTable") // you can use a custom one using @Icon(value=CUSTOM, custom="filename") and adding icons/filename_icon32.png in resources
@Processor(name = "OutputTable")
@Documentation("Azure Output Table Component")
public class OutputTableProcessor implements Serializable {
    private final OutputTableProcessorConfiguration configuration;
    private final AzureConnectionService service;

    public OutputTableProcessor(@Option("configuration") final OutputTableProcessorConfiguration configuration,
                          final AzureConnectionService service) {
        this.configuration = configuration;
        this.service = service;
    }

    @PostConstruct
    public void init() {
        // this method will be executed once for the whole component execution,
        // this is where you can establish a connection for instance
        // Note: if you don't need it you can delete it
    }

    @ElementListener
    public void onNext(
            @Input final JsonObject incomingData) {
        System.out.println(incomingData.toString());
    }

    @PreDestroy
    public void release() {
        //NOOP
    }
}