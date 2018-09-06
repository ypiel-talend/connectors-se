package org.talend.components.azure.table.input;

import com.microsoft.azure.storage.table.EdmType;
import com.microsoft.azure.storage.table.TableQuery;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.talend.components.azure.common.AzureTableConnection;
import org.talend.components.azure.common.NameMapping;
import org.talend.components.azure.service.Comparison;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Structure;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@GridLayout(value = {
    @GridLayout.Row("azureConnection"),
        @GridLayout.Row("useFilterExpression"),
        @GridLayout.Row("filterExpressions"),
        @GridLayout.Row("dieOnError")
}, names = GridLayout.FormType.MAIN)

@GridLayout(value = {
        @GridLayout.Row("nameMappings"),
        @GridLayout.Row("schema")
}, names = GridLayout.FormType.ADVANCED)
@Documentation("TODO fill the documentation for this configuration")
@DataSet("Input")
@Data
public class InputTableMapperConfiguration implements Serializable {
    @Option
    @Documentation("bl")
    private AzureTableConnection azureConnection;

    @Option
    @Documentation("bol bl")
    private boolean useFilterExpression;

    @Option
    @Documentation("table")
    @ActiveIf(target = "useFilterExpression", value = "true")
    //TODO sort columns
    private List<FilterExpression> filterExpressions;

    @Option
    @Documentation("die")
    private boolean dieOnError = true;


    //TODO sort columns
    @Option
    @Documentation("ah")
    private List<NameMapping> nameMappings;

    @Option
    @Structure(discoverSchema = "guessSchema", type = Structure.Type.OUT)
    @Documentation("SOS")
    private List<String> schema;

    private enum Function {
        EQUAL ("EQUAL", TableQuery.QueryComparisons.EQUAL),
        NOT_EQUAL ("NOT EQUAL", TableQuery.QueryComparisons.NOT_EQUAL),
        GREATER_THAN ("GREATER THAN", TableQuery.QueryComparisons.GREATER_THAN),
        GT_OR_EQ ("GREATER THAN OR EQUAL", TableQuery.QueryComparisons.GREATER_THAN_OR_EQUAL),
        LESS_THAN ("LESS THAN", TableQuery.QueryComparisons.LESS_THAN),
        LT_OR_EQ ("LESS THAN OR EQUAL", TableQuery.QueryComparisons.LESS_THAN_OR_EQUAL);

        private final String displayName;

        private final String queryComparison;

        Function(String displayName, String queryComparison) {
            this.displayName = displayName;
            this.queryComparison = queryComparison;
        }
    }


    private enum Predicate {
        AND("AND", TableQuery.Operators.AND),
        OR("OR", TableQuery.Operators.OR);

        private String displayName;

        private String operator;

        private static Map<String, Predicate> mapPossibleValues = new HashMap<>();

        private static List<String> possibleValues = new ArrayList<>();

        static {
            for (Predicate predicate : values()) {
                mapPossibleValues.put(predicate.displayName, predicate);
                possibleValues.add(predicate.displayName);
            }
        }

        Predicate(String displayName, String operator) {
            this.displayName = displayName;
            this.operator = operator;
        }

        /**
         * Convert String predicat to Azure Type {@link TableQuery.Operators}
         */
        public static String getOperator(String p) {

            if (!mapPossibleValues.containsKey(p)) {
                throw new IllegalArgumentException(String.format("Invalid value %s, it must be %s", p, possibleValues));
            }
            return mapPossibleValues.get(p).operator;
        }

        @Override
        public String toString() {
            return this.displayName;
        }
    }


    private enum FieldType {
        STRING("STRING", EdmType.STRING),

        NUMERIC("NUMERIC", EdmType.INT32),

        DATE("DATE", EdmType.DATE_TIME),

        GUID("GUID", EdmType.GUID),

        BOOLEAN("BOOLEAN", EdmType.BOOLEAN);

        private String displayName;

        private EdmType supportedType;

        private static Map<String, FieldType> mapPossibleValues = new HashMap<>();

        private static List<String> possibleValues = new ArrayList<>();

        static {
            for (FieldType supportedFieldType : values()) {
                possibleValues.add(supportedFieldType.displayName);
                mapPossibleValues.put(supportedFieldType.displayName, supportedFieldType);
            }
        }

        FieldType(String displayName, EdmType supportedType) {
            this.displayName = displayName;
            this.supportedType = supportedType;
        }

        /**
         * Convert String type names to Azure Type {@link EdmType}
         */
        public static EdmType getEdmType(String ft) {
            if (!mapPossibleValues.containsKey(ft)) {
                throw new IllegalArgumentException(String.format("Invalid value %s, it must be %s", ft, possibleValues));
            }
            return mapPossibleValues.get(ft).supportedType;
        }

        @Override
        public String toString() {
            return this.displayName;
        }


    }

    @Data
//    @OptionsOrder({"column", "function", "value", "predicate", "fieldType"})
//    FIXME: OptionsOrder is not working now
    public static class FilterExpression {
        @Option
        @Documentation("column name")
        //TODO take column list from schema
        private String column;

        @Option
        @Documentation("func")
        private Function function = Function.EQUAL;

        @Option
        @Documentation("value")
        private String value;

        @Option
        @Documentation("doc")
        private Predicate predicate = Predicate.AND;

        @Option
        @Documentation("fieldType")
        private FieldType fieldType = FieldType.STRING;
    }
    
    //TODO move it to util class
    public String generateCombinedFilterConditions() {
        String filter = "";
        if (isValidFilterExpression()) {
            for (FilterExpression filterExpression: filterExpressions) {
                String cfn = filterExpression.function.displayName;
                String cop = filterExpression.predicate.toString();
                String typ = filterExpression.fieldType.toString();

                String filterB = TableQuery.generateFilterCondition(filterExpression.column,
                        Comparison.getQueryComparisons(cfn), filterExpression.value, FieldType.getEdmType(typ));

                if (!filter.isEmpty()) {
                    filter = TableQuery.combineFilters(filter, Predicate.getOperator(cop), filterB);
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
    private boolean isValidFilterExpression() {

        if (filterExpressions == null) {
            return false;
        }
        for (FilterExpression filterExpression: filterExpressions) {
            if (StringUtils.isEmpty(filterExpression.column) || filterExpression.fieldType == null
                    || filterExpression.function == null || StringUtils.isEmpty(filterExpression.value)
                    || filterExpression.predicate == null) {
                return false;
            }
        }

        return true;
    }
}
