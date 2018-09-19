package org.talend.components.azure.service;

import java.util.ArrayList;
import java.util.List;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Suggestions;

@Service
public class UIServices {

    public static final String COLUMN_NAMES = "COLUMN_NAMES";

    @Suggestions(value = COLUMN_NAMES)
    public SuggestionValues getColumnNames(@Option List<String> schema) {
        List<SuggestionValues.Item> suggestionItemList = new ArrayList<>();

        schema.stream().map(s -> new SuggestionValues.Item(s, s)).forEach(suggestionItemList::add);

        return new SuggestionValues(false, suggestionItemList);
    }
}
