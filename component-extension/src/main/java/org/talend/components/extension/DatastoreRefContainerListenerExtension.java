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

import org.talend.sdk.component.container.Container;
import org.talend.sdk.component.runtime.manager.ComponentFamilyMeta;
import org.talend.sdk.component.runtime.manager.ContainerComponentRegistry;
import org.talend.sdk.component.runtime.manager.ParameterMeta;
import org.talend.sdk.component.runtime.manager.spi.ContainerListenerExtension;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static java.util.stream.Stream.concat;

// FIXME : move to dataset module
public class DatastoreRefContainerListenerExtension implements ContainerListenerExtension {

    @Override
    public void onCreate(final Container container) {
        ContainerComponentRegistry registry = container.get(ContainerComponentRegistry.class);
        if (registry != null) {
            registry.getComponents().values().stream()
                    .flatMap(cfm -> concat(cfm.getPartitionMappers().values().stream(), cfm.getProcessors().values().stream()))
                    .map(ComponentFamilyMeta.BaseMeta.class::cast).forEach(meta -> rewriteDatasoreRef(meta.getParameterMetas()));
        }
    }

    private void rewriteDatasoreRef(Supplier<List<ParameterMeta>> metas) {
        List<ParameterMeta> parameterMetas = metas.get();
        parameterMetas.stream().filter(
                pm -> pm.getMetadata().entrySet().stream().anyMatch(m -> m.getKey().contains("tcomp::configurationtyperef::")))
                .findFirst().ifPresent(orig -> {
                    parameterMetas.remove(orig);
                    final ParameterMeta ref = new ParameterMeta(orig.getSource(), orig.getJavaType(), ParameterMeta.Type.STRING,
                            orig.getPath(), orig.getName(), orig.getI18nPackages(), emptyList(), emptyList(),
                            new HashMap<>(orig.getMetadata()), orig.isLogMissingResourceBundle());
                    final String name = orig.getMetadata().get("tcomp::configurationtyperef::name");
                    final String family = orig.getMetadata().get("tcomp::configurationtyperef::family");
                    ref.getMetadata().put("tcomp::action::dynamic_values",
                            "builtin::references(type=datastore,name=" + name + ",family=" + family + ")");

                    // make the datastore property hidden - i don't have a better way to do that ! :/
                    parameterMetas
                            .add(new ParameterMeta(orig.getSource(), orig.getJavaType(), orig.getType(), orig.getPath() + "__ref",
                                    orig.getName() + "__ref", orig.getI18nPackages(), orig.getNestedParameters(),
                                    orig.getProposals(), orig.getMetadata(), orig.isLogMissingResourceBundle()));
                    parameterMetas.add(ref);
                });

        parameterMetas.stream().filter(m -> !m.getNestedParameters().isEmpty())
                .forEach(m -> rewriteDatasoreRef(() -> m.getNestedParameters()));
    }

    @Override
    public void onClose(final Container container) {

    }
}
