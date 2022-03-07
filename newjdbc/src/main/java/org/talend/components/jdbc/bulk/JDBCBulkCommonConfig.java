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
package org.talend.components.jdbc.bulk;

import lombok.Data;
import org.talend.components.jdbc.dataset.JDBCTableDataSet;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@Data
@GridLayout({
        // TODO layout more here
})
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {
        // TODO layout more here
})
@Documentation("jdbc bulk common")
public class JDBCBulkCommonConfig implements Serializable {

    @Option
    @Documentation("")
    private String bulkFile;

    // advanced setting

    @Option
    @Documentation("")
    private String rowSeparator;

    @Option
    @Documentation("")
    private String fieldSeparator;

    // TODO set text enclosure field

    // TODO set null value field

}
