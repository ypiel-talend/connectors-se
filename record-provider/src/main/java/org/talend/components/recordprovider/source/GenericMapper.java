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
package org.talend.components.recordprovider.source;

import lombok.extern.slf4j.Slf4j;
import org.talend.components.recordprovider.conf.Config;
import org.talend.components.recordprovider.service.GenericService;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Assessor;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.input.PartitionSize;
import org.talend.sdk.component.api.input.Split;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Version(1)
@Icon(Icon.IconType.STAR)
@PartitionMapper(name = "recordprovider")
@Documentation("")
public class GenericMapper implements Serializable {

    private final Config config;

    private int split = -1;

    private GenericService service;

    public GenericMapper(@Option("configuration") final Config config, final GenericService service) {
        this.service = service;
        this.config = config;
    }

    @Assessor
    public long estimateSize() {
        return 0;
    }

    public GenericMapper setSplit(final int split) {
        this.split = split;
        return this;
    }

    @Split
    public List<GenericMapper> split(@PartitionSize final long bundleSize) {
        Integer splits = config.getDataset().getSplits();
        if (config.isOverwriteDataset()) {
            splits = config.getSplits();
        }

        log.info("GenericMapper nb split : " + splits);

        return IntStream.range(0, splits).mapToObj(i -> new GenericMapper(config, service).setSplit(i + 1))
                .collect(Collectors.toList());
    }

    @Emitter
    public GenericEmitter createSource() {
        return new GenericEmitter(split, config, service);
    }
}
