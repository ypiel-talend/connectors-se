package org.talend.components.azure.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.talend.sdk.component.api.service.completion.SuggestionValues;

public class UIServicesTest {

    @Test
    public void testAllColumnsReturnsSameSize() {
        List<String> expectedColumnNames = new ArrayList<>();
        expectedColumnNames.add("column1");
        expectedColumnNames.add("column2");
        expectedColumnNames.add("column3");
        UIServices services = new UIServices();
        SuggestionValues columns = services.getColumnNames(expectedColumnNames);

        assertEquals(expectedColumnNames.size(), columns.getItems().size());
    }

    @Test
    public void testGetColumnNamesValuesAreNotCacheable() {
        SuggestionValues columnValues = new UIServices().getColumnNames(Collections.emptyList());

        assertFalse(columnValues.isCacheable());
    }

    @Test
    public void testColumnNamesRemainsTheSame() {
        String expectedColumn = "expectedName";
        List<String> expectedColumnList = new ArrayList<>();
        expectedColumnList.add(expectedColumn);

        SuggestionValues columns = new UIServices().getColumnNames(expectedColumnList);

        Iterator<SuggestionValues.Item> itemIterator = columns.getItems().iterator();
        assertEquals(1, columns.getItems().size());
        while (itemIterator.hasNext()) {
            SuggestionValues.Item item = itemIterator.next();
            assertEquals(expectedColumn, item.getId());
        }

    }

}