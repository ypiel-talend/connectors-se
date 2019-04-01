package org.talend.components.mongodb.output.processor;

import org.talend.components.mongodb.output.OutputMapping;

public interface ModelProducer<T> {

    void addField(OutputMapping mapping, String col, Object value);

    T createRecord();
}
