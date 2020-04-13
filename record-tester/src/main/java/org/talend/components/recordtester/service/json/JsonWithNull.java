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
package org.talend.components.recordtester.service.json;

import org.talend.components.recordtester.conf.CodingConfig;
import org.talend.components.recordtester.conf.Config;
import org.talend.components.recordtester.service.AbstractProvider;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JsonWithNull extends AbstractProvider {

    @Override
    public List<Object> get(CodingConfig config) {
        final JsonObject jsonObject = this.loadJsonFile("json/jsonWithNull.json");
        List<Object> jsonObjects = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            JsonObjectBuilder builder = this.getJsonBuilderFactory().createObjectBuilder(jsonObject).add("aaa", "val_" + i);
            if (i % 3 == 0) {
                builder = builder.addNull("ccc");
            }

            jsonObjects.add(builder.build());
        }
        return jsonObjects;
    }

}
