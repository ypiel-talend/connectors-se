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
package org.talend.components.slack.input;

import lombok.extern.slf4j.Slf4j;
import org.talend.components.slack.dataset.SlackUserDataset;
import org.talend.components.slack.service.I18nMessage;
import org.talend.components.slack.service.SlackService;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;

import javax.annotation.PostConstruct;
import javax.json.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.talend.components.slack.SlackApiConstants.ATTR_CODE;
import static org.talend.components.slack.SlackApiConstants.ATTR_MESSAGE;

@Slf4j
public class SlackUserSource implements Serializable {

    protected final SlackService slackService;

    protected final I18nMessage i18n;

    protected final JsonBuilderFactory jsonFactory;

    protected final JsonReaderFactory jsonReader;

    protected final JsonWriterFactory jsonWriter;

    private SlackUserDataset dataset;

    protected final SlackUserInputConfiguration configuration;

    protected transient Map<String, Schema.Entry> schema;

    protected transient Iterator<JsonValue> resultIterator;

    private transient SlackService.StartingPoint startingPoint;

    public SlackUserSource(@Option("configuration") final SlackUserInputConfiguration configuration, final SlackService service) {
        this.configuration = configuration;
        this.dataset = configuration.getDataset();
        this.i18n = service.getI18n();
        this.jsonFactory = service.getJsonFactory();
        this.jsonReader = service.getJsonReader();
        this.jsonWriter = service.getJsonWriter();
        this.slackService = service;
    }

    @PostConstruct
    public void init() {
        schema = buildSchemaMap(slackService.getUserSchema());
        startingPoint = new SlackService.StartingPoint();
    }

    @Producer
    public Record next() {
        JsonValue next;
        log.debug("Slack Source next");
        if (resultIterator == null) {
            resultIterator = slackService.getUsers(dataset.getConnection(), startingPoint);
            if (resultIterator == null) {
                log.debug("retrieve nothing");
                return null;
            }
        }
        boolean hasNext = resultIterator.hasNext();
        if (hasNext) {
            next = resultIterator.next();
            log.debug("retrieve record: {}", next);
            return slackService.convertToRecord(next.asJsonObject(), schema);
        } else {
            resultIterator = null;
            if (startingPoint.getNextPage()) {
                return next();
            } else {
                log.debug("retrieved all records");
                return null;
            }
        }
    }

    private Map<String, Schema.Entry> buildSchemaMap(final Schema entitySchema) {
        log.debug("[buildSchemaMap] {}", entitySchema);
        Map<String, Schema.Entry> s = new HashMap<>();
        if (entitySchema != null) {
            for (Schema.Entry entry : entitySchema.getEntries()) {
                s.put(entry.getName(), entry);
            }
        }
        return s;
    }

    /**
     * Convert Slack Errors array to a single String (generally for Exception throwing).
     *
     * @param errors
     * @return flattened string
     */
    public String getErrors(JsonArray errors) {
        StringBuffer error = new StringBuffer();
        for (JsonObject json : errors.getValuesAs(JsonObject.class)) {
            error.append(String.format("[%s] %s", json.getString(ATTR_CODE), json.getString(ATTR_MESSAGE)));
        }

        return error.toString();
    }

}
