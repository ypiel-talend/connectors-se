/*
 * Copyright (C) 2006-2022 Talend Inc. - www.talend.com
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
package org.talend.components.google.storage.datastore;

import java.io.Serializable;

import org.talend.components.google.storage.service.GSService;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@DataStore(GSDataStore.NAME)
@Checkable(GSService.ACTION_HEALTH_CHECK)
@GridLayout({ @GridLayout.Row({ "jsonCredentials" }) })
@Documentation("Connector for google cloud storage")
public class GSDataStore implements Serializable {

    /** Serialization */
    private static final long serialVersionUID = -8983548581539406543L;

    /** component name */
    public static final String NAME = "GoogleStorageDataStore";

    @Option
    @Credential
    @Required
    @Documentation("Google credential (JSON)")
    private String jsonCredentials;

}
