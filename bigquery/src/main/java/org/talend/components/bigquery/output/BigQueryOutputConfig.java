/*
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
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
package org.talend.components.bigquery.output;

import lombok.Data;
import org.talend.components.bigquery.dataset.TableDataSet;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.OptionsOrder;
import org.talend.sdk.component.api.configuration.ui.widget.Code;
import org.talend.sdk.component.api.configuration.ui.widget.TextArea;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

import static org.talend.sdk.component.api.component.Icon.IconType.BIGQUERY;

@Data
@Icon(BIGQUERY)
@Documentation("Dataset of a BigQuery component.")
@OptionsOrder({ "dataSet", "tableOperation", "tableSchemaFields", "writeOperation" })
public class BigQueryOutputConfig implements Serializable {

    @Option
    @Documentation("BigQuery Dataset")
    private TableDataSet dataSet;

    @Option
    @Documentation("The BigQuery table operation")
    @DefaultValue("NONE")
    private TableOperation tableOperation;

    @Option
    @Documentation("The BigQuery write operation")
    @DefaultValue("APPEND")
    private WriteOperation writeOperation;

    @Option
    @Documentation("Specify table schema for creation table")
    @ActiveIf(target = "tableOperation", value = "CREATE_IF_NOT_EXISTS")
    @TextArea
    @Code("json")
    private String tableSchemaFields;

    public enum TableOperation {
        /**
         * Specifics that tables should not be created.
         *
         * <p>
         * If the output table does not exist, the write fails.
         */
        NONE,
        /**
         * Specifies that tables should be created if needed. This is the default behavior.
         *
         * <p>
         * Requires that a table schema is provided via {@link org.talend.components.bigquery.BigQueryDatasetProperties#main}.
         * This precondition is
         * checked before starting a job. The schema is not required to match an existing table's schema.
         *
         * <p>
         * When this transformation is executed, if the output table does not exist, the table is created from the
         * provided schema.
         */
        CREATE_IF_NOT_EXISTS,

        /*
         * Currently not supported by Streams.
         * 
         * /**
         * Specifies that tables should be dropped if exists, and create by the provided schema, which actually the
         * combine with TRUNCATE and CREATE_IF_NOT_EXISTS
         *
         * DROP_IF_EXISTS_AND_CREATE,
         * /**
         * Specifies that write should replace a table.
         *
         * <p>
         * The replacement may occur in multiple steps - for instance by first removing the existing table, then
         * creating a replacement, then filling it in. This is not an atomic operation, and external programs may see
         * the table in any of these intermediate steps.
         * 
         * TRUNCATE,
         */
    }

    public enum WriteOperation {
        /**
         * Specifies that rows may be appended to an existing table.
         */
        APPEND,
        /**
         * Specifies that the output table must be empty. This is the default behavior.
         *
         * <p>
         * If the output table is not empty, the write fails at runtime.
         *
         * <p>
         * This check may occur long before data is written, and does not guarantee exclusive access to the table. If
         * two programs are run concurrently, each specifying the same output table and a
         * {@link BigQueryOutputProperties.WriteOperation} of
         * {@link BigQueryOutputProperties.WriteOperation#WRITE_TO_EMPTY}, it is possible for both to succeed.
         */
        WRITE_TO_EMPTY
    }
}
