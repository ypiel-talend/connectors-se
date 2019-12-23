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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("eventWebhooksWebhook_IDParameters"),
        @GridLayout.Row("webhookTriggersWebhook_Trigger_IDParameters") })
@Documentation("Outbound Webhooks")
public class OutboundWebhooksSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({})
    public static class EventWebhooks implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Override
        public String getServiceToCall() {
            return "common/v1/eventWebhooks";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("webhook_ID") })
    public static class EventWebhooksWebhook_ID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Webhook identifier. This can either be a Workday ID (WID) or a Reference ID in the form of Webhook_ID=WEBHOOK-15-5.")
        private String webhook_ID;

        @Override
        public String getServiceToCall() {
            return "common/v1/eventWebhooks/" + this.webhook_ID + "";
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
    public static class WebhookTriggers implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Override
        public String getServiceToCall() {
            return "common/v1/webhookTriggers";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("webhook_Trigger_ID") })
    public static class WebhookTriggersWebhook_Trigger_ID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Webhook Trigger identifier. This can either be a Workday ID (WID) or a Reference ID in the form of Webhook_Trigger_ID=WEBHOOK_EVENT_SERVICE_CONFIGURATION-15-1.")
        private String webhook_Trigger_ID;

        @Override
        public String getServiceToCall() {
            return "common/v1/webhookTriggers/" + this.webhook_Trigger_ID + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum OutboundWebhooksSwaggerServiceChoice {
        EventWebhooks,
        EventWebhooksWebhook_ID,
        WebhookTriggers,
        WebhookTriggersWebhook_Trigger_ID;
    }

    @Option
    @Documentation("selected service")
    private OutboundWebhooksSwaggerServiceChoice service = OutboundWebhooksSwaggerServiceChoice.EventWebhooks;

    private EventWebhooks eventWebhooksParameters = new EventWebhooks();

    @Option
    @ActiveIf(target = "service", value = "EventWebhooksWebhook_ID")
    @Documentation("parameters")
    private EventWebhooksWebhook_ID eventWebhooksWebhook_IDParameters = new EventWebhooksWebhook_ID();

    private WebhookTriggers webhookTriggersParameters = new WebhookTriggers();

    @Option
    @ActiveIf(target = "service", value = "WebhookTriggersWebhook_Trigger_ID")
    @Documentation("parameters")
    private WebhookTriggersWebhook_Trigger_ID webhookTriggersWebhook_Trigger_IDParameters = new WebhookTriggersWebhook_Trigger_ID();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == OutboundWebhooksSwaggerServiceChoice.EventWebhooks) {
            return this.eventWebhooksParameters;
        }
        if (this.service == OutboundWebhooksSwaggerServiceChoice.EventWebhooksWebhook_ID) {
            return this.eventWebhooksWebhook_IDParameters;
        }
        if (this.service == OutboundWebhooksSwaggerServiceChoice.WebhookTriggers) {
            return this.webhookTriggersParameters;
        }
        if (this.service == OutboundWebhooksSwaggerServiceChoice.WebhookTriggersWebhook_Trigger_ID) {
            return this.webhookTriggersWebhook_Trigger_IDParameters;
        }

        return null;
    }
}
