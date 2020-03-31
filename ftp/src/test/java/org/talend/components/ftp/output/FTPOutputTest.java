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
package org.talend.components.ftp.output;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.impl.StaticLoggerBinder;
import org.talend.components.ftp.dataset.FTPDataSet;
import org.talend.components.ftp.datastore.FTPDataStore;
import org.talend.components.ftp.source.FTPInputConfiguration;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.junit.SimpleFactory;
import org.talend.sdk.component.junit.environment.Environment;
import org.talend.sdk.component.junit.environment.EnvironmentConfiguration;
import org.talend.sdk.component.junit.environment.builtin.ContextualEnvironment;
import org.talend.sdk.component.junit.environment.builtin.beam.SparkRunnerEnvironment;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.junit5.environment.EnvironmentalTest;
import org.talend.sdk.component.runtime.manager.chain.Job;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Environment(ContextualEnvironment.class)
@EnvironmentConfiguration(environment = "Contextual", systemProperties = {})

// @Environment(SparkRunnerEnvironment.class)
// @EnvironmentConfiguration(environment = "Spark", systemProperties = {
// @EnvironmentConfiguration.Property(key = "talend.beam.job.runner", value = "org.apache.beam.runners.spark.SparkRunner"),
// @EnvironmentConfiguration.Property(key = "talend.beam.job.filesToStage", value = ""),
// @EnvironmentConfiguration.Property(key = "spark.ui.enabled", value = "false")})

@WithComponents(value = "org.talend.components.ftp")
@Testcontainers
public class FTPOutputTest {

    @Injected
    private BaseComponentsHandler componentsHandler;

    @Container
    public GenericContainer ftpContainer = new GenericContainer("stilliard/pure-ftpd:latest")
            .withExposedPorts(21)
            .withEnv("PUBLICHOST", "localhost")
            .withEnv("ADDED_FLAGS", "-d")
            .withEnv("FTP_USER_NAME", "user")
            .withEnv("FTP_USER_PASS", "passwd")
            .withEnv("FTP_USER_HOME", "/home/ftpusers")
            .withLogConsumer(new Slf4jLogConsumer(log))
            .withClasspathResourceMapping("fakeFTP", "/home/ftpusers", BindMode.READ_WRITE)
            .withCommand();

    @Service
    public RecordBuilderFactory rbf;

    private FTPOutputConfiguration configuration;

    @Rule
    public final SimpleComponentRule COMPONENTS = new SimpleComponentRule("org.talend.sdk.component.mycomponent");

    private List<File> listFiles(String path) throws Exception {
        File base = new File(Thread.currentThread().getContextClassLoader().getResource("fakeFTP").toURI());
        return Arrays.asList(new File(base, path).listFiles());
    }

    @BeforeEach
    void buildConfig() {

        final StaticLoggerBinder binder = StaticLoggerBinder.getSingleton();

        componentsHandler.injectServices(this);

        FTPDataStore datastore = new FTPDataStore();
        datastore.setHost("localhost");
        datastore.setPort(ftpContainer.getMappedPort(21));
        datastore.setUseCredentials(true);
        datastore.setUsername("user");
        datastore.setPassword("passwd");

        FTPDataSet dataset = new FTPDataSet();
        dataset.setDatastore(datastore);

        configuration = new FTPOutputConfiguration();
        configuration.setDataSet(dataset);
        configuration.setDebug(true);
    }

