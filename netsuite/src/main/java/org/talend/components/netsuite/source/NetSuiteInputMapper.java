/*
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
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
package org.talend.components.netsuite.source;

import org.talend.components.netsuite.dataset.NetsuiteInputDataSet;
import org.talend.components.netsuite.service.NetsuiteService;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Assessor;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.input.PartitionSize;
import org.talend.sdk.component.api.input.Split;
import org.talend.sdk.component.api.meta.Documentation;

import javax.json.JsonBuilderFactory;
import java.io.Serializable;
import java.util.List;

import static java.util.Collections.singletonList;

@Version(1)
@Icon(value = Icon.IconType.CUSTOM, custom = "NetsuiteInput")
@PartitionMapper(name = "Input")
@Documentation("TODO fill the documentation for this mapper")
public class NetsuiteInputMapper implements Serializable {

    private final NetsuiteInputDataSet configuration;

    private final NetsuiteService service;

    private final JsonBuilderFactory jsonBuilderFactory;

    public NetsuiteInputMapper(@Option("configuration") final NetsuiteInputDataSet configuration, final NetsuiteService service,
            final JsonBuilderFactory jsonBuilderFactory) {
        this.configuration = configuration;
        this.service = service;
        this.jsonBuilderFactory = jsonBuilderFactory;
    }

    @Assessor
    public long estimateSize() {
        // this method should return the estimation of the dataset size
        // it is recommended to return a byte value
        // if you don't have the exact size you can use a rough estimation
        return 1L;
    }

    @Split
    public List<NetsuiteInputMapper> split(@PartitionSize final long bundles) {
        // overall idea here is to split the work related to configuration in bundles of size "bundles"
        //
        // for instance if your estimateSize() returned 1000 and you can run on 10 nodes
        // then the environment can decide to run it concurrently (10 * 100).
        // In this case bundles = 100 and we must try to return 10 NetsuiteInputMapper with 1/10 of the overall work each.
        //
        // default implementation returns this which means it doesn't support the work to be split
        return singletonList(this);
    }

    @Emitter
    public NetsuiteInputSource createWorker() {
        // here we create an actual worker,
        // you are free to rework the configuration etc but our default generated implementation
        // propagates the partition mapper entries.
        return new NetsuiteInputSource(configuration, service, jsonBuilderFactory);
    }
}