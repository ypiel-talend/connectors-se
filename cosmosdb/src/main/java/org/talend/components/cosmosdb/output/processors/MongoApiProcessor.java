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

package org.talend.components.cosmosdb.output.processors;

import org.talend.components.mongodb.output.MongoDBOutput;
import org.talend.sdk.component.api.record.Record;

public class MongoApiProcessor implements ApiProcessor {

    private final MongoDBOutput output;

    public MongoApiProcessor(MongoDBOutput output) {
        this.output = output;
    }

    @Override
    public void init() {
        output.init();
    }

    @Override
    public void beforeGroup() {
        output.beforeGroup();
    }

    @Override
    public void process(Record record) {
        output.onNext(record);
    }

    @Override
    public void afterGroup() {
        output.afterGroup();
    }

    @Override
    public void close() {
        output.release();
    }
}
