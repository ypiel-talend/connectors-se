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
package org.talend.components.common.stream.output.csv;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.talend.components.common.stream.api.output.RecordWriter;
import org.talend.components.common.stream.api.output.RecordWriterSupplier;
import org.talend.components.common.stream.format.Encoding.Type;
import org.talend.components.common.stream.format.HeaderLine;
import org.talend.components.common.stream.format.LineConfiguration;
import org.talend.components.common.stream.format.csv.CSVConfiguration;
import org.talend.components.common.stream.format.csv.CommentMarker;
import org.talend.components.common.stream.format.csv.FieldSeparator;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.runtime.record.RecordBuilderFactoryImpl;

class CSVRecordWriterTest {

    @MethodSource("writeCSVSource")
    @ParameterizedTest
    void writeCSV(
            FieldSeparator.Type fieldSeparatorType,
            CommentMarker.Type commentMarkerType,
            Character commentMarker,
            String expected)
            throws IOException {
        final RecordWriterSupplier recordWriterSupplier = new CSVWriterSupplier();

        final CSVConfiguration config = createCsvConfiguration();
        if (fieldSeparatorType != null) {
            config.getFieldSeparator().setFieldSeparatorType(fieldSeparatorType);
        }
        if (commentMarkerType != null) {
            config.getCommentMarker().setCommentMarkerType(commentMarkerType);
        }
        if (commentMarker != null) {
            config.getCommentMarker().setCommentMarker(commentMarker);
        }

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final RecordWriter writer = recordWriterSupplier.getWriter(() -> out, config);

        writer.init(config);

        final List<Record> records = buildRecords();
        writer.add(records);

        writer.flush();
        writer.end();
        Assertions.assertEquals(expected, out.toString());
    }

    private static Stream<Arguments> writeCSVSource() {
        return Stream.of(
                Arguments.of(FieldSeparator.Type.SPACE, CommentMarker.Type.HASH, null,
                        "# \n# \n# \nhello xx\nmike 45\nbob 11\nice;peak 13\n\"ice peak\" 68\n"),
                Arguments.of(null, null, null,
                        "  \n  \n  \nhello;xx\nmike;45\nbob;11\n\"ice;peak\";13\nice peak;68\n"),
                Arguments.of(null, CommentMarker.Type.OTHER, '$',
                        "$ \n$ \n$ \nhello;xx\nmike;45\nbob;11\n\"ice;peak\";13\nice peak;68\n"));
    }

    @Test
    public void spaceFieldSeparatorWithSpaceMarker() throws IOException {
        final RecordWriterSupplier recordWriterSupplier = new CSVWriterSupplier();

        final CSVConfiguration config = createCsvConfiguration();
        config.getFieldSeparator().setFieldSeparatorType(FieldSeparator.Type.SPACE);
        config.getCommentMarker().setCommentMarkerType(CommentMarker.Type.SPACE);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final RecordWriter writer = recordWriterSupplier.getWriter(() -> out, config);

        writer.init(config);

        final List<Record> records = buildRecords();
        Assertions.assertThrows(IllegalArgumentException.class, () -> writer.add(records));
    }

    @Test
    void unsupportedEncoding() throws IOException {
        final RecordWriterSupplier recordWriterSupplier = new CSVWriterSupplier();

        final CSVConfiguration config = createCsvConfiguration();
        config.getLineConfiguration().getEncoding().setEncodingType(Type.OTHER);
        config.getLineConfiguration().getEncoding().setEncoding("BlaBla"); // not exist

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final RecordWriter writer = recordWriterSupplier.getWriter(() -> out, config);

        writer.init(config);

        final List<Record> records = buildRecords();
        Assertions.assertThrows(java.io.UnsupportedEncodingException.class, () -> {
            try {
                writer.add(records);
            } finally {
                // before the fix the execution in flush and end suppress the origin exception with NPE
                writer.flush();
                writer.end();
            }
        });
    }

    private CSVConfiguration createCsvConfiguration() {
        final CSVConfiguration config = new CSVConfiguration();
        config.setLineConfiguration(new LineConfiguration());
        config.getLineConfiguration().setLineSeparator("\n");
        config.setEscape('\\');
        config.setFieldSeparator(new FieldSeparator());
        config.getFieldSeparator().setFieldSeparatorType(FieldSeparator.Type.SEMICOLON);

        config.getLineConfiguration().setHeader(new HeaderLine());
        config.getLineConfiguration().getHeader().setActive(true);
        config.getLineConfiguration().getHeader().setSize(4);

        config.setQuotedValue('"');
        return config;
    }

    private List<Record> buildRecords() {
        final RecordBuilderFactory factory = new RecordBuilderFactoryImpl("test");
        final Record record1 = factory.newRecordBuilder().withString("hello", "mike").withInt("xx", 45).build();

        final Record record2 = factory.newRecordBuilder().withString("hello", "bob").withInt("xx", 11).build();

        final Record record3 = factory.newRecordBuilder().withString("hello", "ice;peak").withInt("xx", 13).build();

        final Record record4 = factory.newRecordBuilder().withString("hello", "ice peak").withInt("xx", 68).build();

        return Arrays.asList(record1, record2, record3, record4);
    }

}