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
package org.talend.components.workday.input;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.talend.components.workday.WorkdayBaseTest;
import org.talend.components.workday.dataset.WQLLayout;
import org.talend.components.workday.dataset.WorkdayDataSet;
import org.talend.components.workday.dataset.WorkdayDataSet.WorkdayMode;
import org.talend.components.workday.dataset.WorkdayServiceDataSet;
import org.talend.components.workday.dataset.service.input.ExpensesSwagger;
import org.talend.components.workday.dataset.service.input.HumanResourceManagementSwagger;
import org.talend.components.workday.dataset.service.input.ModuleChoice;
import org.talend.components.workday.dataset.service.input.UserInfoSwagger;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.junit.SimpleFactory;
import org.talend.sdk.component.junit.http.junit5.HttpApi;
import org.talend.sdk.component.junit5.ComponentExtension;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.manager.chain.Job;

@HttpApi(useSsl = true)
@ExtendWith(ComponentExtension.class)
@WithComponents("org.talend.components.workday")
class WorkdayServiceProducerTest extends WorkdayBaseTest {

    private WorkdayServiceDataSet dataset;

    private InputConfiguration cfg;

    @BeforeEach
    private void init() {
        this.cfg = new InputConfiguration();
        this.dataset = new WorkdayServiceDataSet();
        this.dataset.setDatastore(this.buildDataStore());

        this.cfg.setDataSet(dataset);
    }

    @Test
    public void producer() {
        TesTOutput.OBJECTS.clear();

        ModuleChoice mc = new ModuleChoice();
        mc.setModule(ModuleChoice.Modules.HumanResourceManagementSwagger);
        mc.getHumanResourceManagementSwagger()
                .setService(HumanResourceManagementSwagger.HumanResourceManagementSwaggerServiceChoice.Workers);
        this.dataset.setModule(mc);

        final String configStr = SimpleFactory.configurationByExample().forInstance(cfg).configured().toQueryString();
        System.setProperty("talend.beam.job.targetParallelism", "1"); // our code creates one hz lite instance per thread
        final Job.ExecutorBuilder jobBuilder = Job.components().component("source", "Workday://Service?" + configStr)
                .component("output", "WorkdayTest://collector").connections().from("source").to("output").build();

        jobBuilder.run();

        Assertions.assertFalse(TesTOutput.OBJECTS.isEmpty());
        Assertions.assertTrue(TesTOutput.OBJECTS.size() > 110); // plus d'une page

        JsonObject first = TesTOutput.OBJECTS.get(0);
        Assertions.assertNotNull(first);
    }

    @Test
    void producerWithoutPage() {

        TesTOutput.OBJECTS.clear();

        ModuleChoice mc = new ModuleChoice();
        mc.setModule(ModuleChoice.Modules.UserInfoSwagger);
        mc.getUserInfoSwagger().setService(UserInfoSwagger.UserInfoSwaggerServiceChoice.UserInfo);
        this.dataset.setModule(mc);
        InputConfiguration cfg = new InputConfiguration();
        cfg.setDataSet(this.dataset);

        final String configStr = SimpleFactory.configurationByExample().forInstance(cfg).configured().toQueryString();

        System.setProperty("talend.beam.job.targetParallelism", "1"); // our code creates one hz lite instance per thread
        final Job.ExecutorBuilder jobBuilder = Job.components().component("source", "Workday://Service?" + configStr)
                .component("output", "WorkdayTest://collector").connections().from("source").to("output").build();

        jobBuilder.run();

        Assertions.assertFalse(TesTOutput.OBJECTS.isEmpty());

        JsonObject first = TesTOutput.OBJECTS.get(0);
        Assertions.assertNotNull(first);
    }

    @Test
    public void expenses() {
        TesTOutput.OBJECTS.clear();

        ModuleChoice mc = new ModuleChoice();
        mc.setModule(ModuleChoice.Modules.ExpensesSwagger);

        mc.getExpensesSwagger().setService(ExpensesSwagger.ExpensesSwaggerServiceChoice.Entries);
        mc.getExpensesSwagger().getEntriesParameters().setExpenseEntryStatus("d5478541ee1b431ab34a28339b40caf5");
        this.dataset.setModule(mc);

        InputConfiguration cfg = new InputConfiguration();
        cfg.setDataSet(this.dataset);

        final String configStr = SimpleFactory.configurationByExample().forInstance(cfg).configured().toQueryString();
        System.setProperty("talend.beam.job.targetParallelism", "1"); // our code creates one hz lite instance per thread
        final Job.ExecutorBuilder jobBuilder = Job.components().component("source", "Workday://Service?" + configStr)
                .component("output", "WorkdayTest://collector").connections().from("source").to("output").build();

        jobBuilder.run();

        Assertions.assertFalse(TesTOutput.OBJECTS.isEmpty());

        JsonObject first = TesTOutput.OBJECTS.get(0);
        Assertions.assertNotNull(first);
    }

    @Processor(family = "WorkdayTest", name = "collector")
    public static class TesTOutput implements Serializable {

        private static final long serialVersionUID = -989062340811827429L;

        static final List<JsonObject> OBJECTS = new ArrayList<>();

        @ElementListener
        public void onNext(final JsonObject object) {
            synchronized (OBJECTS) {
                OBJECTS.add(object);
            }
        }
    }
}