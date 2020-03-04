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

import javax.json.JsonObject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.talend.components.workday.WorkdayBaseTest;
import org.talend.components.workday.dataset.WorkdayServiceDataSet;
import org.talend.components.workday.dataset.service.input.HumanResourceManagementSwagger;
import org.talend.components.workday.dataset.service.input.HumanResourceManagementSwagger.HumanResourceManagementSwaggerServiceChoice;
import org.talend.components.workday.dataset.service.input.HumanResourceManagementSwagger.WorkersID_PATH_PARAMETER;
import org.talend.components.workday.dataset.service.input.ModuleChoice;
import org.talend.components.workday.dataset.service.input.UserInfoSwagger.UserInfoSwaggerServiceChoice;
import org.talend.components.workday.service.WorkdayReaderService;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.junit.http.junit5.HttpApi;
import org.talend.sdk.component.junit5.WithComponents;

@HttpApi(useSsl = true)
@WithComponents("org.talend.components.workday")
class WorkdayServiceProducerNextTest extends WorkdayBaseTest {

    private WorkdayServiceDataSet dataset;

    private InputConfiguration cfg;

    @Service
    private WorkdayReaderService service;

    @BeforeEach
    void init() {
        this.cfg = new InputConfiguration();

        WorkdayServiceDataSet ds = new WorkdayServiceDataSet();
        ds.setDatastore(this.buildDataStore());
        ModuleChoice mc = new ModuleChoice();
        ds.setModule(mc);
        mc.setModule(ModuleChoice.Modules.HumanResourceManagementSwagger);
        mc.setHumanResourceManagementSwagger(new HumanResourceManagementSwagger());
        mc.getHumanResourceManagementSwagger()
                .setService(HumanResourceManagementSwagger.HumanResourceManagementSwaggerServiceChoice.Workers);

        this.dataset = ds;

        cfg.setDataSet(ds);
    }

    @Test
    void nextOK() {

        WorkdayServiceProducer producer = new WorkdayServiceProducer(this.cfg, this.service);

        JsonObject o = producer.next();
        Assertions.assertNotNull(o);
        int counter = 0;
        while (o != null) {
            counter++;
            o = producer.next();
        }
        Assertions.assertEquals(466, counter); // from
                                               // org.talend.components.workday.input.WorkdayServiceProducerNextTest_nextOK.json
                                               // file.
    }

    @Test
    void oneWorker() {
        this.dataset.getModule().getHumanResourceManagementSwagger()
                .setService(HumanResourceManagementSwagger.HumanResourceManagementSwaggerServiceChoice.WorkersID_PATH_PARAMETER);
        final WorkersID_PATH_PARAMETER workerId = new WorkersID_PATH_PARAMETER();
        workerId.setID_PATH_PARAMETER("6dcb8106e8b74b5aabb1fc3ab8ef2b92");
        this.dataset.getModule().getHumanResourceManagementSwagger().setWorkersID_PATH_PARAMETERParameters(workerId);

        WorkdayServiceProducer producer = new WorkdayServiceProducer(this.cfg, this.service);

        JsonObject o = producer.next();
        Assertions.assertNotNull(o);

        o = producer.next();
        Assertions.assertNull(o);
    }

    @Test
    void userNoPage() {
        this.dataset.getModule().setModule(ModuleChoice.Modules.UserInfoSwagger);
        this.dataset.getModule().getUserInfoSwagger().setService(UserInfoSwaggerServiceChoice.UserInfo);

        WorkdayServiceProducer producer = new WorkdayServiceProducer(this.cfg, this.service);
        JsonObject o = producer.next();
        Assertions.assertNotNull(o);

        JsonObject o1 = producer.next();
        Assertions.assertNull(o1);
    }

}