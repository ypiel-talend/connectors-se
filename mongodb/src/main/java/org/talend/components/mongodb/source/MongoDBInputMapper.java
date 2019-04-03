package org.talend.components.mongodb.source;

import static java.util.Collections.singletonList;

import java.io.Serializable;
import java.util.List;

import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Assessor;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.input.PartitionSize;
import org.talend.sdk.component.api.input.Split;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import org.talend.components.mongodb.service.MongoDBService;

//
// this class role is to enable the work to be distributed in environments supporting it.
//
@Version(1) // default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Icon(Icon.IconType.STAR) // you can use a custom one using @Icon(value=CUSTOM, custom="filename") and adding
                          // icons/filename_icon32.png in resources
@PartitionMapper(name = "MongoDBInput")
@Documentation("Input mapper for MongoDB")
public class MongoDBInputMapper implements Serializable {

    private final MongoDBInputMapperConfiguration configuration;

    private final MongoDBService service;

    private final RecordBuilderFactory recordBuilderFactory;

    public MongoDBInputMapper(@Option("configuration") final MongoDBInputMapperConfiguration configuration,
            final MongoDBService service, final RecordBuilderFactory recordBuilderFactory) {
        this.configuration = configuration;
        this.service = service;
        this.recordBuilderFactory = recordBuilderFactory;
    }

    @Assessor
    public long estimateSize() {
        return 1L;
    }

    @Split
    public List<MongoDBInputMapper> split(@PartitionSize final long bundles) {
        return singletonList(this);
    }

    @Emitter
    public MongoDBInputSource createWorker() {
        return new MongoDBInputSource(configuration, service, recordBuilderFactory);
    }
}