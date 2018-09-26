package org.talend.components.azure.table.input;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class InputTableMapperConfigurationTest {

    @Test
    public void testGenerateFilterExpression() {
        String columnName = "someColumn";
        InputTableMapperConfiguration configuration = new InputTableMapperConfiguration();
        InputTableMapperConfiguration.FilterExpression expression1 = new InputTableMapperConfiguration.FilterExpression();
        expression1.setColumn(columnName);
        expression1.setFieldType(InputTableMapperConfiguration.FieldType.NUMERIC);
        expression1.setFunction(InputTableMapperConfiguration.Function.GREATER_THAN);
        expression1.setValue("2");
        List<InputTableMapperConfiguration.FilterExpression> filterExpressions = new ArrayList<>();
        filterExpressions.add(expression1);
        configuration.setFilterExpressions(filterExpressions);

        String resultFilterExpression = configuration.generateCombinedFilterConditions();

        assertFalse(resultFilterExpression.isEmpty());
        assertTrue(resultFilterExpression.contains(columnName));
        assertTrue(resultFilterExpression.contains("gt"));
        assertTrue(resultFilterExpression.contains("2"));
    }
}