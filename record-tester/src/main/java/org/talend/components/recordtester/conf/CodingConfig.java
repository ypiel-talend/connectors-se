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
package org.talend.components.recordtester.conf;

import lombok.Data;
import org.talend.components.recordtester.service.generic.Beanshell;
import org.talend.components.recordtester.service.generic.Json;
import org.talend.components.recordtester.service.json.JsonWithArrayWithNull;
import org.talend.components.recordtester.service.json.JsonWithNull;
import org.talend.components.recordtester.service.record.Empty;
import org.talend.components.recordtester.service.record.SchemaWithANull;
import org.talend.components.recordtester.service.record.SchemaWithMissing;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Code;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@Data
@GridLayout({ @GridLayout.Row("provider"), @GridLayout.Row("beanShellCode"), @GridLayout.Row("jsonPointer"),
        @GridLayout.Row("commonio"), @GridLayout.Row("json") })
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {})
public class CodingConfig implements Serializable {

    public enum RECORD_TYPE {
        EMPTY(Empty.class),
        FIXED_SCHEMA_WITH_A_NULL_VALUE(SchemaWithANull.class),
        FIXED_SCHEMA_WITH_A_MISSING_VALUE(SchemaWithMissing.class),
        JSON_WITH_NULL(JsonWithNull.class),
        JSON_WITH_ARRAY_WITH_NULL(JsonWithArrayWithNull.class),
        BEANSHELL(Beanshell.class),
        JSON(Json.class);
        ;

        Class clazz;

        RECORD_TYPE(Class name) {
            this.clazz = name;
        }

        public Class getClazz() {
            return clazz;
        }
    }

    @Option
    @Documentation("")
    RECORD_TYPE provider = RECORD_TYPE.EMPTY;

    @Option
    @Code("java")
    @ActiveIf(target = "provider", value = "BEANSHELL")
    @Documentation("")
    private String beanShellCode = "// import what.you.Want;\n" + "for(int i = 1; i <= 10; i++){\n"
            + " record = provider.getRecordBuilderFactory()\n" + " .newRecordBuilder()\n" + " .withString(\"foo\", \"bar_\"+i);\n"
            + " records.add(record.build());\n" + "}" + "// records can contain Record, JsonObject or Pojo";

    @Option
    @Code("json")
    @ActiveIf(target = "provider", value = "JSON")
    @Documentation("")
    private String json = "{\n" + "  \"aaa\" : \"aaaaa\",\n" + "  \"bbb\" : 12.5,\n" + "  \"ccc\" : true,\n" + "  \"ddd\" : {\n"
            + "    \"eee\" : \"eeee\"\n" + "  },\n" + "  \"fff\" : [\n" + "    {\"ggg\" : \"ggg1\", \"hhh\" : \"hhh1\"},\n"
            + "    {\"ggg\" : \"ggg2\", \"hhh\" : \"hhh2\"}\n" + "  ]\n" + "}";

    @Option
    @ActiveIf(target = "provider", value = "JSON")
    @Documentation("")
    private String jsonPointer = "/";

    @Option
    @ActiveIf(target = "provider", value = "JSON")
    @Documentation("Use jsonToRecord from common-stream-io or TCK RecordConverters")
    private boolean commonio = false;

}
