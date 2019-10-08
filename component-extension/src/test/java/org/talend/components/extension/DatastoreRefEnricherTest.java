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

import org.junit.jupiter.api.Test;
import org.talend.sdk.component.container.Container;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.manager.ComponentFamilyMeta;
import org.talend.sdk.component.runtime.manager.ContainerComponentRegistry;
import org.talend.sdk.component.runtime.manager.ParameterMeta;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WithComponents(value = "org.talend.components.extension.components")
class DatastoreRefEnricherTest {

    @Injected
    private BaseComponentsHandler componentsHandler;

    @Test
    void validDatastoreRef() {
        final Container plugin = componentsHandler.asManager().findPlugin("test-classes")
                .orElseThrow(() -> new IllegalStateException("test plugin can't be found"));
        assertNotNull(plugin);

        ComponentFamilyMeta family = plugin.get(ContainerComponentRegistry.class).getComponents().get("Test");
        Optional<ParameterMeta> ref = getDatastorRef(family, "Valid");

        assertTrue(ref.isPresent());
        assertEquals("Test", ref.get().getMetadata().get("tcomp::configurationtyperef::family"));
        assertEquals("default", ref.get().getMetadata().get("tcomp::configurationtyperef::name"));

        // filters
        assertEquals("type", ref.get().getMetadata().get("tcomp::configurationtyperef::filter[0].key"));
        assertEquals("Oauth1", ref.get().getMetadata().get("tcomp::configurationtyperef::filter[0].value"));

        assertEquals("type", ref.get().getMetadata().get("tcomp::configurationtyperef::filter[1].key"));
        assertEquals("Oauth2", ref.get().getMetadata().get("tcomp::configurationtyperef::filter[1].value"));
    }

    private Optional<ParameterMeta> getDatastorRef(ComponentFamilyMeta family, String component) {
        return family.getPartitionMappers().get(component).getParameterMetas().get().stream().flatMap(this::flatten)
                .filter(p -> p.getMetadata().containsKey("tcomp::configurationtyperef::family")).findFirst();
    }

    Stream<ParameterMeta> flatten(final ParameterMeta meta) {
        return concat(of(meta), meta.getNestedParameters().stream().flatMap(this::flatten));
    }

}
