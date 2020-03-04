/*
 * Copyright (C) 2006-2020 Talend Inc. - www.talend.com
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
package org.talend.components.workday.dataset;

import lombok.Data;
import org.talend.components.workday.dataset.service.input.ModuleChoice;
import org.talend.components.workday.datastore.WorkdayDataStore;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

@Data
@DataSet("WorkdayServiceDataSet")
@GridLayout({ @GridLayout.Row("datastore"), @GridLayout.Row({ "module" }) })
@GridLayout(names = GridLayout.FormType.ADVANCED, value = { @GridLayout.Row("datastore") })
@Documentation("Dataset for workday service")
public class WorkdayServiceDataSet implements QueryHelper, Serializable {

    private static final long serialVersionUID = -9037128911796623682L;

    @Option
    @Documentation("The connection to workday datastore")
    private WorkdayDataStore datastore;

    @Option
    @Documentation("module choice")
    private ModuleChoice module = new ModuleChoice();

    @Override
    public String getServiceToCall() {
        return this.getForQueryHelper(QueryHelper::getServiceToCall, "");
    }

    @Override
    public Map<String, Object> extractQueryParam() {
        return this.getForQueryHelper(QueryHelper::extractQueryParam, Collections.emptyMap());
    }

    @Override
    public boolean isPaginable() {
        return this.getForQueryHelper(QueryHelper::isPaginable, false);
    }

    private <T> T getForQueryHelper(Function<QueryHelper, T> inside, T defaultValue) {
        final QueryHelper queryHelper = this.module.getQueryHelper();
        if (queryHelper == null) {
            return defaultValue;
        }
        return inside.apply(queryHelper);
    }
}
