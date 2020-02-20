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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("customTasksIdParameters") })
@Documentation("Custom Tasks")
public class CustomTasksSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({})
    public static class CustomTasks implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "customTask/v1/customTasks";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id") })
    public static class CustomTasksId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String id;

        @Override
        public String getServiceToCall() {
            return "customTask/v1/customTasks/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum queryView {
        defaultCustomTaskRepresentation
    }

    public enum CustomTasksSwaggerServiceChoice {
        CustomTasks,
        CustomTasksId;
    }

    @Option
    @Documentation("selected service")
    private CustomTasksSwaggerServiceChoice service = CustomTasksSwaggerServiceChoice.CustomTasks;

    private CustomTasks customTasksParameters = new CustomTasks();

    @Option
    @ActiveIf(target = "service", value = "CustomTasksId")
    @Documentation("parameters")
    private CustomTasksId customTasksIdParameters = new CustomTasksId();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == CustomTasksSwaggerServiceChoice.CustomTasks) {
            return this.customTasksParameters;
        }
        if (this.service == CustomTasksSwaggerServiceChoice.CustomTasksId) {
            return this.customTasksIdParameters;
        }

        return null;
    }
}
