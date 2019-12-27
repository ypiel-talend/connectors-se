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
package org.talend.components.solr.source;

import org.talend.components.solr.service.SolrConnectorUtils;
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
@Icon(value = Icon.IconType.CUSTOM, custom = "SolrInput")
@PartitionMapper(name = "Input")
@Documentation("Solr Input Mapper")
public class SolrInputMapper implements Serializable {

    private final SolrInputMapperConfiguration configuration;

    private final SolrConnectorUtils util;

    private final JsonBuilderFactory jsonBuilderFactory;

    public SolrInputMapper(@Option("configuration") final SolrInputMapperConfiguration configuration,
            final JsonBuilderFactory jsonBuilderFactory, final SolrConnectorUtils util) {
        this.configuration = configuration;
        this.util = util;
        this.jsonBuilderFactory = jsonBuilderFactory;
    }

    @Assessor
    public long estimateSize() {
        return 1L;
    }

    @Split
    public List<SolrInputMapper> split(@PartitionSize final long bundles) {
        return singletonList(this);
    }

    @Emitter
    public SolrInputSource createWorker() {
        return new SolrInputSource(configuration, jsonBuilderFactory, util);
    }
}