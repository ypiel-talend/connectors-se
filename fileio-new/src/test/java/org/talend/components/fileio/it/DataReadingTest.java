package org.talend.components.fileio.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

import java.util.Queue;

import org.junit.Test;
import org.talend.components.fileio.components.DataCollector;
import org.talend.components.fileio.s3.configuration.S3DataSet;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.manager.chain.Job;

@WithComponents("org.talend.components.fileio")
public class DataReadingTest {

    private static final S3TestResource testResource = new S3TestResource();

    @Test
    public void testRead() throws Exception {
        S3DataSet dataSet = testResource.createS3DataSet();
        dataSet.setObject("QuotesStressTest.csv");
        String inputConfig = configurationByExample().forInstance(dataSet).configured().toQueryString();

        Job.components().component("nsEmitter", "FileIO://S3Input?" + inputConfig).component("collect", "s3Test://DataCollector")
                .connections().from("nsEmitter").to("collect").build().run();

        Queue<Record> records = DataCollector.getData();
        int actualSize = records.size();
        System.out.println(actualSize);
        assertEquals(28, actualSize);
    }

}
