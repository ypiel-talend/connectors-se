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
package org.talend.components.slack.service;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.talend.components.slack.SlackApiConstants;
import org.talend.components.slack.SlackRuntimeException;
import org.talend.components.slack.connection.SlackConnection;
import org.talend.components.slack.dataset.SlackDataset;
import org.talend.components.slack.input.SlackInputConfiguration;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.configuration.Configuration;
import org.talend.sdk.component.api.service.http.Response;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import javax.json.*;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static org.talend.components.slack.SlackApiConstants.*;

@Accessors
@Slf4j
@Service
public class SlackService {

    @Getter
    @Service
    protected I18nMessage i18n;

    @Getter
    @Service
    protected RecordBuilderFactory recordBuilder;

    @Getter
    @Service
    private JsonBuilderFactory jsonFactory;

    @Getter
    @Service
    private JsonReaderFactory jsonReader;

    @Getter
    @Service
    private JsonWriterFactory jsonWriter;

    @Getter
    @Service
    protected MessagesClient messagesClient;

    public void initClients(SlackConnection connection) {
        messagesClient.base(connection.getEndpoint());
    }

    @Data
    public static class StartingPoint {

        @Setter(AccessLevel.NONE)
        private String oldest = "0"; // default is 0, means from beginning

        public void setOldest(String value) {
            try {
                if (Double.parseDouble(value) > Double.parseDouble(oldest)) {
                    this.oldest = value;
                }
            } catch (Exception e) {
                //
            }
        }

        private Boolean nextPage = false;

        private String nextCursor;

        public String toRequestBody() {
            if (nextPage) {
                return "cursor=" + nextCursor;
            } else {
                return "oldest=" + oldest;
            }
        }

    }

    private final int limit = 100;

    public Iterator<JsonValue> getMessages(@Configuration("connection") final SlackConnection connection, String channelName,
            StartingPoint startingPoint) {
        initClients(connection);
        log.debug("Starting point: {}", startingPoint);
        Response<JsonObject> messages = messagesClient.getMessages(
                SlackApiConstants.HEADER_CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED, encodedToken(connection.getToken()),
                channelName, limit, startingPoint.toRequestBody());
        JsonObject result = handleResponse(messages);
        updateStartingPointByPaging(startingPoint, result);
        JsonArray requestResult = result.getJsonArray("messages");
        if (requestResult != null && requestResult.size() > 0) {
            return requestResult.iterator();
        }
        return null;
    }

    public Map<String, String> listChannels(@Configuration("connection") final SlackConnection connection,
            SlackDataset.ChannelType type) {
        initClients(connection);
        Map<String, String> channels = new HashMap<>();
        StartingPoint startingPoint = new StartingPoint();
        do {
            log.debug("Starting point: {}", startingPoint);
            Response<JsonObject> jsonObjectResponse = messagesClient.listChannels(
                    HEADER_CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED, encodedToken(connection.getToken()), type.getValue(),
                    1000, startingPoint.toRequestBody());
            JsonObject result = handleResponse(jsonObjectResponse);
            updateStartingPointByCursor(startingPoint, result);
            JsonArray requestResult = result.getJsonArray("channels");
            if (requestResult != null && requestResult.size() > 0) {
                requestResult.iterator().forEachRemaining(
                        r -> channels.put(r.asJsonObject().getString("id"), r.asJsonObject().getString("name")));
            }
        } while (startingPoint.getNextPage());
        return channels;
    }

    public Boolean checkAuth(@Configuration("connection") final SlackConnection connection) {
        initClients(connection);
        Response<JsonObject> jsonObjectResponse = messagesClient.checkAuth(HEADER_CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED,
                encodedToken(connection.getToken()));
        JsonObject result = handleResponse(jsonObjectResponse);
        return result.getBoolean("ok");
    }

    private void updateStartingPointByCursor(StartingPoint startingPoint, JsonObject result) {
        try {
            JsonObject responseMetadata = result.getJsonObject("response_metadata");
            String nextCursor = responseMetadata.getString("next_cursor");
            if (nextCursor != null && !"".equals(nextCursor)) {
                startingPoint.setNextPage(true);
                startingPoint.setNextCursor(nextCursor);
            }
        } catch (NullPointerException npe) {
            startingPoint.setNextPage(false);
            startingPoint.setNextCursor("");
        }
    }

    private void updateStartingPointByPaging(StartingPoint startingPoint, JsonObject result) {
        boolean hasMore = result.getBoolean("has_more");
        startingPoint.setNextPage(hasMore);
        if (hasMore) {
            String nextCursor = result.getJsonObject("response_metadata").getString("next_cursor");
            startingPoint.setNextCursor(nextCursor);
        } else {
            startingPoint.setNextCursor("");
        }
    }

    public void updateStartingPointByMessageTS(StartingPoint startingPoint, JsonObject message) {
        String ts = message.getString("ts");
        startingPoint.setOldest(ts);
    }

    private String encodedToken(String token) {
        String encodedToken = " Bearer " + token;
        log.debug("[retrieveAccessToken] [{}] :.", encodedToken);
        return encodedToken;
    }
    /*
     * private Schema getMessagesSchema() {
     * return recordBuilder.newSchemaBuilder(Schema.Type.RECORD)
     * .withEntry(recordBuilder.newEntryBuilder().withName(ATTR_TEXT).withType(Schema.Type.STRING).build())
     * .withEntry(recordBuilder.newEntryBuilder().withName(ATTR_TIMESTAMP).withType(Schema.Type.STRING).build())
     * .withEntry(recordBuilder.newEntryBuilder().withName(ATTR_USERNAME).withType(Schema.Type.STRING).build()).build();
     * }
     * 
     */

