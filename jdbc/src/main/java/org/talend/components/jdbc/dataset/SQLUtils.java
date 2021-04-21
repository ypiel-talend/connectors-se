package org.talend.components.jdbc.dataset;

import com.veracode.annotation.SQLQueryCleanser;

public class SQLUtils {

    @SQLQueryCleanser
    public static String cleanSQL(final String sql) {
        return sql;
    }

}
