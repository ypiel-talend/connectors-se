package org.talend.components.mongodb.output.processor;

import lombok.extern.slf4j.Slf4j;
import org.talend.components.mongodb.output.OutputMapping;
import org.talend.components.mongodb.service.I18nMessage;

@Slf4j
public class ValuesProcessor<T> {

    private final I18nMessage i18n;

    private final ModelProducer<T> producer;

    private final ModelWriter<T> writer;

    public ValuesProcessor(ModelProducer<T> producer, ModelWriter<T> writer, I18nMessage i18n) {
        this.producer = producer;
        this.writer = writer;
        this.i18n = i18n;
    }

    public void processField(OutputMapping mapping, String col, Object value) {
        log.debug(i18n.addingField(col, value));
        producer.addField(mapping, col, value);
    }

    private T createModel() {
        return producer.createRecord(i18n);
    }

    public void finalizeRecord() {
        T model = createModel();
        if (model != null) {
            log.debug(i18n.createdRecord(String.valueOf(model)));
            writer.putModel(model);
        } else {
            log.warn(i18n.emptyRecord());
        }
    }

    public void flush() {
        writer.flush();
    }

    public void close() {
        writer.close();
    }

}
