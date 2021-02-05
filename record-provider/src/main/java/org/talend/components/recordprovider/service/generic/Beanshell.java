/*
 * Copyright (C) 2006-2021 Talend Inc. - www.talend.com
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
package org.talend.components.recordprovider.service.generic;

import bsh.EvalError;
import org.talend.components.recordprovider.conf.CodingConfig;
import org.talend.components.recordprovider.service.AbstractProvider;

import bsh.Interpreter;

import java.util.Collections;
import java.util.List;

public class Beanshell extends AbstractProvider {

    @Override
    public List<Object> get(CodingConfig config) {
        Interpreter i = new Interpreter();
        try {
            final StringBuilder exec = new StringBuilder();
            this.getServices().keySet().stream().forEach(k -> exec.append("import ").append(k.getCanonicalName()).append(";\n"));
            exec.append("import org.talend.sdk.component.api.record.Schema;\n").append("import javax.json.JsonObject;\n")
                    .append("import javax.json.JsonObjectBuilder;").append("import javax.json.Json;\n")
                    .append("import javax.json.JsonArray;\n").append("import javax.json.JsonNumber;\n")
                    .append("import javax.json.JsonObject;\n").append("import javax.json.JsonObjectBuilder;\n")
                    .append("import javax.json.JsonString;\n").append("import javax.json.JsonValue;\n")
                    .append("import javax.json.JsonValue.ValueType;\n")
                    .append("import org.talend.sdk.component.api.record.Record;\n")
                    .append("import org.talend.sdk.component.api.record.Schema;\n")
                    .append("import org.talend.sdk.component.api.record.Schema.Entry;\n")
                    .append("import org.talend.sdk.component.api.record.Schema.Type;\n")
                    .append("List records = new ArrayList();\n");

            i.set("provider", this);

            final Object eval = i.eval(exec.append(config.getBeanShellCode()).toString());
            List<Object> records = (List<Object>) i.get("records");
            return records;
        } catch (EvalError evalError) {
            evalError.printStackTrace();
        }

        return Collections.emptyList();
    }
}
