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

import org.apache.beam.sdk.io.jms.JmsIO;
import org.apache.beam.sdk.io.jms.JmsRecord;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Duration;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.meta.Documentation;

@Version
@Icon(value = CUSTOM, custom = "JmsInput")
@PartitionMapper(name = "Input")
@Documentation("This component reads a dataset from a JMS server.")
public class JmsInput extends PTransform<PBegin, PCollection> {

    transient private JmsInputDataset configuration;

    private JmsService jmsDatastoreService;

    public JmsInput(@Option("configuration") final JmsInputDataset properties, final JmsService jmsDatastoreService) {
        this.configuration = properties;
        this.jmsDatastoreService = jmsDatastoreService;
    }

    @Override
    public PCollection expand(PBegin pBegin) {
        JmsIO.Read read = JmsIO.read()
                .withConnectionFactory(jmsDatastoreService.getConnectionFactory(configuration.getCommon().getDatastore()));
        final JmsMessageType messageType = configuration.getCommon().getMsgType();
        if (messageType.equals(JmsMessageType.QUEUE)) {
            read = read.withQueue(configuration.getCommon().getQueueTopicName());
        } else if (messageType.equals(JmsMessageType.TOPIC)) {
            read = read.withTopic(configuration.getCommon().getQueueTopicName());
        }

        if (configuration.getMaxMsg() != -1 && configuration.getTimeout() != -1) {
            read = read.withMaxNumRecords(configuration.getMaxMsg()).withMaxReadTime(Duration.millis(configuration.getTimeout()));
        } else if (configuration.getMaxMsg() != -1) {
            read = read.withMaxNumRecords(configuration.getMaxMsg());
        } else if (configuration.getTimeout() != -1) {
            read = read.withMaxReadTime(Duration.millis(configuration.getTimeout()));
        }
        PCollection<JmsRecord> jmsCollection = (PCollection<JmsRecord>) pBegin.apply(read);

        if (jmsCollection != null) {
            PCollection<String> outputCollection = jmsCollection.apply("JmsRecordToIndexedRecord",
                    ParDo.of(new DoFn<JmsRecord, String>() {

                        @DoFn.ProcessElement
                        public void processElement(ProcessContext c) throws Exception {
                            c.output(c.element().getPayload());
                        }
                    }));
            return outputCollection;
        }
        return null;
    }
}
