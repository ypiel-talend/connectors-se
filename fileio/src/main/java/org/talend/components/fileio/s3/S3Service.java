package org.talend.components.fileio.s3;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.talend.components.fileio.runtime.ExtraHadoopConfiguration;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Suggestions;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

import com.talend.shaded.com.amazonaws.AmazonServiceException;
import com.talend.shaded.com.amazonaws.auth.AWSCredentialsProviderChain;
import com.talend.shaded.com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.talend.shaded.com.amazonaws.services.s3.AmazonS3;
import com.talend.shaded.com.amazonaws.services.s3.AmazonS3Client;
import com.talend.shaded.com.amazonaws.services.s3.model.Bucket;
import com.talend.shaded.org.apache.hadoop.fs.s3a.AnonymousAWSCredentialsProvider;
import com.talend.shaded.org.apache.hadoop.fs.s3a.BasicAWSCredentialsProvider;
import com.talend.shaded.com.amazonaws.services.s3.internal.Constants;
import com.talend.shaded.org.apache.hadoop.fs.s3a.S3AEncryptionMethods;
import com.talend.shaded.org.apache.hadoop.fs.s3a.S3AFileSystem;

@Service
public class S3Service {

    @Suggestions("S3FindBuckets")
    public SuggestionValues findBuckets(@Option("datastore") final S3DataStore dataStore) {
        final AmazonS3 client = createClient(dataStore);
        return new SuggestionValues(true,
                client.listBuckets().stream().map(bucket -> new SuggestionValues.Item(bucket.getName(), bucket.getName()))
                        .sorted(comparing(SuggestionValues.Item::getLabel)).collect(toList()));
    }

    @HealthCheck("S3")
    public HealthCheckStatus healthCheckS3(@Option final S3DataStore dataStore) {
        try {
            final AmazonS3 client = createClient(dataStore);
            try {
                final List<Bucket> buckets = client.listBuckets();
                if (buckets.isEmpty()) {
                    return new HealthCheckStatus(HealthCheckStatus.Status.KO, "No bucket found");
                }
                client.listObjects(buckets.iterator().next().getName(), "any");
            } catch (final AmazonServiceException ase) {
                if (ase.getStatusCode() != Constants.NO_SUCH_BUCKET_STATUS_CODE) {
                    throw ase;
                }
            }
            return new HealthCheckStatus(HealthCheckStatus.Status.OK, "bucket found");
        } catch (final Exception e) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, e.getClass() + " : " + e.getMessage());
        }
    }

    // get the correct endpoint
    public static String getEndpoint(S3DataSet dataset) {
        String bucket = dataset.getBucket();
        S3DataStore datastore = dataset.getDatastore();
        AmazonS3 s3client = createClient(datastore);
        String bucketLocation = null;
        try {
            bucketLocation = s3client.getBucketLocation(bucket);
        } catch (IllegalArgumentException e) {
            // java.lang.IllegalArgumentException: Cannot create enum from eu-west-2 value!
            String info = e.getMessage();
            if (info == null || info.isEmpty()) {
                throw e;
            }
            Pattern regex = Pattern.compile("[a-zA-Z]+-[a-zA-Z]+-[1-9]");
            Matcher matcher = regex.matcher(info);
            if (matcher.find()) {
                bucketLocation = matcher.group(0);
            } else {
                throw e;
            }
        }
        String region = S3DataSet.S3Region.getBucketRegionFromLocation(bucketLocation);
        return S3DataSet.S3Region.regionToEndpoint(region);
    }

    public static AmazonS3 createClient(S3DataStore datastore) {
        AWSCredentialsProviderChain credentials;
        if (datastore.isSpecifyCredentials()) {
            credentials = new AWSCredentialsProviderChain(
                    new BasicAWSCredentialsProvider(datastore.getAccessKey(), datastore.getSecretKey()),
                    new DefaultAWSCredentialsProviderChain(), new AnonymousAWSCredentialsProvider());
        } else {
            // do not be polluted by hidden accessKey/secretKey
            credentials = new AWSCredentialsProviderChain(new DefaultAWSCredentialsProviderChain(),
                    new AnonymousAWSCredentialsProvider());
        }
        AmazonS3 conn = new AmazonS3Client(credentials);
        return conn;
    }

    // TODO only use for test, should move it to test package
    public static S3AFileSystem createFileSystem(S3DataSet dataset) throws IOException {
        Configuration config = new Configuration(true);
        ExtraHadoopConfiguration extraConfig = new ExtraHadoopConfiguration();
        S3Service.setS3Configuration(extraConfig, dataset);
        extraConfig.addTo(config);
        try {
            return (S3AFileSystem) FileSystem.get(
                    new URI(com.talend.shaded.org.apache.hadoop.fs.s3a.Constants.FS_S3A + "://" + dataset.getBucket()), config);
        } catch (URISyntaxException e) {
            // The URI is constant, so this exception should never occur.
            throw new RuntimeException(e);
        }
    }

    public static String getUriPath(S3DataSet dataset, String path) {
        // Construct the path using the s3a schema.
        return com.talend.shaded.org.apache.hadoop.fs.s3a.Constants.FS_S3A + "://" + dataset.getBucket() + "/" + path;
    }

    public static String getUriPath(S3DataSet dataset) {
        return getUriPath(dataset, dataset.getObject());
    }

    public static void setS3Configuration(ExtraHadoopConfiguration conf, S3DataStore datastore) {
        // Never reuse a filesystem created through this object.
        conf.set(String.format("fs.%s.impl.disable.cache", com.talend.shaded.org.apache.hadoop.fs.s3a.Constants.FS_S3A), "true");
        if (datastore.isSpecifyCredentials()) {
            // do not be polluted by hidden accessKey/secretKey
            conf.set(com.talend.shaded.org.apache.hadoop.fs.s3a.Constants.ACCESS_KEY, datastore.getAccessKey());
            conf.set(com.talend.shaded.org.apache.hadoop.fs.s3a.Constants.SECRET_KEY, datastore.getSecretKey());
        }
    }

    public static void setS3Configuration(ExtraHadoopConfiguration conf, S3DataSet dataset) {
        String endpoint = getEndpoint(dataset);
        conf.set(com.talend.shaded.org.apache.hadoop.fs.s3a.Constants.ENDPOINT, endpoint);
        // need to it?
        // conf.set("fs.s3a.experimental.input.fadvise", "random");

        if (dataset.isEncryptDataAtRest()) {
            conf.set(com.talend.shaded.org.apache.hadoop.fs.s3a.Constants.SERVER_SIDE_ENCRYPTION_ALGORITHM,
                    S3AEncryptionMethods.SSE_KMS.getMethod());
            conf.set(com.talend.shaded.org.apache.hadoop.fs.s3a.Constants.SERVER_SIDE_ENCRYPTION_KEY,
                    dataset.getKmsForDataAtRest());
        }
        if (dataset.isEncryptDataInMotion()) {
            // TODO: these don't exist yet...
            conf.set("fs.s3a.client-side-encryption-algorithm", S3AEncryptionMethods.SSE_KMS.getMethod());
            conf.set("fs.s3a.client-side-encryption-key", dataset.getKmsForDataInMotion());
        }
        setS3Configuration(conf, dataset.getDatastore());
    }
}
