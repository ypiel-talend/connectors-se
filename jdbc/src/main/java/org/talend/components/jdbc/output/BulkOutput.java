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
package org.talend.components.jdbc.output;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.talend.components.common.stream.api.RecordIORepository;
import org.talend.components.common.stream.api.output.RecordWriter;
import org.talend.components.common.stream.api.output.RecordWriterSupplier;
import org.talend.components.common.stream.format.ContentFormat;
import org.talend.components.common.stream.format.csv.CSVConfiguration;
import org.talend.components.jdbc.configuration.OutputConfig;
import org.talend.components.jdbc.output.platforms.Platform;
import org.talend.components.jdbc.output.platforms.PlatformFactory;
import org.talend.components.jdbc.output.statement.QueryManager;
import org.talend.components.jdbc.output.statement.QueryManagerFactory;
import org.talend.components.jdbc.output.statement.operations.QueryManagerImpl;
import org.talend.components.jdbc.service.I18nMessage;
import org.talend.components.jdbc.service.JdbcService;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.AfterGroup;
import org.talend.sdk.component.api.processor.BeforeGroup;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Version(value = 1)
@Processor(name = "BulkOutput")
@Icon(value = Icon.IconType.DATASTORE)
@Documentation("JDBC bulk Output component.")
public class BulkOutput extends Output {

    @Getter
    private QueryManager<Path> queryManager;

    @Getter
    private Platform platform;

    private RecordIORepository ioRepository;

    private transient Path tempDirectory;

    private transient Path currentTempFile;

    private transient ContentFormat contentFormat;

    private transient RecordWriterSupplier recordWriterSupplier;

    private transient RecordWriter recordWriter;

    private transient Record firstRecord;

    public BulkOutput(@Option("configuration") final OutputConfig configuration, final JdbcService jdbcService,
            final I18nMessage i18n, final RecordIORepository ioRepository) {
        super(configuration, jdbcService, i18n);
        this.platform = PlatformFactory.get(configuration.getDataset().getConnection(), i18n);
        configuration.setActionOnData(OutputConfig.ActionOnData.BULK_LOAD.toString());
        this.queryManager = (QueryManager<Path>) QueryManagerFactory.getQueryManager(platform, i18n, configuration);
        this.ioRepository = ioRepository;
    }

    @PostConstruct
    public void postConstruct() throws Exception {
        System.out.println("PostConstruct");
        contentFormat = new CSVConfiguration();
        recordWriterSupplier = ioRepository.findWriter(contentFormat.getClass());
        tempDirectory = Files.createTempDirectory("temp-jdbc-bulk-");
    }

    @BeforeGroup
    public void beforeGroup() throws Exception {
        System.out.println(recordWriterSupplier);
        // Create temp file to store records
        currentTempFile = Files.createTempFile(tempDirectory, "jdbc-bulk-", ".csv");
        recordWriter = recordWriterSupplier.getWriter(() -> new FileOutputStream(currentTempFile.toFile()), contentFormat);
        recordWriter.init(contentFormat);
    }

    @AfterGroup
    public void afterGroup() throws Exception {
        checkForTable();
        // Close current writer
        recordWriter.flush();
        recordWriter.end();
        recordWriter.close();
        // Load temp file to database
        queryManager.execute(currentTempFile, getDatasource());
        // Delete temp file
        Files.delete(currentTempFile);
        currentTempFile = null;
    }

    @Override
    protected List<Record> getRecords() {
        return Collections.singletonList(firstRecord);
    }

    @ElementListener
    public void elementListener(@Input final Record record) throws Exception {

        System.out.println(record);
        super.elementListener(record);

        if (firstRecord == null) {
            firstRecord = record;
        }

        recordWriter.add(record);
    }

    @PreDestroy
    public void preDestroy() {

        System.out.println("PreDestroy");

        super.preDestroy();
        try {
            if (currentTempFile != null) {
                Files.deleteIfExists(currentTempFile);
                currentTempFile = null;
            }
            if (tempDirectory != null) {
                Files.deleteIfExists(tempDirectory);
                tempDirectory = null;
            }
        } catch (IOException e) {
            log.error(getI18n().errorCantDeleteTempDirectory(tempDirectory), e);
        }
    }
}
