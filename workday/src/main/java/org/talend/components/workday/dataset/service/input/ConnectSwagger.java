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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("messageTemplatesParameters"),
        @GridLayout.Row("messageTemplatesIDParameters"), @GridLayout.Row("notificationTypesIDParameters") })
@Documentation("Connect")
public class ConnectSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("name"), @GridLayout.Row("notificationType"), @GridLayout.Row("inactive") })
    public static class MessageTemplates implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Message Template Name")
        private String name;

        @Option
        @Documentation("Notification Type indicates where the template can be used.")
        private String notificationType;

        @Option
        @Documentation("Indicates template is active or has been disabled.")
        private Boolean inactive = Boolean.TRUE;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "connect/v1/messageTemplates";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.name != null) {
                queryParam.put("name", this.name);
            }
            if (this.notificationType != null) {
                queryParam.put("notificationType", this.notificationType);
            }
            if (this.inactive != null) {
                queryParam.put("inactive", this.inactive);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD") })
    public static class MessageTemplatesID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD;

        @Override
        public String getServiceToCall() {
            return "connect/v1/messageTemplates/" + this.iD + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({})
    public static class NotificationTypes implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "connect/v1/notificationTypes";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD") })
    public static class NotificationTypesID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD;

        @Override
        public String getServiceToCall() {
            return "connect/v1/notificationTypes/" + this.iD + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum queryView {
        messageTemplateDetail
    }

    public enum ConnectSwaggerServiceChoice {
        MessageTemplates,
        MessageTemplatesID,
        NotificationTypes,
        NotificationTypesID;
    }

    @Option
    @Documentation("selected service")
    private ConnectSwaggerServiceChoice service = ConnectSwaggerServiceChoice.MessageTemplates;

    @Option
    @ActiveIf(target = "service", value = "MessageTemplates")
    @Documentation("parameters")
    private MessageTemplates messageTemplatesParameters = new MessageTemplates();

    @Option
    @ActiveIf(target = "service", value = "MessageTemplatesID")
    @Documentation("parameters")
    private MessageTemplatesID messageTemplatesIDParameters = new MessageTemplatesID();

    private NotificationTypes notificationTypesParameters = new NotificationTypes();

    @Option
    @ActiveIf(target = "service", value = "NotificationTypesID")
    @Documentation("parameters")
    private NotificationTypesID notificationTypesIDParameters = new NotificationTypesID();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == ConnectSwaggerServiceChoice.MessageTemplates) {
            return this.messageTemplatesParameters;
        }
        if (this.service == ConnectSwaggerServiceChoice.MessageTemplatesID) {
            return this.messageTemplatesIDParameters;
        }
        if (this.service == ConnectSwaggerServiceChoice.NotificationTypes) {
            return this.notificationTypesParameters;
        }
        if (this.service == ConnectSwaggerServiceChoice.NotificationTypesID) {
            return this.notificationTypesIDParameters;
        }

        return null;
    }
}