    @EnvironmentalTest
    public void testRecordLimit() throws Exception {
        String path = "/out";
        int nbRecords = 210;
        int expectedFiles = 5;

        configuration.getDataSet().setPath(path);
        configuration.getDataSet().setFormat(FTPDataSet.Format.CSV);
        configuration.setLimitBy(FTPOutputConfiguration.LimitBy.RECORDS);
        configuration.setRecordsLimit(50);

        String configURI = SimpleFactory.configurationByExample().forInstance(configuration).configured().toQueryString();

        Schema schema = rbf.newSchemaBuilder(Schema.Type.RECORD)
                .withEntry(rbf.newEntryBuilder().withName("k").withType(Schema.Type.STRING).build())
                .withEntry(rbf.newEntryBuilder().withName("v").withType(Schema.Type.STRING).build()).build();

        List<Record> inputData = IntStream.range(0, nbRecords)
                .mapToObj(i -> rbf.newRecordBuilder(schema).withString("k", "entry" + i).withString("v", "value" + i).build())
                .collect(Collectors.toList());

        COMPONENTS.setInputData(inputData);

        Job.components().component("source", "test://emitter").component("output", "FTP://FTPOutput?" + configURI).connections()
                .from("source").to("output").build().run();

        // Waiting for completion
        List<File> files = listFiles(path);
        int nbFiles = files.size();
        int nbRetry = 0;
        int maxNbRetries = 5;
        while (nbFiles < expectedFiles && nbRetry < maxNbRetries) {
            files = listFiles(path);
            nbFiles = files.size();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (nbRetry > maxNbRetries) {
            Assertions.fail("Wrong number of files generated : " + nbFiles + " instead of " + expectedFiles);
        }

        List<String> csvLines = new ArrayList<>();
        files.stream().map(f -> {
            try {
                return new FileInputStream(f);
            } catch (FileNotFoundException e) {
                log.error(e.getMessage(), e);
                return null;
            }
        }).map(is -> {
            try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int read = 0;
                while ((read = is.read(buffer)) > 0) {
                    bout.write(buffer, 0, read);
                    bout.flush();
                }
                return bout.toByteArray();
            } catch (IOException ioe) {
                log.error(ioe.getMessage(), ioe);
                return new byte[0];
            }
        }).map(b -> new String(b, StandardCharsets.ISO_8859_1)).forEach(s -> {
            Arrays.stream(s.split("\r\n")).filter(l -> !"".equals(l.trim())).forEach(csvLines::add);
        });

        Assertions.assertEquals(nbRecords, csvLines.size(), "Wrong number of lines");
    }

    // @EnvironmentalTest
    // public void testSizeLimit() {
    // String path = "/out";
    // int nbRecords = 200;
    // int expectedFiles = 4;
    //
    // configuration.getDataSet().setPath(path);
    // configuration.getDataSet().setFormat(FTPDataSet.Format.CSV);
    // configuration.setLimitBy(FTPOutputConfiguration.LimitBy.SIZE);
    // configuration.setSizeLimit(1);
    // configuration.setSizeUnit(FTPOutputConfiguration.SizeUnit.KB);
    //
    // String configURI = SimpleFactory.configurationByExample().forInstance(configuration).configured().toQueryString();
    //
    // Schema schema = rbf.newSchemaBuilder(Schema.Type.RECORD)
    // .withEntry(rbf.newEntryBuilder().withName("k").withType(Schema.Type.STRING).build())
    // .withEntry(rbf.newEntryBuilder().withName("v").withType(Schema.Type.STRING).build()).build();
    //
    // List<Record> inputData = IntStream.range(0, nbRecords)
    // .mapToObj(i -> rbf.newRecordBuilder(schema).withString("k", "entry" + i).withString("v", "value" + i).build())
    // .collect(Collectors.toList());
    //
    // COMPONENTS.setInputData(inputData);
    //
    // Job.components().component("source", "test://emitter").component("output", "FTP://FTPOutput?" + configURI).connections()
    // .from("source").to("output").build().run();
    //
    // // Waiting for completion
    // List<FileEntry> files = fileSystem.listFiles(path);
    // int nbFiles = files.size();
    // int nbRetry = 0;
    // int maxNbRetries = 5;
    // while (nbFiles < expectedFiles && nbRetry < maxNbRetries) {
    // files = fileSystem.listFiles(path);
    // nbFiles = files.size();
    // try {
    // Thread.sleep(500);
    // } catch (InterruptedException e) {
    // e.printStackTrace();
    // }
    // }
    // if (nbRetry > maxNbRetries) {
    // Assertions.fail("Wrong number of files generated : " + nbFiles + " instead of " + expectedFiles);
    // }
    //
    // List<String> csvLines = new ArrayList<>();
    // files.stream().map(FileEntry::createInputStream).map(is -> {
    // try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
    // byte[] buffer = new byte[1024];
    // int read = 0;
    // while ((read = is.read(buffer)) > 0) {
    // bout.write(buffer, 0, read);
    // bout.flush();
    // }
    // return bout.toByteArray();
    // } catch (IOException ioe) {
    // log.error(ioe.getMessage(), ioe);
    // return new byte[0];
    // }
    // }).map(b -> new String(b, StandardCharsets.ISO_8859_1)).forEach(s -> {
    // Arrays.stream(s.split("\r\n")).filter(l -> !"".equals(l.trim())).forEach(csvLines::add);
    // });
    //
    // Assertions.assertEquals(nbRecords, csvLines.size(), "Wrong number of lines");
    // files.stream().map(FileEntry::getName).forEach(fileSystem::delete);
    // }
}
