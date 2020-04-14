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
package org.talend.components.recordtester.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.talend.components.recordtester.conf.CodingConfig;
import org.talend.components.recordtester.conf.Config;
import org.talend.components.recordtester.conf.Dataset;
import org.talend.components.recordtester.conf.Feedback;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.api.service.update.Update;

import javax.json.JsonBuilderFactory;
import javax.json.JsonReaderFactory;
import javax.json.spi.JsonProvider;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Data
public class GenericService {

    @Service
    private JsonReaderFactory jsonReaderFactory;

    @Service
    private RecordBuilderFactory recordBuilderFactory;

    @Service
    private JsonBuilderFactory jsonBuilderFactory;

    @Service
    private JsonProvider jsonProvider;

    public List<Object> get(final Config globalConfig) {

        CodingConfig config = globalConfig.getDataset().getDsCodingConfig();
        if (Optional.ofNullable(globalConfig.isOverwriteDataset()).orElse(false)) {
            config = globalConfig.getCodingConfig();
        }

        return this.get(config);
    }

    public List<Object> get(final CodingConfig config) {
        final CodingConfig.RECORD_TYPE type = Optional.ofNullable(config.getProvider()).orElse(CodingConfig.RECORD_TYPE.EMPTY);
        RecordProvider provider = null;
        try {
            final Class<?> clazz = type.getClazz();
            provider = (RecordProvider) clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Can't load record provider " + type, e);
        }
        provider.setServices(this.getServices());
        return provider.get(config);
    }

    private Map<Class, Object> getServices() {
        Map<Class, Object> services = new HashMap<>();

        services.put(JsonReaderFactory.class, jsonReaderFactory);
        services.put(RecordBuilderFactory.class, recordBuilderFactory);
        services.put(JsonBuilderFactory.class, jsonBuilderFactory);
        services.put(JsonProvider.class, jsonProvider);

        return services;
    }

    @Update("COPY")
    public CodingConfig update(@Option("dataset") final Dataset dataset) throws Exception {
        return dataset.getDsCodingConfig();
    }

    @Update("FEEDBACK_DS")
    public Feedback updateds(@Option("dsCodingConfig") final CodingConfig codingConfig) throws Exception {
        final List<Object> records = this.get(codingConfig);

        StringBuilder sb = new StringBuilder();
        records.stream().forEach(r -> sb.append(r.toString() + "\n"));

        final Feedback feedback = new Feedback();
        ;
        feedback.setFeedback(sb.toString());

        return feedback;
    }

    @Update("FEEDBACK")
    public Feedback update(@Option("dataset") final Dataset dataset, @Option("overwriteDataset") final boolean overwriteDataset,
            @Option("codingConfig") final CodingConfig codingConfig) throws Exception {

        final Feedback feedback = this.updateds(overwriteDataset ? codingConfig : dataset.getDsCodingConfig());
        return feedback;
    }

}
