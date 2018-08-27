package org.talend.components.fileio.s3;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.talend.components.simplefileio.runtime.s3.S3Connection;
import org.talend.components.simplefileio.s3.S3Region;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Suggestions;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.internal.Constants;
import com.amazonaws.services.s3.model.Bucket;

@Service
public class S3Service {

    @Suggestions("S3FindBuckets")
    public SuggestionValues findBuckets(@Option("datastore") final S3DataStore dataStore) {
        final AmazonS3 client = createClient(dataStore);
        try {
            return new SuggestionValues(true,
                    client.listBuckets().stream().map(bucket -> new SuggestionValues.Item(bucket.getName(), bucket.getName()))
                            .sorted(comparing(SuggestionValues.Item::getLabel)).collect(toList()));
        } finally {
            client.shutdown();
        }
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
            } finally {
                client.shutdown();
            }
            return new HealthCheckStatus(HealthCheckStatus.Status.OK, "bucket found");
        } catch (final Exception e) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, e.getClass() + " : " + e.getMessage());
        }
    }

    public static AmazonS3 createClient(final S3DataStore datastore) {
        final AWSCredentialsProviderChain credentials = datastore.isSpecifyCredentials()
                ? new AWSCredentialsProviderChain(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(datastore.getAccessKey(), datastore.getSecretKey())),
                        new DefaultAWSCredentialsProviderChain(), new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                : new AWSCredentialsProviderChain(new DefaultAWSCredentialsProviderChain(),
                        new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()));
        return new AmazonS3Client(credentials);
    }

    // get the correct endpoint
    private static String getEndpoint(S3DataSet dataset) {
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
}
