package org.talend.components.azure.output;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.talend.components.azure.common.runtime.output.BlobFileWriter;
import org.talend.components.azure.service.MessageService;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.AfterGroup;
import org.talend.sdk.component.api.processor.BeforeGroup;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;

import org.talend.components.azure.service.AzureBlobComponentServices;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

@Version(1) // default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Icon(value = Icon.IconType.CUSTOM, custom = "AzureOutput")
@Processor(name = "Output")
@Documentation("TODO fill the documentation for this processor")
public class BlobOutput implements Serializable {

    private final BlobOutputConfiguration configuration;

    private final AzureBlobComponentServices service;

    private BlobFileWriter fileWriter;

    public BlobOutput(@Option("configuration") final BlobOutputConfiguration configuration,
            final AzureBlobComponentServices service, final MessageService i18n) {
        this.configuration = configuration;
        this.service = service;
    }

    @PostConstruct
    public void init() {
        try {
            this.fileWriter = BlobFileWriter.BlobFileWriterFactory.getWriter(configuration, service.getConnectionService());
            fileWriter.generateFile();
        } catch (Exception e) {
            throw new RuntimeException(e); // TODO custom exception
        }
    }

    @BeforeGroup
    public void beforeGroup() {
        fileWriter.newBatch();
    }

    @ElementListener
    public void onNext(@Input final Record defaultInput) {
        fileWriter.writeRecord(defaultInput);
    }

    @AfterGroup
    public void afterGroup() {
        try {
            fileWriter.flush();
        } catch (Exception e) {
            throw new RuntimeException(e); // TODO custom exception
        }
    }

    @PreDestroy
    public void release() {
        afterGroup();
    }
}