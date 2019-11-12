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
package org.talend.components.extension;

import lombok.extern.slf4j.Slf4j;
import org.talend.sdk.component.container.Container;
import org.talend.sdk.component.runtime.manager.ComponentFamilyMeta;
import org.talend.sdk.component.runtime.manager.ContainerComponentRegistry;
import org.talend.sdk.component.runtime.manager.spi.ContainerListenerExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class AutoPollingExtension implements ContainerListenerExtension {

    @Override
    public void onCreate(Container container) {
        log.info("XXXXXXXXXXXXXXXX YPL CREATE : " + container.getId());
        Optional.ofNullable(container.get(ContainerComponentRegistry.class))
                .ifPresent(this::registerPollingMappers);
    }

    @Override
    public void onClose(Container container) {
        log.info("YYYYYYYYYYYYYY YPL CREATE : " + container.getId());
    }

    private void registerPollingMappers(final ContainerComponentRegistry registry) {

        registry.getComponents().values().forEach(family -> {

            log.info("Family ::" + family.getName());

            final Map<String, ComponentFamilyMeta.PartitionMapperMeta> mappers = family.getPartitionMappers();

            final List<ComponentFamilyMeta.PartitionMapperMeta> pollables = mappers.values().stream()
                    .filter(this::isPollable)
                    .collect(Collectors.toList());

            pollables.stream().forEach(mapperMeta -> log.info("===> " + mapperMeta.getType().getName() + " / " + mapperMeta.getName() + " :: " + mapperMeta.toString()));

            /*if (!pollables.isEmpty()) {
                mappers.putAll(pollables.stream()
                        .map(this::toPollable)
                        .collect(toMap(ComponentFamilyMeta.PartitionMapperMeta::getName, identity())));
            }*/

        });
    }

    private boolean isPollable(final ComponentFamilyMeta.PartitionMapperMeta value) {
        // return value.getType().isAnnotationPresent(Pollable.class);
        return true;
    }
}