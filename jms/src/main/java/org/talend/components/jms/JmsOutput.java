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

package org.talend.components.jms;

import static org.talend.sdk.component.api.component.Icon.IconType.CUSTOM;

import java.io.StringWriter;

import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;

import org.apache.beam.sdk.io.jms.JmsIO;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.meta.Documentation;

@Version
@Icon(value = CUSTOM, custom = "JmsOutput")
@PartitionMapper(name = "Output")
@Documentation("This component writes json records to a JMS server.")
public class JmsOutput extends PTransform<PCollection<JsonObject>, PDone> {

    private final JmsOutputDataset configuration;

    private final JmsService service;

    private final JsonWriterFactory writerFactory;

    public JmsOutput(@Option("configuration") final JmsOutputDataset properties, final JmsService service,
            final JsonWriterFactory writerFactory) {
        this.configuration = properties;
        this.service = service;
        this.writerFactory = writerFactory;

    }

    @Override
    public PDone expand(PCollection<JsonObject> input) {
        return input.apply("JsonToString", ParDo.of(new DoFn<JsonObject, String>() {

            @ProcessElement
            public void processElement(final ProcessContext c) {
                final StringWriter output = new StringWriter();
                try (final JsonWriter writer = writerFactory.createWriter(output)) {
                    writer.writeObject(c.element());
                }
                c.output(output.toString());
            }
        })).apply(configure());
    }

    private JmsIO.Write configure() {
        final JmsMessageType messageType = configuration.getCommon().getMsgType();
        if (messageType.equals(JmsMessageType.QUEUE)) {
            return JmsIO.write().withConnectionFactory(service.getConnectionFactory(configuration.getCommon().getDatastore()))
                    .withQueue(configuration.getCommon().getQueueTopicName());
        } else if (messageType.equals(JmsMessageType.TOPIC)) {
            return JmsIO.write().withConnectionFactory(service.getConnectionFactory(configuration.getCommon().getDatastore()))
                    .withTopic(configuration.getCommon().getQueueTopicName());

        } else {
            throw new IllegalArgumentException("Unexpected argument");
        }
    }
}
