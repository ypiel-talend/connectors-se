package org.talend.components.fileio.it;

import org.talend.components.fileio.configuration.CsvConfiguration;
import org.talend.components.fileio.configuration.FileFormatConfiguration;
import org.talend.components.fileio.configuration.SimpleFileIOFormat;
import org.talend.components.fileio.s3.configuration.S3DataSet;
import org.talend.components.fileio.s3.configuration.S3DataStore;

public class S3TestResource {

    public S3DataStore createS3DataStore() {
        S3DataStore dataStore = new S3DataStore();
        String s3AccessKey = System.getProperty("s3.accesskey");
        String s3SecretKey = System.getProperty("s3.secretkey");
        dataStore.setAccessKey(s3AccessKey);
        dataStore.setSecretKey(s3SecretKey);
        return dataStore;
    }

    public S3DataSet createS3DataSet() {
        S3DataSet dataSet = new S3DataSet();
        S3DataStore dataStore = createS3DataStore();
        dataSet.setDatastore(dataStore);
        String bucketName = System.getProperty("s3.bucket");
        dataSet.setBucket(bucketName);
        CsvConfiguration csvConfig = new CsvConfiguration();
        FileFormatConfiguration fileFormatConfig = new FileFormatConfiguration();
        fileFormatConfig.setFormat(SimpleFileIOFormat.CSV);
        fileFormatConfig.setCsvConfiguration(csvConfig);
        dataSet.setFileFormatConfiguration(fileFormatConfig);
        return dataSet;
    }

}
