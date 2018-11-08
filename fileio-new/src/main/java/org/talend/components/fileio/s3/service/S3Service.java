package org.talend.components.fileio.s3.service;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.talend.components.fileio.s3.configuration.S3DataSet;
import org.talend.components.fileio.s3.configuration.S3DataStore;
import org.talend.components.fileio.s3.configuration.S3Region;
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
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.internal.Constants;
import com.amazonaws.services.s3.model.Bucket;

@Service
public class S3Service {

    @Suggestions("S3FindBuckets")
    public SuggestionValues findBuckets(@Option("datastore") final S3DataStore dataStore) {
        final AmazonS3 client = createClient(dataStore);
        try {
            SuggestionValues sv = new SuggestionValues(true,
                    client.listBuckets().stream().map(bucket -> new SuggestionValues.Item(bucket.getName(), bucket.getName()))
                            .sorted(comparing(SuggestionValues.Item::getLabel)).collect(toList()));
            return sv;
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
            } catch (final AmazonServiceException ase) {
                if (ase.getStatusCode() != Constants.NO_SUCH_BUCKET_STATUS_CODE) {
                    throw ase;
                }
            } finally {
                client.shutdown();
            }
            return new HealthCheckStatus(HealthCheckStatus.Status.OK, "Connection successful");
        } catch (final Exception e) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, e.getClass() + " : " + e.getMessage());
        }
    }

    public AmazonS3 createClient(final S3DataStore datastore) {
        return this.createClient(datastore, null);
    }

    public AmazonS3 createClientForBucket(final S3DataSet dataset) {
        S3Region region = getRegion(dataset);
        return createClient(dataset.getDatastore(), region.getValue());
    }

    public S3Region getRegion(S3DataSet dataset) {
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
        } finally {
            s3client.shutdown();
        }
        return S3Region.fromLocation(bucketLocation);
    }

    public AmazonS3 createClient(final S3DataStore datastore, final String region) {
        Regions curRegion;
        if (region == null) {
            curRegion = Regions.fromName(S3Region.AP_SOUTHEAST_1.getValue());
        } else {
            curRegion = Regions.fromName(region);
        }
        final AWSCredentialsProviderChain credentials = datastore.isSpecifyCredentials()
                ? new AWSCredentialsProviderChain(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(datastore.getAccessKey(), datastore.getSecretKey())),
                        new DefaultAWSCredentialsProviderChain(), new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                : new AWSCredentialsProviderChain(new DefaultAWSCredentialsProviderChain(),
                        new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()));
        return AmazonS3ClientBuilder.standard().withRegion(curRegion).withCredentials(credentials).build();
    }

}
