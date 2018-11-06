// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.azure.service;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.talend.components.azure.common.Comparison;

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

    @Test
    public void verifyGetQueryComparisonThrowAnException() {
        String illegalValue = "abc";
        Assertions.assertThrows(RuntimeException.class, () -> Comparison.getQueryComparisons(illegalValue));
    }
}