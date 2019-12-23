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
package org.talend.components.workday.dataset.service.input;

import java.io.Serializable;
import java.util.Map;
import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.components.workday.dataset.QueryHelper;

@Data
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("workerCustomDataSnapshotIdCustomObjectsCustomObjectaliasParameters") })
@Documentation("Common Services")
public class CommonServicesSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("customObjectAlias") })
    public static class WorkerCustomDataSnapshotIdCustomObjectsCustomObjectalias implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Workday ID of the custom data snapshot. This value is returned in the response of the events POST request in the customBusinessProcess service, by the field customObject.")
        private String id;

        @Option
        @Documentation("The web service alias of the custom object.")
        private String customObjectAlias;

        @Override
        public String getServiceToCall() {
            return "common/v1/workerCustomDataSnapshot/" + this.id + "/customObjects/" + this.customObjectAlias + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum CommonServicesSwaggerServiceChoice {
        WorkerCustomDataSnapshotIdCustomObjectsCustomObjectalias;
    }

    @Option
    @Documentation("selected service")
    private CommonServicesSwaggerServiceChoice service = CommonServicesSwaggerServiceChoice.WorkerCustomDataSnapshotIdCustomObjectsCustomObjectalias;

    @Option
    @ActiveIf(target = "service", value = "WorkerCustomDataSnapshotIdCustomObjectsCustomObjectalias")
    @Documentation("parameters")
    private WorkerCustomDataSnapshotIdCustomObjectsCustomObjectalias workerCustomDataSnapshotIdCustomObjectsCustomObjectaliasParameters = new WorkerCustomDataSnapshotIdCustomObjectsCustomObjectalias();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == CommonServicesSwaggerServiceChoice.WorkerCustomDataSnapshotIdCustomObjectsCustomObjectalias) {
            return this.workerCustomDataSnapshotIdCustomObjectsCustomObjectaliasParameters;
        }

        return null;
    }
}