    private Schema getMessagesSchema() {
        Schema.Entry.Builder rb = recordBuilder.newEntryBuilder().withName("reactions").withType(Schema.Type.ARRAY)
                .withElementSchema(recordBuilder.newSchemaBuilder(Schema.Type.RECORD)
                        .withEntry(recordBuilder.newEntryBuilder().withName("name").withType(Schema.Type.STRING).build())
                        .withEntry(recordBuilder.newEntryBuilder().withName("count").withType(Schema.Type.STRING).build())
                        .build());

        return recordBuilder.newSchemaBuilder(Schema.Type.RECORD)
                .withEntry(recordBuilder.newEntryBuilder().withName(ATTR_TEXT).withType(Schema.Type.STRING).build())
                .withEntry(recordBuilder.newEntryBuilder().withName(ATTR_TIMESTAMP).withType(Schema.Type.STRING).build())
                .withEntry(recordBuilder.newEntryBuilder().withName(ATTR_USERNAME).withType(Schema.Type.STRING).build())
                .withEntry(rb.build()).build();
    }

    protected JsonArray parseResultFromResponse(Response<JsonObject> response) {
        if (response.status() == 200 && response.body() != null && response.body().getJsonArray(ATTR_RESULT) != null) {
            return response.body().getJsonArray(ATTR_RESULT);
        }
        log.error("[parseResultFromResponse] Error: [{}] headers:{}; body: {}.", response.status(), response.headers(),
                response.body());
        throw new IllegalArgumentException(i18n.invalidOperation());
    }

    public Schema getEntitySchema(final SlackInputConfiguration configuration) {
        Schema s = null;
        Schema.Builder b = recordBuilder.newSchemaBuilder(Schema.Type.RECORD);

        Schema messagesSchema = getMessagesSchema();

        return messagesSchema;
    }

    public JsonObject toJson(final Record record) {
        String recordStr = record.toString().replaceAll("AvroRecord\\{delegate=(.*)\\}$", "$1");
        JsonReader reader = jsonReader.createReader(new StringReader(recordStr));
        Throwable throwable = null;
        JsonObject json;
        try {
            json = reader.readObject();
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (reader != null) {
                if (throwable != null) {
                    try {
                        reader.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    reader.close();
                }
            }
        }
        return json;
    }

    private boolean hasJsonValue(JsonValue value) {
        if (value == null) {
            return false;
        }
        if (value.getValueType().equals(JsonValue.ValueType.NULL)) {
            return false;
        }
        return true;
    }

    public Record convertToRecord(final JsonObject json, final Map<String, Schema.Entry> schema) {
        Record.Builder b = getRecordBuilder().newRecordBuilder();
        log.debug("[convertToRecord] json {} VS schema {}", json.entrySet().size(), schema.keySet().size());
        for (Schema.Entry entry : schema.values()) {
            String key = entry.getName();
            JsonValue val = json.get(key);
            switch (entry.getType()) {
            case ARRAY:
                String ary = "";
                if (val != null) {
                    json.getJsonArray(key).stream().map(JsonValue::toString).collect(joining(","));
                    // not in a sub array
                    if (!ary.contains("{")) {
                        ary = ary.replaceAll("\"", "").replaceAll("(\\[|\\])", "");
                    }
                }
                b.withString(key, ary);
                break;
            case RECORD:
            case BYTES:
            case STRING:
                if (hasJsonValue(val)) {
                    switch (val.getValueType()) {
                    case ARRAY:
                        b.withString(key, json.getJsonArray(key).stream().map(JsonValue::toString).collect(joining(",")));
                        break;
                    case OBJECT:
                        b.withString(key, String.valueOf(json.getJsonObject(key).toString()));
                        break;
                    case STRING:
                        b.withString(key, json.getString(key));
                        break;
                    case NUMBER:
                        b.withString(key, String.valueOf(json.getJsonNumber(key)));
                        break;
                    case TRUE:
                    case FALSE:
                        b.withString(key, String.valueOf(json.getBoolean(key)));
                        break;
                    case NULL:
                        b.withString(key, null);
                        break;
                    }
                } else {
                    b.withString(key, null);
                }
                break;
            case INT:
                b.withInt(key, hasJsonValue(val) ? json.getInt(key) : 0);
                break;
            case LONG:
                b.withLong(key, hasJsonValue(val) ? json.getJsonNumber(key).longValue() : 0);
                break;
            case FLOAT:
            case DOUBLE:
                b.withDouble(key, hasJsonValue(val) ? json.getJsonNumber(key).doubleValue() : 0);
                break;
            case BOOLEAN:
                b.withBoolean(key, hasJsonValue(val) ? json.getBoolean(key) : null);
                break;
            case DATETIME:
                try {
                    b.withDateTime(key,
                            hasJsonValue(val) ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(json.getString(key))
                                    : null);
                } catch (ParseException e1) {
                    log.error("[convertToRecord] Date parsing error: {}.", e1.getMessage());
                }
                break;
            }
        }
        Record record = b.build();
        log.debug("[convertToRecord] returning : {}.", record);
        return record;
    }

    /**
     * Handle a typical Slack response's payload to API call.
     *
     * @param response the http response
     * @return Slack API result
     */
    public JsonObject handleResponse(final Response<JsonObject> response) {
        log.trace("[handleResponse] [{}] body: {}.", response.status(), response.body());
        if (response.status() == SlackApiConstants.HTTP_STATUS_OK) {
            return response.body();
        }
        throw new SlackRuntimeException(response.error(String.class));
    }

}
