package org.talend.components.azure.service;

import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.table.EdmType;
import com.microsoft.azure.storage.table.TableQuery;

import lombok.Data;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.talend.components.azure.table.input.InputProperties;
import org.talend.sdk.component.api.record.Schema;

@Data
public class AzureTableUtils {

    private static OperationContext talendOperationContext;

    private static final String USER_AGENT_KEY = "User-Agent";

    private static final String USER_AGENT_VALUE = "APN/1.0 Talend/7.1 TaCoKit/1.0.3";

    public static final String TABLE_TIMESTAMP = "Timestamp";

    public static OperationContext getTalendOperationContext() {
        if (talendOperationContext == null) {
            talendOperationContext = new OperationContext();
            HashMap<String, String> talendUserHeaders = new HashMap<>();
            talendUserHeaders.put(USER_AGENT_KEY, USER_AGENT_VALUE);
            talendOperationContext.setUserHeaders(talendUserHeaders);
        }

        return talendOperationContext;
    }

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

                if (!filter.isEmpty()) {
                    filter = TableQuery.combineFilters(filter, InputProperties.Predicate.getOperator(cop), filterB);
                } else {
                    filter = filterB;
                }
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

    public static Schema.Type getAppropriateType(EdmType edmType) {
        switch (edmType) {
        case BOOLEAN:
            return Schema.Type.BOOLEAN;
        case BYTE:
        case SBYTE:
        case INT16:
        case INT32:
            return Schema.Type.INT;
        case INT64:
        case DECIMAL:
        case SINGLE:
        case DOUBLE:
            return Schema.Type.DOUBLE;
        case DATE_TIME:
        case DATE_TIME_OFFSET:
            return Schema.Type.DATETIME;
        default:
            return Schema.Type.STRING;
        }
    }
}
