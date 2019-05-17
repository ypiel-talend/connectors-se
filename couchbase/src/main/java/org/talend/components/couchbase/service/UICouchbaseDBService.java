package org.talend.components.couchbase.service;

import org.talend.components.couchbase.dataset.CouchbaseDataSet;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Suggestions;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UICouchbaseDBService {

    public static final String GET_SCHEMA_FIELDS = "loadFields";

    @Service
    private CouchbaseService couchbaseService;

    @Service
    private I18nMessage i18n;

    @Suggestions(GET_SCHEMA_FIELDS)
    public SuggestionValues getFields(CouchbaseDataSet dataset) {
        if (dataset.getSchema() == null || dataset.getSchema().isEmpty()) {
            return new SuggestionValues(false, Collections.emptyList());
        }
        List<SuggestionValues.Item> items = dataset.getSchema().stream().map(s -> new SuggestionValues.Item(s, s))
                .collect(Collectors.toList());
        return new SuggestionValues(false, items);
    }
}
