package org.talend.components.azure.service;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ComparisonTest {

    @Test
    public void testPossibleValuesHasAllEnumValues() {
        List<String> possibleValues = Comparison.possibleValues();

        assertEquals(possibleValues.size(), Comparison.values().length);
        Arrays.stream(Comparison.values()).forEach(value -> assertTrue(possibleValues.contains(value.toString())));
    }

    @Test
    public void testGetQueryComparison() {
        String expectedQueryComparison = "eq";
        String queryComparison = Comparison.getQueryComparisons(Comparison.EQUAL.toString());

        assertEquals(expectedQueryComparison, queryComparison);
    }

    @Test(expected = IllegalArgumentException.class)
    public void verifyGetQueryComparisonThrowAnException() {
        String illegalValue = "abc";
        Comparison.getQueryComparisons(illegalValue);
    }
}