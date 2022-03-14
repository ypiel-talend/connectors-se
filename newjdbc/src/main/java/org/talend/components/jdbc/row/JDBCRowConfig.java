/*
 * Copyright (C) 2006-2022 Talend Inc. - www.talend.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.talend.components.jdbc.row;

import lombok.Data;
import org.talend.components.jdbc.common.PreparedStatementParameter;
import org.talend.components.jdbc.dataset.JDBCQueryDataSet;
import org.talend.components.jdbc.dataset.JDBCTableDataSet;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;
import java.util.List;

@Data
@GridLayout({
        @GridLayout.Row("dataSet"),
        @GridLayout.Row("dieOnError")
})
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {
        @GridLayout.Row("dataSet"),
        @GridLayout.Row("usePreparedStatement"),
        @GridLayout.Row("preparedStatementParameters"),
        @GridLayout.Row("commitEvery")
})
@Documentation("jdbc row")
public class JDBCRowConfig implements Serializable {

    @Option
    @Documentation("table dataset")
    private JDBCQueryDataSet dataSet;

    // TODO studio will add schema field auto
    // TODO but how to add guess schema auto for tjdbcrow, which should guess from table

    @Option
    @Documentation("")
    private boolean dieOnError;

    // TODO field : propagate query's recordset

    // TODO column choose field : how to map schema to this field, avoiding to call runtime?

    @Option
    @Documentation("")
    private boolean usePreparedStatement;

    // TODO how to inject the var to main part?
    @Option
    @Documentation("")
    private List<PreparedStatementParameter> preparedStatementParameters;

    // TODO detect error on multiple statements field

    @Option
    @Documentation("")
    private int commitEvery = 10000;

    // TODO use query timeout and query timeout
}
