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

import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrException;
import org.talend.components.solr.service.SolrConnectorUtils;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;

import javax.annotation.PostConstruct;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Documentation("Solr input source. Provides to receive data from Solr data collections")
public class SolrInputSource implements Serializable {

    private final SolrInputMapperConfiguration configuration;

    private final SolrConnectorUtils util;

    private final JsonBuilderFactory jsonBuilderFactory;

    // private SolrClient solr;

    private transient Iterator<SolrDocument> iter;

    public SolrInputSource(@Option("configuration") final SolrInputMapperConfiguration configuration,
            final JsonBuilderFactory jsonBuilderFactory, final SolrConnectorUtils util) {
        this.configuration = configuration;
        this.util = util;
        this.jsonBuilderFactory = jsonBuilderFactory;
    }

    @PostConstruct
    public void init() {
        try (SolrClient solr = new HttpSolrClient.Builder(configuration.getDataset().getFullUrl()).build()) {
            SolrQuery query = util.generateQuery(configuration);
            QueryRequest req = new QueryRequest(query);
            req.setBasicAuthCredentials(configuration.getDataset().getDataStore().getLogin(),
                    configuration.getDataset().getDataStore().getPassword());
            List<SolrDocument> resultList = executeSolrQuery(solr, req);
            iter = resultList.iterator();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<SolrDocument> executeSolrQuery(SolrClient solr, QueryRequest req) {
        List<SolrDocument> resultList;
        try {
            resultList = req.process(solr).getResults();
        } catch (SolrServerException | IOException | SolrException e) {
            log.error(util.getMessages(e));
            resultList = new ArrayList<>();
        }
        return resultList;
    }

    @Producer
    public JsonObject next() {
        if (iter.hasNext()) {
            JsonObjectBuilder jsonBuilder = jsonBuilderFactory.createObjectBuilder();
            iter.next().forEach((key, value) -> jsonBuilder.add(key, value.toString()));
            return jsonBuilder.build();
        }
        return null;
    }

    // @PreDestroy
    // public void release() {
    // try {
    // solr.close();
    // } catch (IOException e) {
    // log.error(e.getMessage(), e);
    // }
    // }
}