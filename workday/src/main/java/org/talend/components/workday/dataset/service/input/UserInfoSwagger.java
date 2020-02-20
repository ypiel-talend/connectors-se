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
@GridLayout({ @GridLayout.Row("service") })
@Documentation("User Info")
public class UserInfoSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({})
    public static class UserInfo implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Override
        public String getServiceToCall() {
            return "common/v1/userInfo";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum UserInfoSwaggerServiceChoice {
        UserInfo;
    }

    @Option
    @Documentation("selected service")
    private UserInfoSwaggerServiceChoice service = UserInfoSwaggerServiceChoice.UserInfo;

    private UserInfo userInfoParameters = new UserInfo();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == UserInfoSwaggerServiceChoice.UserInfo) {
            return this.userInfoParameters;
        }

        return null;
    }
}
