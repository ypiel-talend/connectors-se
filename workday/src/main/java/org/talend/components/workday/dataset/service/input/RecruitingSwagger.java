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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("interviewsParameters"), @GridLayout.Row("interviewsIDParameters"),
        @GridLayout.Row("interviewsIDFeedbackParameters"), @GridLayout.Row("jobPostingsParameters"),
        @GridLayout.Row("jobPostingsIdParameters"), @GridLayout.Row("prospectsIdParameters"),
        @GridLayout.Row("prospectsIdEducationsParameters"), @GridLayout.Row("prospectsIdExperiencesParameters"),
        @GridLayout.Row("prospectsIdLanguagesParameters"), @GridLayout.Row("prospectsIdResumeAttachmentsParameters"),
        @GridLayout.Row("prospectsIdResumeAttachmentsTypeViewFileParameters"), @GridLayout.Row("prospectsIdSkillsParameters") })
@Documentation("Recruiting")
public class RecruitingSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("interviewStatus") })
    public static class Interviews implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("All applicable interview statuses for an Interview Event. Statuses can be: \n* AWAITING_ME - An in progress interview event is waiting for my feedback. \n* COMPLETED - An interview event has been completed.\n* FEEDBACK_COMPLETE - All feedback for this in progress interview event has been submitted.\n* NOT_SCHEDULED - An interview hasn't been scheduled for an in progress interview event.\n* PENDING_FEEDBACK - An in progress interview event is waiting for interviewer feedback.\n* SCHEDULED - An interview has been scheduled for an in progress interview event.\n* SUBMITTED_FEEDBACK - For an in progress interview event, I have submitted feedback.")
        private java.util.List<String> interviewStatus;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "recruiting/v1/interviews";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.interviewStatus != null) {
                queryParam.put("interviewStatus", this.interviewStatus);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("iD") })
    public static class InterviewsID implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Workday ID of the interview.")
        private String iD;

        @Override
        public String getServiceToCall() {
            return "recruiting/v1/interviews/" + this.iD + "";
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
    public static class InterviewsIDFeedback implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Workday ID of the interview feedback.")
        private String iD;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "recruiting/v1/interviews/" + this.iD + "/feedback";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("jobSite"), @GridLayout.Row("category") })
    public static class JobPostings implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The Job Posting Site for posting.")
        private java.util.List<String> jobSite;

        @Option
        @Documentation("The job family group for the job posting.")
        private java.util.List<String> category;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "recruiting/v1/jobPostings";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.jobSite != null) {
                queryParam.put("jobSite", this.jobSite);
            }
            if (this.category != null) {
                queryParam.put("category", this.category);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id") })
    public static class JobPostingsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String id;

        @Override
        public String getServiceToCall() {
            return "recruiting/v1/jobPostings/" + this.id + "";
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
    public static class ProspectsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String id;

        @Override
        public String getServiceToCall() {
            return "recruiting/v1/prospects/" + this.id + "";
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
    public static class ProspectsIdEducations implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String id;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "recruiting/v1/prospects/" + this.id + "/educations";
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
    public static class ProspectsIdExperiences implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String id;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "recruiting/v1/prospects/" + this.id + "/experiences";
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
    public static class ProspectsIdLanguages implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String id;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "recruiting/v1/prospects/" + this.id + "/languages";
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
    public static class ProspectsIdResumeAttachments implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String id;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "recruiting/v1/prospects/" + this.id + "/resumeAttachments";
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
    public static class ProspectsIdResumeAttachmentsTypeViewFile implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String id;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "recruiting/v1/prospects/" + this.id + "/resumeAttachments?type=viewFile";
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
    public static class ProspectsIdSkills implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("Id of specified instance")
        private String id;

        @Override
        public boolean isPaginable() {
            return true;
        }

        @Override
        public String getServiceToCall() {
            return "recruiting/v1/prospects/" + this.id + "/skills";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    public enum queryView {
        candidateLanguageSkillDetails
    }

    public enum RecruitingSwaggerServiceChoice {
        Interviews,
        InterviewsID,
        InterviewsIDFeedback,
        JobPostings,
        JobPostingsId,
        ProspectsId,
        ProspectsIdEducations,
        ProspectsIdExperiences,
        ProspectsIdLanguages,
        ProspectsIdResumeAttachments,
        ProspectsIdResumeAttachmentsTypeViewFile,
        ProspectsIdSkills;
    }

    @Option
    @Documentation("selected service")
    private RecruitingSwaggerServiceChoice service = RecruitingSwaggerServiceChoice.Interviews;

    @Option
    @ActiveIf(target = "service", value = "Interviews")
    @Documentation("parameters")
    private Interviews interviewsParameters = new Interviews();

    @Option
    @ActiveIf(target = "service", value = "InterviewsID")
    @Documentation("parameters")
    private InterviewsID interviewsIDParameters = new InterviewsID();

    @Option
    @ActiveIf(target = "service", value = "InterviewsIDFeedback")
    @Documentation("parameters")
    private InterviewsIDFeedback interviewsIDFeedbackParameters = new InterviewsIDFeedback();

    @Option
    @ActiveIf(target = "service", value = "JobPostings")
    @Documentation("parameters")
    private JobPostings jobPostingsParameters = new JobPostings();

    @Option
    @ActiveIf(target = "service", value = "JobPostingsId")
    @Documentation("parameters")
    private JobPostingsId jobPostingsIdParameters = new JobPostingsId();

    @Option
    @ActiveIf(target = "service", value = "ProspectsId")
    @Documentation("parameters")
    private ProspectsId prospectsIdParameters = new ProspectsId();

    @Option
    @ActiveIf(target = "service", value = "ProspectsIdEducations")
    @Documentation("parameters")
    private ProspectsIdEducations prospectsIdEducationsParameters = new ProspectsIdEducations();

    @Option
    @ActiveIf(target = "service", value = "ProspectsIdExperiences")
    @Documentation("parameters")
    private ProspectsIdExperiences prospectsIdExperiencesParameters = new ProspectsIdExperiences();

    @Option
    @ActiveIf(target = "service", value = "ProspectsIdLanguages")
    @Documentation("parameters")
    private ProspectsIdLanguages prospectsIdLanguagesParameters = new ProspectsIdLanguages();

    @Option
    @ActiveIf(target = "service", value = "ProspectsIdResumeAttachments")
    @Documentation("parameters")
    private ProspectsIdResumeAttachments prospectsIdResumeAttachmentsParameters = new ProspectsIdResumeAttachments();

    @Option
    @ActiveIf(target = "service", value = "ProspectsIdResumeAttachmentsTypeViewFile")
    @Documentation("parameters")
    private ProspectsIdResumeAttachmentsTypeViewFile prospectsIdResumeAttachmentsTypeViewFileParameters = new ProspectsIdResumeAttachmentsTypeViewFile();

    @Option
    @ActiveIf(target = "service", value = "ProspectsIdSkills")
    @Documentation("parameters")
    private ProspectsIdSkills prospectsIdSkillsParameters = new ProspectsIdSkills();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == RecruitingSwaggerServiceChoice.Interviews) {
            return this.interviewsParameters;
        }
        if (this.service == RecruitingSwaggerServiceChoice.InterviewsID) {
            return this.interviewsIDParameters;
        }
        if (this.service == RecruitingSwaggerServiceChoice.InterviewsIDFeedback) {
            return this.interviewsIDFeedbackParameters;
        }
        if (this.service == RecruitingSwaggerServiceChoice.JobPostings) {
            return this.jobPostingsParameters;
        }
        if (this.service == RecruitingSwaggerServiceChoice.JobPostingsId) {
            return this.jobPostingsIdParameters;
        }
        if (this.service == RecruitingSwaggerServiceChoice.ProspectsId) {
            return this.prospectsIdParameters;
        }
        if (this.service == RecruitingSwaggerServiceChoice.ProspectsIdEducations) {
            return this.prospectsIdEducationsParameters;
        }
        if (this.service == RecruitingSwaggerServiceChoice.ProspectsIdExperiences) {
            return this.prospectsIdExperiencesParameters;
        }
        if (this.service == RecruitingSwaggerServiceChoice.ProspectsIdLanguages) {
            return this.prospectsIdLanguagesParameters;
        }
        if (this.service == RecruitingSwaggerServiceChoice.ProspectsIdResumeAttachments) {
            return this.prospectsIdResumeAttachmentsParameters;
        }
        if (this.service == RecruitingSwaggerServiceChoice.ProspectsIdResumeAttachmentsTypeViewFile) {
            return this.prospectsIdResumeAttachmentsTypeViewFileParameters;
        }
        if (this.service == RecruitingSwaggerServiceChoice.ProspectsIdSkills) {
            return this.prospectsIdSkillsParameters;
        }

        return null;
    }
}
