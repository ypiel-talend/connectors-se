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
package org.talend.components.salesforce.output;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.annotation.PreDestroy;

import com.sforce.soap.partner.IField;
import com.sforce.ws.ConnectionException;

import org.talend.components.salesforce.configuration.OutputConfig;
import org.talend.components.salesforce.service.Messages;
import org.talend.components.salesforce.service.SalesforceOutputService;
import org.talend.components.salesforce.service.SalesforceService;
import org.talend.components.salesforce.service.operation.ConnectionFacade;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.configuration.LocalConfiguration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Version
@Icon(value = Icon.IconType.CUSTOM, custom = "file-salesforce-output")
@Processor(name = "SalesforceOutput", family = "Salesforce")
@Documentation("Salesforce output")
public class SalesforceOutput implements Serializable {

    private final OutputConfig configuration;

    private final SalesforceService service;

    private final LocalConfiguration localConfiguration;

    private transient SalesforceOutputService outputService;

    private Messages messages;

    public SalesforceOutput(@Option("configuration") final OutputConfig outputConfig,
            final LocalConfiguration localConfiguration,
            final SalesforceService service, final Messages messages) {
        this.configuration = outputConfig;
        this.service = service;
        this.localConfiguration = localConfiguration;
        this.messages = messages;
    }

    @ElementListener
    public void onNext(@Input final Record record) throws IOException {
        if (outputService == null) {
            try {
                final ConnectionFacade cnx = this.service
                        .buildConnection(configuration.getModuleDataSet().getDataStore(),
                                localConfiguration);
                outputService = new SalesforceOutputService(configuration, cnx, this.messages);
                Map<String, IField> fieldMap =
                        service.getFieldMap(cnx, configuration.getModuleDataSet().getModuleName());
                outputService.setFieldMap(fieldMap);
            } catch (ConnectionException e) {
                throw service.handleConnectionException(e);
            }
        }
        outputService.write(record);
    }

    @PreDestroy
    public void release() throws IOException {
        if (outputService != null) {
            outputService.finish();
        }
    }
}