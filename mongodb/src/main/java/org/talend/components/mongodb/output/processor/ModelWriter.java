package org.talend.components.mongodb.output.processor;

public interface ModelWriter<T> {

    void putModel(T model);

    void flush();

    void close();

}
