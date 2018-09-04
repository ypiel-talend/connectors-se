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
package org.talend.components.fileio.s3;

import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.talend.shaded.com.amazonaws.services.s3.model.ObjectMetadata;
import com.talend.shaded.org.apache.hadoop.fs.s3a.S3AFileSystem;

/**
 * Reusable for creating S3 properties preconfigured from environment variables for integration tests.
 *
 * <pre>
 * <profile>
 *   <id>amazon_credentials</id>
 *   <properties>
 *     <s3.accesskey>ACCESS_KEY</s3.accesskey>
 *     <s3.secretkey>SECRETY_KEY</s3.secretkey>
 *     <s3.bucket>testbucket</s3.bucket>
 *     <s3.ssekmskey>KEY1</s3.ssekmskey>
 *     <s3.csekmskey>KEY2</s3.csekmskey>
 *   </properties>
 *   <activation>
 *     <activeByDefault>true</activeByDefault>
 *   </activation>
 * </profile>
 * </pre>
 */
public class S3TestResource extends ExternalResource {

    /** The currently running test. */
    protected String name = null;

    /** The output path used for the currently running test. */
    protected String path = null;

    private String bucketName;

    private S3TestResource() {
        bucketName = System.getProperty("s3.bucket");
    }

    public static S3TestResource of() {
        return new S3TestResource();
    }

    public String getBucketName() {
        return bucketName;
    }

    public S3DataStore createS3Datastore() {
        S3DataStore datastore = new S3DataStore();
        String s3AccessKey = System.getProperty("s3.accesskey");
        String s3SecretKey = System.getProperty("s3.secretkey");
        // If we are running in an environment without specified keys, then don't use them.
        if (StringUtils.isEmpty(s3AccessKey) || StringUtils.isEmpty(s3SecretKey)) {
            datastore.setSpecifyCredentials(false);
        } else {
            datastore.setAccessKey(System.getProperty("s3.accesskey"));
            datastore.setSecretKey(System.getProperty("s3.secretkey"));
        }
        return datastore;
    }

    /**
     * @return An S3DatasetProperties with credentials in the datastore, not configured for encryption. The
     * bucket are taken from the environment, and a unique "object" property is created for this unit test.
     */
    public S3DataSet createS3DataSet() {
        return createS3Dataset(false, false);
    }

    /**
     * Return an S3DataSet potentially configured for encryption.
     * 
     * @param sseKms Whether server-side encryption is used. The KMS key is taken from the system environment.
     * @param sseKms Whether client-side encryption is used. The KMS key is taken from the system environment.
     * @return An S3DataSet with credentials in the datastore, configured for the specified encryption. The
     * bucket are taken from the environment, and a unique "object" property is created for this unit test.
     */
    public S3DataSet createS3Dataset(boolean sseKms, boolean cseKms) {
        S3DataSet dataset = new S3DataSet();
        dataset.setBucket(bucketName);
        dataset.setObject(getPath());
        dataset.setDatastore(createS3Datastore());
        if (sseKms) {
            dataset.setEncryptDataAtRest(true);
            dataset.setKmsForDataAtRest(System.getProperty("s3.ssekmskey"));
        }
        if (cseKms) {
            dataset.setEncryptDataInMotion(true);
            dataset.setKmsForDataInMotion(System.getProperty("s3.csekmskey"));
        }
        return dataset;
    }

    /**
     * Get the ObjectMetadata from S3 for the first file found on the path specified by the S3DatasetProperties.
     */
    public ObjectMetadata getObjectMetadata(S3DataSet dataset) throws IOException {
        S3AFileSystem fs = S3Service.createFileSystem(dataset);

        // The current path is a directory, so get a file to check the encryption.
        Path path = new Path(S3Service.getUriPath(dataset));
        FileStatus[] files = fs.listStatus(path);
        assertThat(files, arrayWithSize(greaterThan(0)));
        return fs.getObjectMetadata(files[0].getPath());
    }

    /**
     * Return the name of the currently executing test.
     *
     * @return the name of the currently executing test.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the output path for the currently executing test.
     *
     * @return the output path for the currently executing test.
     */
    public String getPath() {
        return path;
    }

    public String getS3APath(S3DataSet dataset) {
        return S3Service.getUriPath(dataset, getPath());
    }

    @Override
    public Statement apply(Statement base, Description desc) {
        name = desc.getMethodName();
        path = "integration-test/" + getName() + "_" + UUID.randomUUID();
        return super.apply(base, desc);
    }

    /**
     * Remove any resources created on the S3 bucket.
     */
    @Override
    protected void after() {
        try {
            S3DataSet dataset = createS3DataSet();
            S3AFileSystem fs = S3Service.createFileSystem(dataset);
            Path path = new Path(S3Service.getUriPath(dataset));
            if (fs.exists(path)) {
                fs.delete(path, true);
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
