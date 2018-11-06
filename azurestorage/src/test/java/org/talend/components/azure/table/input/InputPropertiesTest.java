package org.talend.components.azure.table.input;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.talend.components.azure.common.Comparison;
import org.talend.components.azure.service.AzureTableUtils;

public class InputPropertiesTest {

    @Test
    public void testGenerateFilterExpression() {
        String columnName = "someColumn";
        InputProperties configuration = new InputProperties();
        InputProperties.FilterExpression expression1 = new InputProperties.FilterExpression();
        expression1.setColumn(columnName);
        expression1.setFieldType(InputProperties.FieldType.NUMERIC);
        expression1.setFunction(Comparison.GREATER_THAN);
        expression1.setValue("2");
        List<InputProperties.FilterExpression> filterExpressions = new ArrayList<>();
        filterExpressions.add(expression1);
        configuration.setFilterExpressions(filterExpressions);

        String resultFilterExpression = AzureTableUtils.generateCombinedFilterConditions(configuration);

        assertFalse(resultFilterExpression.isEmpty());
        assertTrue(resultFilterExpression.contains(columnName));
        assertTrue(resultFilterExpression.contains("gt"));
        assertTrue(resultFilterExpression.contains("2"));
    }
}