package org.talend.components.azure.source;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.talend.components.azure.common.runtime.BlobFileReader;
import org.talend.components.azure.service.AzureBlobComponentServices;
import org.talend.components.azure.service.MessageService;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

@Documentation("TODO fill the documentation for this source")
public class BlobSource implements Serializable {

    private final InputMapperConfiguration configuration;

    private final AzureBlobComponentServices service;

    private final RecordBuilderFactory builderFactory;

    private final MessageService i18n;

    private BlobFileReader reader;

    public BlobSource(@Option("configuration") final InputMapperConfiguration configuration,
            final AzureBlobComponentServices service, final RecordBuilderFactory builderFactory, final MessageService i18n) {
        this.configuration = configuration;
        this.service = service;
        this.builderFactory = builderFactory;
        this.i18n = i18n;
    }

    @PostConstruct
    public void init() throws Exception {
        reader = BlobFileReader.BlobFileReaderFactory.getReader(configuration.getDataset(), builderFactory,
                service.getConnectionService());
    }

    @Producer
    public Record next() {
        return reader.readRecord();
    }

    @PreDestroy
    public void release() {
        // NOOP
    }
}