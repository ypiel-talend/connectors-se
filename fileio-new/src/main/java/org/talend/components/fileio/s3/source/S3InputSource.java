package org.talend.components.fileio.s3.source;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.talend.components.fileio.s3.configuration.S3DataSet;
import org.talend.components.fileio.s3.service.S3Service;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

@Documentation("TODO fill the documentation for this source")
public class S3InputSource implements Serializable {

    private final S3DataSet configuration;

    private final S3Service service;

    private final RecordBuilderFactory builderFactory;

    public S3InputSource(@Option("configuration") final S3DataSet configuration, final S3Service service,
            final RecordBuilderFactory builderFactory) {
        this.configuration = configuration;
        this.service = service;
        this.builderFactory = builderFactory;
    }

    @PostConstruct
    public void init() {
    }

    @Producer
    public Record next() throws IOException {
        return null;
    }

    @PreDestroy
    public void release() throws IOException {
    }
}