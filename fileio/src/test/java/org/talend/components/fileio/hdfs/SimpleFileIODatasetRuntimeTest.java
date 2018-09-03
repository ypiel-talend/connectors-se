// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.fileio.hdfs;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.talend.components.test.RecordSetUtil.getEmptyTestData;
import static org.talend.components.test.RecordSetUtil.getSimpleTestData;
import static org.talend.components.test.RecordSetUtil.writeCsvFile;
import static org.talend.components.test.RecordSetUtil.writeRandomAvroFile;
import static org.talend.components.test.RecordSetUtil.writeRandomCsvFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema.Field;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.runners.direct.DirectOptions;
import org.apache.beam.runners.direct.DirectRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.Path;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.adapter.beam.BeamLocalRunnerOption;
import org.talend.components.adapter.beam.transform.DirectConsumerCollector;
import org.talend.components.fileio.configuration.EncodingType;
import org.talend.components.fileio.configuration.ExcelFormat;
import org.talend.components.fileio.configuration.SimpleFileIOFormat;
import org.talend.components.test.MiniDfsResource;
import org.talend.components.test.RecordSet;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.java8.Consumer;

/**
 * Unit tests for {@link SimpleFileIOInput}.
 */
// migrate it from tcompv0, TODO maybe we need to rename it or move to SimpleFileIOInputRuntimeTest
// as no dataset runtime in fact now in tacokit, all call input
public class SimpleFileIODatasetRuntimeTest {

