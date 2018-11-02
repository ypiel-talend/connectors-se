package org.talend.components.azure.service;

import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.table.EdmType;
import com.microsoft.azure.storage.table.TableQuery;

import lombok.Data;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.talend.components.azure.common.Comparison;
import org.talend.components.azure.table.input.InputProperties;
import org.talend.sdk.component.api.record.Schema;

@Data
public class AzureTableUtils {

    public static final String TABLE_TIMESTAMP = "Timestamp";

    public static String generateCombinedFilterConditions(InputProperties options) {
        String filter = "";
        if (isValidFilterExpression(options)) {
            for (InputProperties.FilterExpression filterExpression : options.getFilterExpressions()) {
                String cfn = filterExpression.getFunction().getDisplayName();
                String cop = filterExpression.getPredicate().toString();
                String typ = filterExpression.getFieldType().toString();

                String filterB = TableQuery.generateFilterCondition(filterExpression.getColumn(),
                        Comparison.getQueryComparisons(cfn), filterExpression.getValue(),
                        InputProperties.FieldType.getEdmType(typ));

                filter = filter.isEmpty() ? filterB
                        : TableQuery.combineFilters(filter, InputProperties.Predicate.getOperator(cop), filterB);
            }
        }
        return filter;
    }

    /**
     * this method check if the data in the Filter expression is valid and can produce a Query filter.<br/>
     * the table is valid if :<br>
     * 1) all column, fieldType, function, operand and predicate lists are not null<br/>
     * 2) values in the lists column, fieldType, function, operand and predicate are not empty
     *
     * <br/>
     *
     * @return {@code true } if the two above condition are true
     *
     */
    private static boolean isValidFilterExpression(InputProperties options) {

        if (options.getFilterExpressions() == null) {
            return false;
        }
        for (InputProperties.FilterExpression filterExpression : options.getFilterExpressions()) {
            if (StringUtils.isEmpty(filterExpression.getColumn()) || filterExpression.getFieldType() == null
                    || filterExpression.getFunction() == null || StringUtils.isEmpty(filterExpression.getValue())
                    || filterExpression.getPredicate() == null) {
                return false;
            }
        }

        return true;
    }
}
