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
@GridLayout({ @GridLayout.Row("service"), @GridLayout.Row("courseSectionsParameters"),
        @GridLayout.Row("courseSectionsIdParameters"), @GridLayout.Row("courseSubjectsIdParameters"),
        @GridLayout.Row("coursesParameters"), @GridLayout.Row("coursesIdParameters"),
        @GridLayout.Row("programsOfStudyIdParameters"), @GridLayout.Row("studentsParameters"),
        @GridLayout.Row("studentsIdParameters"), @GridLayout.Row("studentsIdChargesChargeIdParameters"),
        @GridLayout.Row("studentsIdCourseRegistrationsParameters") })
@Documentation("Student")
public class StudentSwagger implements Serializable, QueryHelper.QueryHelperProvider {

    private static final long serialVersionUID = 1L;

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("academicPeriod"), @GridLayout.Row("academicLevel"), @GridLayout.Row("subject"),
            @GridLayout.Row("course") })
    public static class CourseSections implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The WID of the Academic Period where the Course Section starts in.")
        private String academicPeriod;

        @Option
        @Documentation("The WID of the Academic Level for the Course Section.")
        private String academicLevel;

        @Option
        @Documentation("The WID of the Course Subject for the Course Section.")
        private String subject;

        @Option
        @Documentation("The WID of the Course for the Course Section.")
        private String course;

        @Override
        public String getServiceToCall() {
            return "student/v1Alpha/courseSections";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.academicPeriod != null) {
                queryParam.put("academicPeriod", this.academicPeriod);
            }
            if (this.academicLevel != null) {
                queryParam.put("academicLevel", this.academicLevel);
            }
            if (this.subject != null) {
                queryParam.put("subject", this.subject);
            }
            if (this.course != null) {
                queryParam.put("course", this.course);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id") })
    public static class CourseSectionsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The WID of the Course Section")
        private String id;

        @Override
        public String getServiceToCall() {
            return "student/v1Alpha/courseSections/" + this.id + "";
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
    public static class CourseSubjects implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Override
        public String getServiceToCall() {
            return "student/v1Alpha/courseSubjects";
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
    public static class CourseSubjectsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The WID of the Course Subject")
        private String id;

        @Override
        public String getServiceToCall() {
            return "student/v1Alpha/courseSubjects/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("subject") })
    public static class Courses implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The WID of the Course Subject")
        private String subject;

        @Override
        public String getServiceToCall() {
            return "student/v1Alpha/courses";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.subject != null) {
                queryParam.put("subject", this.subject);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id") })
    public static class CoursesId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The WID of the Course")
        private String id;

        @Override
        public String getServiceToCall() {
            return "student/v1Alpha/courses/" + this.id + "";
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
    public static class ProgramsOfStudy implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Override
        public String getServiceToCall() {
            return "student/v1Alpha/programsOfStudy";
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
    public static class ProgramsOfStudyId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The WID of the Program of Study")
        private String id;

        @Override
        public String getServiceToCall() {
            return "student/v1Alpha/programsOfStudy/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("academicUnit"), @GridLayout.Row("academicLevel"), @GridLayout.Row("programOfStudy") })
    public static class Students implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The WID of the student's primary Academic Unit")
        private String academicUnit;

        @Option
        @Documentation("The WID of the student's primary Academic Level")
        private String academicLevel;

        @Option
        @Documentation("The WID of the student's primary Program of Study")
        private String programOfStudy;

        @Override
        public String getServiceToCall() {
            return "student/v1Alpha/students";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.academicUnit != null) {
                queryParam.put("academicUnit", this.academicUnit);
            }
            if (this.academicLevel != null) {
                queryParam.put("academicLevel", this.academicLevel);
            }
            if (this.programOfStudy != null) {
                queryParam.put("programOfStudy", this.programOfStudy);
            }
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({})
    public static class StudentsMe implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Override
        public String getServiceToCall() {
            return "student/v1Alpha/students/me";
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
    public static class StudentsId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The WID of the Student")
        private String id;

        @Override
        public String getServiceToCall() {
            return "student/v1Alpha/students/" + this.id + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("chargeId") })
    public static class StudentsIdChargesChargeId implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The WID of the Student")
        private String id;

        @Option
        @Documentation("The WID of the Charge")
        private String chargeId;

        @Override
        public String getServiceToCall() {
            return "student/v1Alpha/students/" + this.id + "/charges/" + this.chargeId + "";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            return queryParam;
        }
    }

    @Data
    @Documentation("unkonwn doc in workday")
    @GridLayout({ @GridLayout.Row("id"), @GridLayout.Row("academicPeriod") })
    public static class StudentsIdCourseRegistrations implements Serializable, QueryHelper {

        private static final long serialVersionUID = 1L;

        @Option
        @Documentation("The WID of the Student")
        private String id;

        @Option
        @Documentation("The WID of an academic period to filter by")
        private String academicPeriod;

        @Override
        public String getServiceToCall() {
            return "student/v1Alpha/students/" + this.id + "/courseRegistrations";
        }

        @Override
        public Map<String, Object> extractQueryParam() {
            final Map<String, Object> queryParam = new java.util.HashMap<>();
            if (this.academicPeriod != null) {
                queryParam.put("academicPeriod", this.academicPeriod);
            }
            return queryParam;
        }
    }

    public enum StudentSwaggerServiceChoice {
        CourseSections,
        CourseSectionsId,
        CourseSubjects,
        CourseSubjectsId,
        Courses,
        CoursesId,
        ProgramsOfStudy,
        ProgramsOfStudyId,
        Students,
        StudentsId,
        StudentsIdChargesChargeId,
        StudentsIdCourseRegistrations,
        StudentsMe;
    }

    @Option
    @Documentation("selected service")
    private StudentSwaggerServiceChoice service = StudentSwaggerServiceChoice.CourseSections;

    @Option
    @ActiveIf(target = "service", value = "CourseSections")
    @Documentation("parameters")
    private CourseSections courseSectionsParameters = new CourseSections();

    @Option
    @ActiveIf(target = "service", value = "CourseSectionsId")
    @Documentation("parameters")
    private CourseSectionsId courseSectionsIdParameters = new CourseSectionsId();

    private CourseSubjects courseSubjectsParameters = new CourseSubjects();

    @Option
    @ActiveIf(target = "service", value = "CourseSubjectsId")
    @Documentation("parameters")
    private CourseSubjectsId courseSubjectsIdParameters = new CourseSubjectsId();

    @Option
    @ActiveIf(target = "service", value = "Courses")
    @Documentation("parameters")
    private Courses coursesParameters = new Courses();

    @Option
    @ActiveIf(target = "service", value = "CoursesId")
    @Documentation("parameters")
    private CoursesId coursesIdParameters = new CoursesId();

    private ProgramsOfStudy programsOfStudyParameters = new ProgramsOfStudy();

    @Option
    @ActiveIf(target = "service", value = "ProgramsOfStudyId")
    @Documentation("parameters")
    private ProgramsOfStudyId programsOfStudyIdParameters = new ProgramsOfStudyId();

    @Option
    @ActiveIf(target = "service", value = "Students")
    @Documentation("parameters")
    private Students studentsParameters = new Students();

    @Option
    @ActiveIf(target = "service", value = "StudentsId")
    @Documentation("parameters")
    private StudentsId studentsIdParameters = new StudentsId();

    @Option
    @ActiveIf(target = "service", value = "StudentsIdChargesChargeId")
    @Documentation("parameters")
    private StudentsIdChargesChargeId studentsIdChargesChargeIdParameters = new StudentsIdChargesChargeId();

    @Option
    @ActiveIf(target = "service", value = "StudentsIdCourseRegistrations")
    @Documentation("parameters")
    private StudentsIdCourseRegistrations studentsIdCourseRegistrationsParameters = new StudentsIdCourseRegistrations();

    private StudentsMe studentsMeParameters = new StudentsMe();

    @Override
    public QueryHelper getQueryHelper() {
        if (this.service == StudentSwaggerServiceChoice.CourseSections) {
            return this.courseSectionsParameters;
        }
        if (this.service == StudentSwaggerServiceChoice.CourseSectionsId) {
            return this.courseSectionsIdParameters;
        }
        if (this.service == StudentSwaggerServiceChoice.CourseSubjects) {
            return this.courseSubjectsParameters;
        }
        if (this.service == StudentSwaggerServiceChoice.CourseSubjectsId) {
            return this.courseSubjectsIdParameters;
        }
        if (this.service == StudentSwaggerServiceChoice.Courses) {
            return this.coursesParameters;
        }
        if (this.service == StudentSwaggerServiceChoice.CoursesId) {
            return this.coursesIdParameters;
        }
        if (this.service == StudentSwaggerServiceChoice.ProgramsOfStudy) {
            return this.programsOfStudyParameters;
        }
        if (this.service == StudentSwaggerServiceChoice.ProgramsOfStudyId) {
            return this.programsOfStudyIdParameters;
        }
        if (this.service == StudentSwaggerServiceChoice.Students) {
            return this.studentsParameters;
        }
        if (this.service == StudentSwaggerServiceChoice.StudentsId) {
            return this.studentsIdParameters;
        }
        if (this.service == StudentSwaggerServiceChoice.StudentsIdChargesChargeId) {
            return this.studentsIdChargesChargeIdParameters;
        }
        if (this.service == StudentSwaggerServiceChoice.StudentsIdCourseRegistrations) {
            return this.studentsIdCourseRegistrationsParameters;
        }
        if (this.service == StudentSwaggerServiceChoice.StudentsMe) {
            return this.studentsMeParameters;
        }

        return null;
    }
}
