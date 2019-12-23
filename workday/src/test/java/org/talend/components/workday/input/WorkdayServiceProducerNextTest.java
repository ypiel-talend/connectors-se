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
package org.talend.components.workday.input;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.talend.components.workday.dataset.WorkdayServiceDataSet;
import org.talend.components.workday.dataset.service.input.HumanResourceManagementSwagger;
import org.talend.components.workday.dataset.service.input.ModuleChoice;
import org.talend.components.workday.service.ConfigHelper;
import org.talend.components.workday.service.WorkdayReaderService;
import org.talend.sdk.component.junit.http.junit5.HttpApi;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.runtime.record.RecordBuilderFactoryImpl;

import javax.json.JsonObject;

@HttpApi(useSsl = true)
class WorkdayServiceProducerNextTest {

    private static WorkdayServiceDataSet dataset;

    private static InputConfiguration cfg;

    private static WorkdayReaderService service;

    @BeforeAll
    private static void init() throws NoSuchFieldException, IllegalAccessException {
        WorkdayServiceProducerNextTest.service = ConfigHelper.buildReader();

        WorkdayServiceProducerNextTest.cfg = new InputConfiguration();

        WorkdayServiceDataSet ds = new WorkdayServiceDataSet();

        ds.setDatastore(ConfigHelper.buildDataStore());
        ModuleChoice mc = new ModuleChoice();
        ds.setModule(mc);
        mc.setModule(ModuleChoice.Modules.HumanResourceManagementSwagger);
        mc.setHumanResourceManagementSwagger(new HumanResourceManagementSwagger());
        mc.getHumanResourceManagementSwagger()
                .setService(HumanResourceManagementSwagger.HumanResourceManagementSwaggerServiceChoice.Workers);
        // ds.setModule("human-resource-management-swagger.json");
        /*
         * ds.setService("common/v1/workers");
         * ds.setParameters(new WorkdayServiceDataSet.Parameters());
         * ds.getParameters().setPaginable(true);
         * 
         * RecordBuilderFactory factory = new RecordBuilderFactoryImpl("xx");
         * final Schema schemaWorker = factory.newSchemaBuilder(Schema.Type.RECORD)
         * .withEntry(factory.newEntryBuilder().withName("PONumber").withType(Schema.Type.STRING).build()).build();
         * ds.getParameters().setResponseSchema(schemaWorker);
         */

        WorkdayServiceProducerNextTest.dataset = ds;

        WorkdayServiceProducerNextTest.cfg.setDataSet(ds);
    }

    @Test
    void nextOK() {

        WorkdayServiceProducer producer = new WorkdayServiceProducer(WorkdayServiceProducerNextTest.cfg,
                WorkdayServiceProducerNextTest.service);

        JsonObject o = producer.next();
        Assertions.assertNotNull(o);
    }

}