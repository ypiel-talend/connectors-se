package org.talend.components.ftp.output;

import lombok.extern.slf4j.Slf4j;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.slf4j.impl.StaticLoggerBinder;
import org.talend.components.ftp.dataset.FTPDataSet;
import org.talend.components.ftp.datastore.FTPDataStore;
import org.talend.components.ftp.jupiter.FtpFile;
import org.talend.components.ftp.jupiter.FtpServer;
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
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.junit5.environment.EnvironmentalTest;
import org.talend.sdk.component.runtime.manager.chain.Job;

import java.io.ByteArrayOutputStream;
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
@WithComponents(value = "org.talend.components.ftp")
@FtpFile(base = "fakeFTP/")
public class FTPOutputTest {

    @Injected
    private BaseComponentsHandler componentsHandler;

    @Service
    public RecordBuilderFactory rbf;

    private FTPOutputConfiguration configuration;

    @Rule
    public final SimpleComponentRule COMPONENTS = new SimpleComponentRule("org.talend.sdk.component.mycomponent");

    @BeforeEach
    void buildConfig() {

        final StaticLoggerBinder binder = StaticLoggerBinder.getSingleton();

        componentsHandler.injectServices(this);

        FTPDataStore datastore = new FTPDataStore();
        datastore.setHost("localhost");
        datastore.setUseCredentials(true);
        datastore.setUsername(FtpServer.USER);
        datastore.setPassword(FtpServer.PASSWD);

        FTPDataSet dataset = new FTPDataSet();
        dataset.setDatastore(datastore);
        dataset.setPath("/out");

        configuration = new FTPOutputConfiguration();
        configuration.setDataSet(dataset);
        configuration.setDebug(true);
    }

    @EnvironmentalTest
    public void testOutput(UnixFakeFileSystem fileSystem) {
        int nbRecords = 200;
        configuration.getDataSet().setFormat(FTPDataSet.Format.CSV);
        configuration.setLimitBy(FTPOutputConfiguration.LimitBy.RECORDS);
        configuration.setRecordsLimit(500);

        String configURI = SimpleFactory.configurationByExample().forInstance(configuration).configured().toQueryString();

         Schema schema = rbf.newSchemaBuilder(Schema.Type.RECORD)
                .withEntry(rbf.newEntryBuilder().withName("k").withType(Schema.Type.STRING).build())
                .withEntry(rbf.newEntryBuilder().withName("v").withType(Schema.Type.STRING).build())
                .build();

        List<Record> inputData = IntStream.range(0, nbRecords).mapToObj(i ->
            rbf.newRecordBuilder(schema).withString("k", "entry" + i).withString("v", "value" + i).build()
        ).collect(Collectors.toList());


        COMPONENTS.setInputData(inputData);

        Job.components().component("source", "test://emitter").component("output", "FTP://FTPOutput?" + configURI)
                .connections().from("source").to("output").build().run();

        List<FileEntry> files = fileSystem.listFiles("/out");
        List<String> csvLines = new ArrayList<>();
        files.stream().filter(f -> f.getName() != "/out/file1.txt")
                .map(FileEntry::createInputStream)
                .map(is -> {
                    try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
                        byte[] buffer = new byte[1024];
                        int read = 0;
                        while ((read = is.read(buffer)) > 0) {
                            bout.write(buffer, 0, read);
                        }
                        return bout.toByteArray();
                    } catch (IOException ioe) {
                        log.error(ioe.getMessage(), ioe);
                        return new byte[0];
                    }
                })
                .map(b -> new String(b, StandardCharsets.ISO_8859_1))
                .forEach(s -> {
                    log.debug(s);
                    Arrays.stream(s.split("\r\n"))
                            .filter(l ->  !"".equals(l.trim()))
                            .forEach(csvLines::add);
                });

        Assertions.assertEquals(nbRecords, csvLines.size(), "Wrong number of lines");
    }
}
