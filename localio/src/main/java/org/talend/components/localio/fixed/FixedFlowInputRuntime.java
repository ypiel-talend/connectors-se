package org.talend.components.localio.fixed;

import org.apache.avro.generic.IndexedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.sdk.component.api.base.BufferizedProducerSupport;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.*;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.runtime.beam.spi.record.AvroRecord;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Version
@Icon(value = Icon.IconType.CUSTOM, custom = "FixedFlowInputRuntime")
@PartitionMapper(name = "FixedFlowInputRuntime")
@Documentation("This component duplicates an input a configured number of times.")
public class FixedFlowInputRuntime implements Serializable {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FixedFlowInputConfiguration configuration;

    public FixedFlowInputRuntime(@Option("configuration") final FixedFlowInputConfiguration configuration) {
        this.configuration = configuration;
    }

    @Assessor
    public long estimateSize() {
        // TODO: Estimate the size of the dataset data.
        long estimatedSize = this.configuration.getDataset().getValues().getBytes().length;
        logger.info("Estimated size: " + estimatedSize);
        return estimatedSize;
    }

    @Split
    public List<FixedFlowInputRuntime> split() {
        /*
         * return LongStream.range(0, 1).mapToObj(i -> {
         * return new FixedFlowInputRuntime(configuration);
         * }).collect(Collectors.toList());
         */
        // TODO: Split the data according to a desired partition size.
        List<FixedFlowInputRuntime> mappers = new ArrayList<>();
        mappers.add(new FixedFlowInputRuntime(configuration));
        logger.info("Return the list of mappers");
        return mappers;
    }

    @Emitter
    public FixedFlowInputProducer createWorker() {
        logger.info("Create the worker");
        return new FixedFlowInputProducer(configuration);
    }

    class FixedFlowInputProducer implements Serializable {

        private FixedFlowInputConfiguration configuration;

        private BufferizedProducerSupport<IndexedRecord> bufferedReader;

        private boolean consumed = false;

        FixedFlowInputProducer(final FixedFlowInputConfiguration configuration) {
            this.configuration = configuration;
        }

        @PostConstruct
        public void init() {
            bufferedReader = new BufferizedProducerSupport<>(() -> {
                if (consumed) {
                    // TODO: fix this dirty way to stop emitting records.
                    return null;
                } else {
                    consumed = true;
                    FixedDataSetRuntime runtime = new FixedDataSetRuntime(configuration.getDataset());
                    List<IndexedRecord> values = new LinkedList<>();

                    if (configuration.getOverrideValuesAction() == FixedFlowInputConfiguration.OverrideValuesAction.NONE
                            || configuration.getOverrideValuesAction() == FixedFlowInputConfiguration.OverrideValuesAction.APPEND) {
                        if (!configuration.getDataset().getValues().trim().isEmpty()) {
                            values.addAll(runtime.getValues(Integer.MAX_VALUE));
                        }
                    }

                    if (configuration.getOverrideValuesAction() == FixedFlowInputConfiguration.OverrideValuesAction.APPEND
                            || configuration.getOverrideValuesAction() == FixedFlowInputConfiguration.OverrideValuesAction.REPLACE) {
                        configuration.getDataset().setValues(configuration.getOverrideValues());
                        if (!configuration.getDataset().getValues().trim().isEmpty()) {
                            values.addAll(runtime.getValues(Integer.MAX_VALUE));
                        }
                    }
                    logger.info("Number of records: " + String.valueOf(values.size()));
                    return values.iterator();
                }
            });
        }

        @Producer
        public Record next() {
            IndexedRecord record = bufferedReader.next();
            if (record != null) {
                return new AvroRecord(record);
            } else return null;
        }
    }
}
