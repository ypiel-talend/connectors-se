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
package org.talend.components.solr.output;

import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrException;
import org.talend.components.solr.service.Messages;
import org.talend.components.solr.service.SolrConnectorUtils;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.AfterGroup;
import org.talend.sdk.component.api.processor.BeforeGroup;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Processor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.json.JsonObject;
import java.io.IOException;
import java.io.Serializable;

@Slf4j
@Version(1)
@Icon(value = Icon.IconType.CUSTOM, custom = "SolrOutput")
@Processor(name = "Output")
@Documentation("Solr processor. " + "The component provides deletion or creation of documents from Solr. "
        + "Parameters are taken from input components")
public class SolrProcessorOutput implements Serializable {

    private final SolrProcessorOutputConfiguration configuration;

    // private final SolrConnectorService service;

    private final SolrConnectorUtils utils;

    private final Messages i18n;

    private transient SolrClient solr;

    private transient UpdateRequest request;

    private transient SolrActionExecutor solrActionExecutor;

    public SolrProcessorOutput(@Option("configuration") final SolrProcessorOutputConfiguration configuration,
            final SolrConnectorUtils utils, final Messages i18n) {
        this.configuration = configuration;
        // this.service = service;
        this.utils = utils;
        this.i18n = i18n;
    }

    @PostConstruct
    public void init() {
        solr = new HttpSolrClient.Builder(configuration.getDataset().getFullUrl()).build();
        request = new UpdateRequest();
        request.setBasicAuthCredentials(configuration.getDataset().getDataStore().getLogin(),
                configuration.getDataset().getDataStore().getPassword());
        solrActionExecutor = new SolrActionExecutorFactory(request, utils, configuration.getAction(), i18n)
                .getSolrActionExecutor();
    }

    @BeforeGroup
    public void beforeGroup() {
        request.clear();
    }

    @ElementListener
    public void onNext(@Input final JsonObject record) {
        solrActionExecutor.execute(record);
    }

    @AfterGroup
    public void afterGroup() {
        try {
            request.commit(solr, null);
        } catch (SolrServerException | IOException | SolrException e) {
            log.error(utils.getMessages(e));
        }
    }

    @PreDestroy
    public void release() {
        try {
            solr.close();
        } catch (IOException | SolrException e) {
            log.error(utils.getMessages(e));
        }
    }
}