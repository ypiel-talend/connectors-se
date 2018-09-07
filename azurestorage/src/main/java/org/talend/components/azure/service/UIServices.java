package org.talend.components.azure.service;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Suggestions;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class UIServices {
    public static final String COLUMN_NAMES = "COLUMN_NAMES";
    @Suggestions(value = COLUMN_NAMES)
    public SuggestionValues getColumnNames(@Option List<String> schema) {
        List<SuggestionValues.Item> suggestionItemList = schema.stream().map(s -> new SuggestionValues.Item(s, s))
                .collect(Collectors.toList());
        return new SuggestionValues(false, suggestionItemList);
    }
}
