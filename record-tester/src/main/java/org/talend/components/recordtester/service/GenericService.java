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
import org.talend.components.recordtester.conf.Datastore;
import org.talend.components.recordtester.conf.Feedback;
import org.talend.components.recordtester.conf.FileContent;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Updatable;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Suggestions;
import org.talend.sdk.component.api.service.http.Response;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.api.service.update.Update;

import javax.json.JsonBuilderFactory;
import javax.json.JsonReaderFactory;
import javax.json.spi.JsonProvider;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Service
    Client httpClient;

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
    // public CodingConfig update(@Option("dataset") final Dataset dataset, @Option("filea") final String filea,
    public CodingConfig update(final String filea, final boolean justLoadFile) throws Exception {
        if (justLoadFile) {
            return this.loadFile(filea);
        }
        return new CodingConfig(); // dataset.getDsCodingConfig();
    }

    @Update("FEEDBACK_DS")
    public Feedback updateds(final CodingConfig dsCodingConfig) throws Exception {

        final List<Object> records = this.get(dsCodingConfig);

        StringBuilder sb = new StringBuilder();
        records.stream().forEach(r -> sb.append(r.toString() + "\n"));

        final Feedback feedback = new Feedback();
        feedback.setFeedback(sb.toString());

        return feedback;
    }

    @Update("FEEDBACK")
    public Feedback update(final Dataset dataset, final boolean overwriteDataset, final CodingConfig codingConfig)
            throws Exception {

        final Feedback feedback = this.updateds(overwriteDataset ? codingConfig : dataset.getDsCodingConfig());
        return feedback;
    }

    @Update("LOAD_FILE")
    public CodingConfig loadFile(final String file) throws Exception {
        CodingConfig cc = new CodingConfig();
        if (file.startsWith("http")) {
            final Response<byte[]> response = httpClient.execute("GET", file, Collections.emptyMap(), Collections.emptyMap());
            final StringBuilder body = new StringBuilder();
            final String content = new String(Optional.ofNullable(response.body()).orElse(new byte[0]));
            if (response.status() >= 300) {
                body.append("Retrieved content status code : ").append(response.status()).append("\n");
            }
            body.append(content);

            cc.setProvider(CodingConfig.RECORD_TYPE.JSON);
            cc.setJson(body.toString());
        } else {
            final InputStream stream = GenericService.class.getResourceAsStream(file);
            final String s = loadStream(stream);

            if (file.endsWith(".json")) {
                cc.setProvider(CodingConfig.RECORD_TYPE.JSON);
                cc.setJson(s);
            } else if (file.endsWith(".bsh")) {
                cc.setProvider(CodingConfig.RECORD_TYPE.BEANSHELL);
                cc.setBeanShellCode(s);
            }
        }

        return cc;
    }

    @Suggestions("LIST_FILES")
    public SuggestionValues getListFiles() {
        final StringBuilder root = new StringBuilder();
        root.append("/scripts/");

        try {
            final List<SuggestionValues.Item> items = walkThroughResource(root.toString()).filter(p -> Files.isRegularFile(p))
                    .map(this::pathToItem).collect(Collectors.toList());
            return new SuggestionValues(true, items);
        } catch (URISyntaxException | IOException e) {
            throw new IllegalArgumentException("Can't load files.", e);
        }
    }

    private SuggestionValues.Item pathToItem(final Path p) {
        return new SuggestionValues.Item(p.toString(), p.getFileName().toString());
    }

    public Stream<Path> walkThroughResource(final String root) throws URISyntaxException, IOException {
        URI uri = GenericService.class.getResource(root).toURI();
        final Path myPath;
        if (uri.getScheme().equals("jar")) {
            FileSystem fileSystem = null;
            try {
                fileSystem = FileSystems.getFileSystem(uri);
            } catch (FileSystemNotFoundException e) {
                fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object> emptyMap());
            }
            myPath = fileSystem.getPath(root);
        } else {
            myPath = Paths.get(uri);
        }
        final Stream<Path> walk = Files.walk(myPath);

        return walk;
    }

    private String loadStream(final InputStream stream) throws IOException {
        byte[] buff = new byte[512];

        StringBuilder sb = new StringBuilder();

        int n = 1;
        while (n >= 0) {
            n = stream.read(buff);
            if (n <= 0) {
                break;
            }
            final String s = new String(buff, 0, n, Charset.defaultCharset());
            sb.append(s);
        }

        return sb.toString();

    }

}
