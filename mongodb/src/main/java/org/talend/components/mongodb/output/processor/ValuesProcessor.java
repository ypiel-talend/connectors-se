package org.talend.components.mongodb.output.processor;

import lombok.extern.slf4j.Slf4j;
import org.talend.components.mongodb.output.OutputMapping;

@Slf4j
public class ValuesProcessor<T> {

    private ModelProducer<T> producer;

    private ModelWriter<T> writer;

    public ValuesProcessor(ModelProducer<T> producer, ModelWriter<T> writer) {
        this.producer = producer;
        this.writer = writer;
    }

    public void processField(OutputMapping mapping, String col, Object value) {
        producer.addField(mapping, col, value);
    }

    private T createModel() {
        return producer.createRecord();
    }

    public void finalizeRecord() {
        T model = createModel();
        if (model != null) {
            writer.putModel(model);
        } else {
            log.warn("Empty record received.");
        }
    }

    public void flush() {
        writer.flush();
    }

    public void close() {
        writer.close();
    }

}
