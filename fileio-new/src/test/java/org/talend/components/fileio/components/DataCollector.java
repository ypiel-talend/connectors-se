package org.talend.components.fileio.components;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PostConstruct;

import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;

@Version
@Icon(Icon.IconType.SAMPLE)
@Processor(name = "DataCollector", family = "s3Test")
public class DataCollector implements Serializable {

    private static Queue<Record> data = new ConcurrentLinkedQueue<>();

    public DataCollector() {

    }

    @PostConstruct
    public void init() {

    }

    @ElementListener
    public void onElement(@Input final Record record) {
        System.out.println("got record " + record);
        data.add(record);
    }

    public static Queue<Record> getData() {
        Queue<Record> records = new ConcurrentLinkedQueue<>(data);
        return records;
    }

    public static void reset() {
        data = new ConcurrentLinkedQueue<>();
    }
}
