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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("mentorshipsParameters"), @GridLayout.Row("mentorshipsIDParameters") })
@Documentation("Talent Management")
public class TalentManagementSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("mentee"), @GridLayout.Row("closed"), @GridLayout.Row("mentor"),
            @GridLayout.Row("closeMentorshipReason"), @GridLayout.Row("mentorType"), @GridLayout.Row("inProgress") })
    public static class Mentorships implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Filter the mentorship by using the WID of Mentee.")
        private java.util.List<String> mentee;

        @Option
        @Documentation("Get only the closed mentorship if set to true.")
        private Boolean closed = Boolean.TRUE;

        @Option
        @Documentation("Filter the mentorship by using the WID of Mentor.")
        private java.util.List<String> mentor;

        @Option
        @Documentation("Filter the mentorship using the WID of close mentorship reason.")
        private java.util.List<String> closeMentorshipReason;

        @Option
        @Documentation("Filter the mentorship by using the WID of Mentor Type.")
        private java.util.List<String> mentorType;

        @Option
        @Documentation("Get only the in progress mentorship if set to true.")
        private Boolean inProgress = Boolean.TRUE;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "talentManagement/v1/mentorships";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.mentee != null) {
                queryParam.put("mentee", this.mentee);
            }
            if (this.closed != null) {
                queryParam.put("closed", this.closed);
            }
            if (this.mentor != null) {
                queryParam.put("mentor", this.mentor);
            }
            if (this.closeMentorshipReason != null) {
                queryParam.put("closeMentorshipReason", this.closeMentorshipReason);
            }
            if (this.mentorType != null) {
                queryParam.put("mentorType", this.mentorType);
            }
            if (this.inProgress != null) {
                queryParam.put("inProgress", this.inProgress);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD") })
    public static class MentorshipsID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String iD;

        @Override
        public String getServiceToCall() {
            return "talentManagement/v1/mentorships/" + this.iD + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum queryView {
        mentorshipDetail,
        referenceIndexData
    }

    public enum TalentManagementSwaggerServiceChoice {
        Mentorships,
        MentorshipsID;
    }

    @Option
    @Documentation("selected service")
    private TalentManagementSwaggerServiceChoice service = TalentManagementSwaggerServiceChoice.Mentorships;

    @Option
    @ActiveIf(target = "service", value = "Mentorships")
    @Documentation("parameters")
    private Mentorships mentorshipsParameters = new Mentorships();

    @Option
    @ActiveIf(target = "service", value = "MentorshipsID")
    @Documentation("parameters")
    private MentorshipsID mentorshipsIDParameters = new MentorshipsID();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == TalentManagementSwaggerServiceChoice.Mentorships) {
            return this.mentorshipsParameters;
        }
        if (this.service == TalentManagementSwaggerServiceChoice.MentorshipsID) {
            return this.mentorshipsIDParameters;
        }

        return null;
    }
}
