/*
 * Copyright (C) 2006-2022 Talend Inc. - www.talend.com
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
package org.talend.components.adlsgen2;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import javax.json.JsonBuilderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.talend.components.adlsgen2.common.format.FileFormat;
import org.talend.components.adlsgen2.dataset.AdlsGen2DataSet;
import org.talend.components.adlsgen2.datastore.AdlsGen2Connection;
import org.talend.components.adlsgen2.input.InputConfiguration;
import org.talend.components.adlsgen2.output.OutputConfiguration;
import org.talend.components.adlsgen2.service.AdlsGen2Service;
import org.talend.components.adlsgen2.service.I18n;
import org.talend.components.common.connection.adls.AuthMethod;
import org.talend.components.common.converters.CSVConverterForADLS;
import org.talend.components.common.formats.csv.CSVFieldDelimiter;
import org.talend.components.common.formats.csv.CSVFormatOptions;
import org.talend.components.common.formats.csv.CSVFormatOptionsWithSchema;
import org.talend.components.common.formats.csv.CSVRecordDelimiter;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema.Entry;
import org.talend.sdk.component.api.record.Schema.Type;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;

@WithComponents("org.talend.components.adlsgen2")
public class AdlsGen2TestBase implements Serializable {

    @Injected
    protected BaseComponentsHandler componentsHandler;

    @Service
    protected RecordBuilderFactory recordBuilderFactory;

    @Service
    protected JsonBuilderFactory jsonBuilderFactory;

    @Service
    protected AdlsGen2Service service;

    protected AdlsGen2Connection connection;

    protected AdlsGen2DataSet dataSet;

    // protected AdlsDatasetRuntimeInfo datasetRuntimeInfo;

    protected InputConfiguration inputConfiguration;

    protected OutputConfiguration outputConfiguration;

    protected Record versatileRecord;

    protected Record complexRecord;

    protected String tmpDir;

    protected ZonedDateTime now;

    protected String basePathIn = "TestIT/in/";

    protected String basePathOut = "TestIT/out/";

    @BeforeEach
    protected void setUp() throws Exception {
        tmpDir = System.getProperty("java.io.tmpdir", ".") + "/";

        service = new AdlsGen2Service();

        connection = new AdlsGen2Connection();
        connection.setAuthMethod(AuthMethod.SAS);
        connection.setAccountName("accountName");
        connection.setSharedKey("accountKey");
        connection.setSas(";Fake=Sas");
        connection.setTimeout(600);

        dataSet = new AdlsGen2DataSet();
        dataSet.setConnection(connection);
        dataSet.setFilesystem("storageFs");
        dataSet.setBlobPath("myNewFolder/customer_20190325.csv");
        dataSet.setFormat(FileFormat.CSV);
        dataSet.setCsvConfiguration(new CSVFormatOptionsWithSchema());

        // datasetRuntimeInfo = new AdlsDatasetRuntimeInfo(dataSet, tokenProviderService);

        CSVFormatOptionsWithSchema csvConfig = new CSVFormatOptionsWithSchema();
        csvConfig.setCsvFormatOptions(new CSVFormatOptions());
        csvConfig.getCsvFormatOptions().setFieldDelimiter(CSVFieldDelimiter.SEMICOLON);
        csvConfig.getCsvFormatOptions().setRecordDelimiter(CSVRecordDelimiter.LF);
        csvConfig.setCsvSchema("id;firstname;lastname;address;enrolled;zip;state");
        dataSet.setCsvConfiguration(csvConfig);

        inputConfiguration = new InputConfiguration();
        inputConfiguration.setDataSet(dataSet);

        outputConfiguration = new OutputConfiguration();
        outputConfiguration.setDataSet(dataSet);

        // some demo records
        versatileRecord = recordBuilderFactory
                .newRecordBuilder() //
                .withString("string1", "Bonjour") //
                .withString("string2", "Olà") //
                .withInt("int", 71) //
                .withBoolean("boolean", true) //
                .withLong("long", 1971L) //
                .withDateTime("datetime", LocalDateTime.of(2019, 04, 22, 0, 0).atZone(ZoneOffset.UTC)) //
                .withFloat("float", 20.5f) //
                .withDouble("double", 20.5) //
                .build();
        Entry er = recordBuilderFactory
                .newEntryBuilder()
                .withName("record")
                .withType(Type.RECORD)
                .withElementSchema(versatileRecord.getSchema())
                .build();
        Entry ea = recordBuilderFactory
                .newEntryBuilder()
                .withName("array")
                .withType(Type.ARRAY)
                .withElementSchema(recordBuilderFactory.newSchemaBuilder(Type.ARRAY).withType(Type.STRING).build())
                .build();
        //
        now = ZonedDateTime.now();
        complexRecord = recordBuilderFactory
                .newRecordBuilder() //
                .withString("name", "ComplexR") //
                .withRecord(er, versatileRecord) //
                .withDateTime("now", now) //
                .withArray(ea, Arrays.asList("ary1", "ary2", "ary3"))
                .build();
        // inject needed services
        componentsHandler.injectServices(CSVConverterForADLS.class);
        I18n i18 = componentsHandler.findService(I18n.class);
        recordBuilderFactory = componentsHandler.findService(RecordBuilderFactory.class);
    }

    protected Record createData() {
        Record record = recordBuilderFactory
                .newRecordBuilder() //
                .withString("id", "1") //
                .withString("firstname", "firstfirst") //
                .withString("lastname", "lastlast") //
                .withString("address", "addressaddr") //
                .withString("enrolled", "Datedsldsk") //
                .withString("zip", "89100") //
                .withString("state", "YO") //
                .build();

        return record;
    }

}