    @Rule
    public MiniDfsResource mini = new MiniDfsResource();

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFileIOInput.class);

    private static SimpleFileIODataSet createSimpleFileIODataSet() {
        SimpleFileIODataSet dataset = new SimpleFileIODataSet();
        SimpleFileIODataStore datastore = new SimpleFileIODataStore();
        dataset.setDatastore(datastore);
        return dataset;
    }

    private List<IndexedRecord> getData(SimpleFileIOInput inputRuntime) {
        // TODO in this options, disable lots of thing, not right, need to check it for more complex env, not only directly local
        // and no parallize
        // run
        // options = PipelineOptionsFactory.as(DirectOptions.class);
        // options.setTargetParallelism(1);
        // options.setRunner(DirectRunner.class);
        // options.setEnforceEncodability(false);
        // options.setEnforceImmutability(false);
        DirectOptions options = BeamLocalRunnerOption.getOptions();
        final Pipeline p = Pipeline.create(options);

        List<IndexedRecord> result = new ArrayList<IndexedRecord>();
        Consumer consumer = new Consumer<IndexedRecord>() {

            @Override
            public void accept(IndexedRecord in) {
                result.add(in);
            }
        };

        try (DirectConsumerCollector<IndexedRecord> collector = DirectConsumerCollector.of(consumer)) {
            // Collect a sample of the input records.
            p.apply(inputRuntime) //
                    .apply(collector);
            try {
                p.run().waitUntilFinish();
            } catch (Pipeline.PipelineExecutionException e) {
                if (e.getCause() instanceof TalendRuntimeException)
                    throw (TalendRuntimeException) e.getCause();
                throw e;
            }
        }
        return result;
    }

    @Test
    public void testGetSchema() throws Exception {
        writeRandomAvroFile(mini.getFs(), "/user/test/input.avro", getSimpleTestData(0));
        String fileSpec = mini.getFs().getUri().resolve("/user/test/input.avro").toString();

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setFormat(SimpleFileIOFormat.AVRO);
        dataset.setPath(fileSpec);
        dataset.setLimit(1);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);
        IndexedRecord firstRecord = rows.get(0);

        assertThat(firstRecord.getSchema(), notNullValue());
    }

    @Test
    public void testEmptyCsvFile() throws Exception {
        writeRandomCsvFile(mini.getFs(), "/user/test/empty.csv", getEmptyTestData(), "UTF-8");
        String fileSpec = mini.getFs().getUri().resolve("/user/test/empty.csv").toString();

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setFormat(SimpleFileIOFormat.CSV);
        dataset.setPath(fileSpec);
        dataset.setLimit(1);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);

        assertThat(rows, hasSize(0));
    }

    @Test
    public void testGetSampleCsv() throws Exception {
        RecordSet rs = getSimpleTestData(0);
        writeRandomCsvFile(mini.getFs(), "/user/test/input5.csv", rs, "UTF-8");
        String fileSpec = mini.getFs().getUri().resolve("/user/test/input5.csv").toString();

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setFormat(SimpleFileIOFormat.CSV);
        dataset.setPath(fileSpec);
        dataset.setLimit(Integer.MAX_VALUE);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);

        // Check the expected values match.
        assertThat(rows, hasSize(10));
    }

    @Test
    public void testGetSampleCsv_header() throws Exception {
        RecordSet rs = getSimpleTestData(0);
        writeRandomCsvFile(mini.getFs(), "/user/test/input4.csv", rs, "UTF-8");
        String fileSpec = mini.getFs().getUri().resolve("/user/test/input4.csv").toString();

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setFormat(SimpleFileIOFormat.CSV);
        dataset.setPath(fileSpec);
        dataset.setSetHeaderLine4CSV(true);
        dataset.setHeaderLine4CSV(3l);
        dataset.setLimit(100);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);

        assertThat(rows, hasSize(7));
    }

    @Test
    public void testGetSampleCsv_encoding() throws Exception {
        RecordSet rs = getSimpleTestData(0);
        writeRandomCsvFile(mini.getFs(), "/user/test/input3.csv", rs, "GBK");
        String fileSpec = mini.getFs().getUri().resolve("/user/test/input3.csv").toString();

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setFormat(SimpleFileIOFormat.CSV);
        dataset.setPath(fileSpec);
        dataset.setEncoding4CSV(EncodingType.OTHER);
        dataset.setSpecificEncoding4CSV("GBK");
        dataset.setLimit(100);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);

        assertThat(rows, hasSize(10));
    }

    @Test
    public void testGetSampleCsv_encoding_header() throws Exception {
        RecordSet rs = getSimpleTestData(0);
        writeRandomCsvFile(mini.getFs(), "/user/test/input2.csv", rs, "GBK");
        String fileSpec = mini.getFs().getUri().resolve("/user/test/input2.csv").toString();

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setFormat(SimpleFileIOFormat.CSV);
        dataset.setPath(fileSpec);
        dataset.setEncoding4CSV(EncodingType.OTHER);
        dataset.setSpecificEncoding4CSV("GBK");
        dataset.setSetHeaderLine4CSV(true);
        dataset.setHeaderLine4CSV(1l);
        dataset.setLimit(100);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);

        assertThat(rows, hasSize(9));
    }

    @Test
    public void testGetSampleCsv_textEnclosure() throws Exception {
        String content = "\"wang;wei\";Beijing;100\n\"gao\nyan\";Beijing;99\ndabao;Beijing;98\n";
        writeCsvFile(mini.getFs(), "/user/test/input1.csv", content, "UTF-8");
        String fileSpec = mini.getFs().getUri().resolve("/user/test/input1.csv").toString();

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setFormat(SimpleFileIOFormat.CSV);
        dataset.setPath(fileSpec);
        dataset.setTextEnclosureCharacter("\"");
        dataset.setLimit(100);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);

        assertThat(rows, hasSize(3));

        for (IndexedRecord row : rows) {
            assertThat(row.getSchema().getFields(), hasSize(3));
        }
    }

    @Test
    public void testGetSampleCsv_escape() throws Exception {
        String content = "wang\\;wei;Beijing;100\ngaoyan;Beijing;99\ndabao;Beijing;98\n";
        writeCsvFile(mini.getFs(), "/user/test/input6.csv", content, "UTF-8");
        String fileSpec = mini.getFs().getUri().resolve("/user/test/input6.csv").toString();

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setFormat(SimpleFileIOFormat.CSV);
        dataset.setPath(fileSpec);
        dataset.setEscapeCharacter("\\");
        dataset.setLimit(100);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);

        assertThat(rows, hasSize(3));

        for (IndexedRecord row : rows) {
            assertThat(row.getSchema().getFields(), hasSize(3));
        }
    }

    @Test
    public void testGetSampleCsv_textEnclosureAndEscape() throws Exception {
        String content = "\"wa\\\"ng;wei\";Bei\\\"jing;100\n\"gao\nyan\";Bei\\\"jing;99\ndabao;Bei\\\"jing;98\n";
        writeCsvFile(mini.getFs(), "/user/test/input7.csv", content, "UTF-8");
        String fileSpec = mini.getFs().getUri().resolve("/user/test/input7.csv").toString();

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setFormat(SimpleFileIOFormat.CSV);
        dataset.setPath(fileSpec);
        dataset.setTextEnclosureCharacter("\"");
        dataset.setEscapeCharacter("\\");
        dataset.setLimit(100);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);

        assertThat(rows, hasSize(3));

        for (IndexedRecord row : rows) {
            assertThat(row.getSchema().getFields(), hasSize(3));
        }
    }

    @Test
    public void testGetSampleCsv_multipleSources() throws Exception {
        RecordSet rs1 = getSimpleTestData(0);
        writeRandomCsvFile(mini.getFs(), "/user/test/input/part-00000", rs1, "UTF-8");
        RecordSet rs2 = getSimpleTestData(100);
        writeRandomCsvFile(mini.getFs(), "/user/test/input/part-00001", rs2, "UTF-8");
        RecordSet rs3 = getSimpleTestData(100);
        writeRandomCsvFile(mini.getFs(), "/user/test/input/part-00002", rs3, "UTF-8");
        String fileSpec = mini.getFs().getUri().resolve("/user/test/input/").toString();

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setFormat(SimpleFileIOFormat.CSV);
        dataset.setPath(fileSpec);
        dataset.setLimit(15);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);
        assertThat(rows, hasSize(15));
        // Run it again to verify that the static state is not retained.
        rows = getData(runtime);
        assertThat(rows, hasSize(15));
    }

    @Test
    public void testGetSampleAvro() throws Exception {
        RecordSet rs = getSimpleTestData(0);
        writeRandomAvroFile(mini.getFs(), "/user/test/input.avro", rs);
        String fileSpec = mini.getFs().getUri().resolve("/user/test/input.avro").toString();

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setFormat(SimpleFileIOFormat.AVRO);
        dataset.setPath(fileSpec);
        dataset.setLimit(100);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);

        // Check the expected values.
        assertThat(rows, (Matcher) equalTo(rs.getAllData()));
    }

    @Test
    public void testGetSampleExcelHtml() throws Exception {
        String fileSpec = sourceFilePrepare("sales-force.html");

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setPath(fileSpec);
        dataset.setFormat(SimpleFileIOFormat.EXCEL);
        dataset.setExcelFormat(ExcelFormat.HTML);
        dataset.setSetHeaderLine4EXCEL(true);
        dataset.setHeaderLine4EXCEL(1l);
        dataset.setLimit(100);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);

        assertThat(rows, hasSize(100));
        List<Field> fields = rows.get(0).getSchema().getFields();
        assertThat(fields, hasSize(7));
        assertThat("UID", equalTo(fields.get(0).name()));
        assertThat("Hire_Date", equalTo(fields.get(6).name()));

        assertThat("000001", equalTo(rows.get(0).get(0)));
        assertThat("France", equalTo(rows.get(0).get(5)));
    }

    private String sourceFilePrepare(String filename) throws IOException {
        InputStream in = getClass().getResourceAsStream(filename);
        try (OutputStream inOnMinFS = mini.getFs().create(new Path("/user/test/" + filename))) {
            inOnMinFS.write(IOUtils.toByteArray(in));
        }
        String fileSpec = mini.getFs().getUri().resolve("/user/test/" + filename).toString();
        return fileSpec;
    }

    @Test
    public void testGetSampleExcelHtml_header() throws Exception {
        String fileSpec = sourceFilePrepare("sales-force.html");

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setPath(fileSpec);
        dataset.setFormat(SimpleFileIOFormat.EXCEL);
        dataset.setExcelFormat(ExcelFormat.HTML);
        dataset.setSetHeaderLine4EXCEL(true);
        dataset.setHeaderLine4EXCEL(900l);
        dataset.setLimit(100);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);

        assertThat(rows, hasSize(47));
        List<Field> fields = rows.get(0).getSchema().getFields();
        assertThat(fields, hasSize(7));
        assertThat("field0", equalTo(fields.get(0).name()));
        assertThat("field6", equalTo(fields.get(6).name()));

        assertThat("000931", equalTo(rows.get(0).get(0)));
        assertThat("", equalTo(rows.get(0).get(5)));
    }

    @Test
    public void testGetSampleExcelHtml_header_footer() throws Exception {
        String fileSpec = sourceFilePrepare("sales-force.html");

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setPath(fileSpec);
        dataset.setFormat(SimpleFileIOFormat.EXCEL);
        dataset.setExcelFormat(ExcelFormat.HTML);
        dataset.setSetHeaderLine4EXCEL(true);
        dataset.setHeaderLine4EXCEL(900l);
        dataset.setSetFooterLine4EXCEL(true);
        dataset.setFooterLine4EXCEL(1l);
        dataset.setLimit(100);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);

        assertThat(rows, hasSize(46));
        List<Field> fields = rows.get(0).getSchema().getFields();
        assertThat(fields, hasSize(7));
        assertThat("field0", equalTo(fields.get(0).name()));
        assertThat("field6", equalTo(fields.get(6).name()));

        assertThat("000931", equalTo(rows.get(0).get(0)));
        assertThat("", equalTo(rows.get(0).get(5)));
    }

    @Test
    public void testGetSampleExcel_emptyrow() throws Exception {
        String fileSpec = sourceFilePrepare("emptyrowexist.xlsx");

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setPath(fileSpec);
        dataset.setFormat(SimpleFileIOFormat.EXCEL);
        dataset.setExcelFormat(ExcelFormat.EXCEL2007);
        dataset.setSetHeaderLine4EXCEL(true);
        dataset.setHeaderLine4EXCEL(1l);
        dataset.setLimit(1000);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);

        assertThat(rows, hasSize(199));
        List<Field> fields = rows.get(0).getSchema().getFields();
        assertThat(fields, hasSize(5));
    }

    @Test
    public void testGetSampleExcel2007_TDI_40654() throws Exception {
        String fileSpec = sourceFilePrepare("emptyfield.xlsx");

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setPath(fileSpec);
        dataset.setFormat(SimpleFileIOFormat.EXCEL);
        dataset.setExcelFormat(ExcelFormat.EXCEL2007);
        dataset.setSetHeaderLine4EXCEL(true);
        dataset.setHeaderLine4EXCEL(1l);
        dataset.setLimit(100);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);

        assertThat(rows, hasSize(3));

        List<Field> fields = rows.get(0).getSchema().getFields();
        assertThat(fields, hasSize(8));
        assertThat("field0", equalTo(fields.get(0).name()));
        assertThat("field7", equalTo(fields.get(7).name()));

        assertThat("", equalTo(rows.get(0).get(0)));
        assertThat("2", equalTo(rows.get(0).get(1)));
        assertThat("false", equalTo(rows.get(0).get(2)));
        assertThat("", equalTo(rows.get(0).get(3)));
        assertThat("3.4", equalTo(rows.get(0).get(4)));
        assertThat("2018-04-26", equalTo(rows.get(0).get(5)));
        assertThat("", equalTo(rows.get(0).get(6)));
        assertThat("TDI-T3_V1", equalTo(rows.get(0).get(7)));
    }

    @Test
    public void testGetSampleExcel97_TDI_40654() throws Exception {
        String fileSpec = sourceFilePrepare("emptyfield.xls");

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setPath(fileSpec);
        dataset.setFormat(SimpleFileIOFormat.EXCEL);
        dataset.setExcelFormat(ExcelFormat.EXCEL97);
        dataset.setSetHeaderLine4EXCEL(true);
        dataset.setHeaderLine4EXCEL(1l);
        dataset.setLimit(100);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);

        assertThat(rows, hasSize(3));

        List<Field> fields = rows.get(0).getSchema().getFields();
        assertThat(fields, hasSize(8));
        assertThat("field0", equalTo(fields.get(0).name()));
        assertThat("field7", equalTo(fields.get(7).name()));

        assertThat("", equalTo(rows.get(0).get(0)));
        assertThat("2", equalTo(rows.get(0).get(1)));
        assertThat("false", equalTo(rows.get(0).get(2)));
        assertThat("", equalTo(rows.get(0).get(3)));
        assertThat("3.4", equalTo(rows.get(0).get(4)));
        assertThat("2018-04-26", equalTo(rows.get(0).get(5)));
        assertThat("", equalTo(rows.get(0).get(6)));
        assertThat("TDI-T3_V1", equalTo(rows.get(0).get(7)));
    }

    @Test
    public void testGetSampleExcel97() throws Exception {
        String fileSpec = sourceFilePrepare("basic.xls");

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setPath(fileSpec);
        dataset.setFormat(SimpleFileIOFormat.EXCEL);
        dataset.setExcelFormat(ExcelFormat.EXCEL97);
        dataset.setSheet("Sheet1");
        dataset.setSetHeaderLine4EXCEL(true);
        dataset.setHeaderLine4EXCEL(1l);
        dataset.setLimit(100);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);

        assertThat(rows, hasSize(2));
        List<Field> fields = rows.get(0).getSchema().getFields();
        assertThat(fields, hasSize(3));

        assertThat("2", equalTo(rows.get(0).get(0)));
        assertThat("gaoyan", equalTo(rows.get(0).get(1)));
        assertThat("Shunyi", equalTo(rows.get(0).get(2)));
    }

    @Test
    public void testGetSampleExcel() throws Exception {
        String fileSpec = sourceFilePrepare("basic.xlsx");

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setPath(fileSpec);
        dataset.setFormat(SimpleFileIOFormat.EXCEL);
        dataset.setSheet("Sheet1");
        dataset.setSetHeaderLine4EXCEL(true);
        dataset.setHeaderLine4EXCEL(1l);
        dataset.setLimit(100);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);

        assertThat(rows, hasSize(2));
        List<Field> fields = rows.get(0).getSchema().getFields();
        assertThat(fields, hasSize(3));

        assertThat("2", equalTo(rows.get(0).get(0)));
        assertThat("gaoyan", equalTo(rows.get(0).get(1)));
        assertThat("Shunyi", equalTo(rows.get(0).get(2)));
    }

    @Test
    public void testGetSampleExcel_no_sheet() throws Exception {
        String fileSpec = sourceFilePrepare("basic.xlsx");

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setPath(fileSpec);
        dataset.setFormat(SimpleFileIOFormat.EXCEL);
        dataset.setSetHeaderLine4EXCEL(true);
        dataset.setHeaderLine4EXCEL(1l);
        dataset.setLimit(100);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);

        assertThat(rows, hasSize(2));
        List<Field> fields = rows.get(0).getSchema().getFields();
        assertThat(fields, hasSize(3));

        assertThat("2", equalTo(rows.get(0).get(0)));
        assertThat("gaoyan", equalTo(rows.get(0).get(1)));
        assertThat("Shunyi", equalTo(rows.get(0).get(2)));
    }

    @Test
    public void testGetSampleExcel_header() throws Exception {
        String fileSpec = sourceFilePrepare("basic.xlsx");

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setPath(fileSpec);
        dataset.setFormat(SimpleFileIOFormat.EXCEL);
        dataset.setSetHeaderLine4EXCEL(true);
        dataset.setLimit(100);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);

        assertThat(rows, hasSize(2));
        List<Field> fields = rows.get(0).getSchema().getFields();
        assertThat(fields, hasSize(3));

        assertThat("2", equalTo(rows.get(0).get(0)));
        assertThat("gaoyan", equalTo(rows.get(0).get(1)));
        assertThat("Shunyi", equalTo(rows.get(0).get(2)));
    }

    @Test
    public void testGetSampleExcel_footer() throws Exception {
        String fileSpec = sourceFilePrepare("basic.xlsx");

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setPath(fileSpec);
        dataset.setFormat(SimpleFileIOFormat.EXCEL);
        dataset.setSheet("Sheet1");
        dataset.setSetFooterLine4EXCEL(true);
        dataset.setFooterLine4EXCEL(1l);
        dataset.setLimit(100);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);

        assertThat(rows, hasSize(2));
        List<Field> fields = rows.get(0).getSchema().getFields();
        assertThat(fields, hasSize(3));

        assertThat("1", equalTo(rows.get(0).get(0)));
        assertThat("wangwei", equalTo(rows.get(0).get(1)));
        assertThat("Shunyi", equalTo(rows.get(0).get(2)));
    }

    @Test
    public void testGetSampleExcel_header_footer() throws Exception {
        String fileSpec = sourceFilePrepare("basic.xlsx");

        // Configure the component.
        SimpleFileIODataSet dataset = createSimpleFileIODataSet();
        dataset.setPath(fileSpec);
        dataset.setFormat(SimpleFileIOFormat.EXCEL);
        dataset.setSheet("Sheet1");
        dataset.setSetHeaderLine4EXCEL(true);
        dataset.setSetFooterLine4EXCEL(true);
        dataset.setFooterLine4EXCEL(1l);
        dataset.setLimit(100);

        // Create the runtime.
        SimpleFileIOInput runtime = new SimpleFileIOInput(dataset);

        List<IndexedRecord> rows = getData(runtime);

        assertThat(rows, hasSize(1));
        List<Field> fields = rows.get(0).getSchema().getFields();
        assertThat(fields, hasSize(3));

        assertThat("2", equalTo(rows.get(0).get(0)));
        assertThat("gaoyan", equalTo(rows.get(0).get(1)));
        assertThat("Shunyi", equalTo(rows.get(0).get(2)));
    }

}
