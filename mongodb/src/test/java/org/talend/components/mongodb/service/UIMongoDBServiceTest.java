package org.talend.components.mongodb.service;

import org.junit.jupiter.api.Test;
import org.talend.components.mongodb.dataset.MongoDBDataset;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.junit5.WithComponents;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WithComponents("org.talend.components.mongodb")
public class UIMongoDBServiceTest {

    @Service
    private UIMongoDBService uiMongoDBService;

    @Test
    public void testSchemaFields() {
        MongoDBDataset dataset = new MongoDBDataset();
        dataset.setSchema(Arrays.asList("column1", "column2"));

        SuggestionValues values = uiMongoDBService.getFields(dataset);

        assertEquals(2, values.getItems().size());
        assertThat(values.getItems().stream().map(SuggestionValues.Item::getId).collect(Collectors.toList()), containsInAnyOrder(dataset.getSchema().toArray()));
    }
}
