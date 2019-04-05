package org.talend.components.mongodb.output.processor;

import org.talend.components.mongodb.output.OutputMapping;
import org.talend.components.mongodb.service.I18nMessage;

public interface ModelProducer<T> {

    void addField(OutputMapping mapping, String col, Object value);

    T createRecord(I18nMessage i18nMessage);
}
