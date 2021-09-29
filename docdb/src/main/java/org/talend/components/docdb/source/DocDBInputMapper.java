/*
 * Copyright (C) 2006-2021 Talend Inc. - www.talend.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.talend.components.docdb.source;

import static java.util.Collections.singletonList;
import static org.talend.sdk.component.api.component.Icon.IconType.CUSTOM;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.talend.components.docdb.service.DocDBConnectionService;
import org.talend.components.docdb.service.I18nMessage;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Assessor;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.input.PartitionSize;
import org.talend.sdk.component.api.input.Split;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.service.connection.Connection;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import org.talend.components.docdb.service.DocDBService;

@Version(1)
@Slf4j
@Icon(value = CUSTOM, custom = "CompanyInput") // icon is located at src/main/resources/icons/CompanyInput.svg
@PartitionMapper(name = "Input")
@Documentation("DocDB input with query or collection")
public class DocDBInputMapper implements Serializable {

    private final DocDBInputMapperConfiguration configuration;

    private final DocDBService service;

    private final RecordBuilderFactory recordBuilderFactory;

    private final I18nMessage i18nMessage;

    private final DocDBConnectionService connectionService;

    @Connection
    private transient DocDBConnectionService conn;

    @Setter
    private String query4Split;

    public DocDBInputMapper(@Option("configuration") final DocDBInputMapperConfiguration configuration,
            final DocDBService service,
            final RecordBuilderFactory recordBuilderFactory,
            final DocDBConnectionService connectionService,
            final I18nMessage i18nMessage) {
        this.configuration = configuration;
        this.service = service;
        this.recordBuilderFactory = recordBuilderFactory;
        this.connectionService = connectionService;
        this.i18nMessage = i18nMessage;
    }

    @Assessor
    public long estimateSize() {
        if (!SplitUtil.isSplit(configuration.getSampleLimit())) {
            return 1l;
        }

        return SplitUtil.getEstimatedSizeBytes(configuration, service, conn);
    }

    @Split
    public List<DocDBInputMapper> split(@PartitionSize final long bundles) {
        if (!org.talend.components.mongodb.source.SplitUtil.isSplit(configuration.getSampleLimit())) {
            return singletonList(this);
        }

        if (bundles < 2) {
            return singletonList(this);
        }

        int splitCount = (int) (estimateSize() / bundles);

        log.info("split number : " + splitCount + ",  the size of every split from platform : " + bundles);

        if (splitCount > 1) {
            List<String> queries4Split = SplitUtil.getQueries4Split(configuration, service, splitCount, conn);

            if (queries4Split == null || queries4Split.size() < 2) {
                return singletonList(this);
            }
            return queries4Split
                    .stream()
                    .map(query4Split -> cloneMapperAndSetSplitParameter4Reader(query4Split))
                    .collect(Collectors.toList());
        }

        return singletonList(this);
    }

    private DocDBInputMapper cloneMapperAndSetSplitParameter4Reader(String query4Split) {
        DocDBInputMapper mapper =
                new DocDBInputMapper(configuration, service, recordBuilderFactory, connectionService, i18nMessage);
        mapper.setQuery4Split(query4Split);
        return mapper;
    }

    @Emitter
    public DocDBInputSource createWorker() {
        // here we create an actual worker,
        // you are free to rework the configuration etc but our default generated implementation
        // propagates the partition mapper entries.
        return new DocDBInputSource(configuration, service, recordBuilderFactory, connectionService, i18nMessage,
                query4Split);
    }
}