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

import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.talend.components.solr.service.Messages;
import org.talend.components.solr.service.SolrConnectorUtils;

import javax.json.JsonObject;

public class SolrActionExecutorFactory {

    private UpdateRequest request;

    private SolrConnectorUtils utils;

    private SolrAction action;

    private Messages i18n;

    public SolrActionExecutorFactory(UpdateRequest request, SolrConnectorUtils utils, SolrAction action, Messages i18n) {
        this.request = request;
        this.utils = utils;
        this.action = action;
        this.i18n = i18n;
    }

    private void updateDocument(JsonObject record) {
        SolrInputDocument doc = new SolrInputDocument();
        record.keySet().forEach(e -> doc.addField(e, utils.trimQuotes(getStringValue(record, e))));
        request.add(doc);
    }

    private String getStringValue(JsonObject record, String key) {
        if (record != null && record.get(key) != null) {
            return record.get(key).toString();
        }
        return "";
    }

    private void deleteDocument(JsonObject record) {
        String query = utils.createQueryFromRecord(record);
        request.deleteByQuery(query);
    }

    public SolrActionExecutor getSolrActionExecutor() throws UnsupportedSolrActionException {
        if (SolrAction.UPSERT == action) {
            return this::updateDocument;
        } else if (SolrAction.DELETE == action) {
            return this::deleteDocument;
        }
        throw new UnsupportedSolrActionException(i18n.unsupportedSolrAction());
    }

}
