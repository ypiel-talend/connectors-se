/*
 * Copyright (C) 2006-2018 Talend Inc. - www.talend.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.talend.components.fileio.s3;

import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;
import static org.talend.sdk.component.runtime.manager.ComponentManager.ComponentType.MAPPER;
import static org.talend.sdk.component.runtime.manager.ComponentManager.ComponentType.PROCESSOR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.json.JsonObject;

import com.talend.shaded.com.amazonaws.auth.AWSCredentialsProviderChain;
import com.talend.shaded.com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.talend.shaded.com.amazonaws.services.s3.AmazonS3;
import com.talend.shaded.com.amazonaws.services.s3.AmazonS3Client;
import com.talend.shaded.com.amazonaws.services.s3.model.Bucket;
import com.talend.shaded.com.amazonaws.services.s3.model.ObjectListing;
import com.talend.shaded.com.amazonaws.services.s3.model.S3ObjectSummary;
import com.talend.shaded.org.apache.hadoop.fs.s3a.AnonymousAWSCredentialsProvider;
import com.talend.shaded.org.apache.hadoop.fs.s3a.BasicAWSCredentialsProvider;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.talend.components.adapter.beam.transform.ConvertToIndexedRecord;
import org.talend.components.fileio.configuration.FieldDelimiterType;
import org.talend.components.fileio.configuration.RecordDelimiterType;
import org.talend.components.fileio.configuration.SimpleFileIOFormat;
import org.talend.sdk.component.api.DecryptedServer;
import org.talend.sdk.component.junit.ComponentsHandler;
import org.talend.sdk.component.junit.http.api.HttpApiHandler;
import org.talend.sdk.component.junit.http.junit5.HttpApi;
import org.talend.sdk.component.junit.http.junit5.HttpApiInject;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.junit5.WithMavenServers;
import org.talend.sdk.component.maven.Server;
import org.talend.sdk.component.runtime.beam.impl.CapturingPipeline;
import org.talend.sdk.component.runtime.beam.transform.avro.IndexedRecordToJson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@WithMavenServers
@WithComponents("org.talend.components.fileio")
@HttpApi(useSsl = true, headerFilter = HttpProxyHeaderFilter.class, logLevel = "ERROR") //
class S3InputTest {

    static {
        // System.setProperty("talend.junit.http.capture", "true");
    }

    private static final String BUCKET_NAME_FORMAT = "-tacokit-test-bucket-%s";

    private static final String DATASET_NAME_FORMAT = "-tacokit-test-dataset-%s";

    @Injected
    private ComponentsHandler handler;

    @HttpApiInject
    private HttpApiHandler<?> httpApiHandler;

    @DecryptedServer(value = "aws-s3", alwaysTryLookup = true)
    private Server s3Credentials;

    private String bucketName;

    private String dataSetName;

    private AmazonS3 client;

    private final S3DataStore dataStore = new S3DataStore();

    @BeforeAll
    static void beforeAll() {
        System.setProperty("com.talend.shaded.com.amazonaws.sdk.disableCertChecking", "true");
    }

    @AfterAll
    static void afterAll() {
        System.setProperty("com.talend.shaded.com.amazonaws.sdk.disableCertChecking", "false");
    }

    @BeforeEach
    void init() {
        dataStore.setSpecifyCredentials(true);
        dataStore.setAccessKey(s3Credentials.getUsername());
        dataStore.setSecretKey(s3Credentials.getPassword());
        final AWSCredentialsProviderChain credentials = new AWSCredentialsProviderChain(
                new BasicAWSCredentialsProvider(dataStore.getAccessKey(), dataStore.getSecretKey()),
                new DefaultAWSCredentialsProviderChain(), new AnonymousAWSCredentialsProvider());
        client = new AmazonS3Client(credentials);
        client.setEndpoint(S3DataSet.S3Region.DEFAULT.toEndpoint());
        final String uuid = "37917";// UUID.randomUUID().toString().substring(0, 5);
        bucketName = String.format(uuid + BUCKET_NAME_FORMAT, S3DataSet.S3Region.DEFAULT.getValue());
        final Bucket bucket = client.createBucket(bucketName);
        if (bucket == null) {
            fail("Can not create s3 bucket for this test");
        } else {
            log.info("S3 Bucket created " + bucket);
        }

        // output test
        final S3OutputDataSet dataSet = new S3OutputDataSet();
        dataSet.setDatastore(dataStore);
        dataSet.setOverwrite(true);
        dataSet.setBucket(bucketName);
        dataSet.setLimit(10);
        dataSet.setFormat(SimpleFileIOFormat.CSV);
        dataSet.setFieldDelimiter(FieldDelimiterType.COMMA);
        dataSet.setRecordDelimiter(RecordDelimiterType.LF);
        dataSet.setRegion(S3DataSet.S3Region.DEFAULT);
        dataSetName = String.format(uuid + DATASET_NAME_FORMAT, S3DataSet.S3Region.DEFAULT.getValue());
        dataSet.setObject(dataSetName);

        // Beam pipeline
        final Map<String, String> config = configurationByExample().forInstance(dataSet).configured().toMap();
        final CapturingPipeline.TransformWithCoder output = handler.asManager()
                .createComponent("FileIO", "S3Output", PROCESSOR, 1, config).map(e -> (CapturingPipeline.TransformWithCoder) e)
                .orElseThrow(() -> new IllegalArgumentException("No S3Output component found"));

        final Pipeline pipeline = Pipeline.create();
        final Create.Values<IndexedRecord> inMemoryValues = Create.of(
                ConvertToIndexedRecord.convertToAvro(new String[] { "1", "user1" }),
                ConvertToIndexedRecord.convertToAvro(new String[] { "2", "user2" }),
                ConvertToIndexedRecord.convertToAvro(new String[] { "3", "user3" }));

        if (output.getCoders() != null) {
            output.getCoders().entrySet().stream().filter(e -> e.getValue() != null)
                    .forEach(e -> pipeline.getCoderRegistry().registerCoderForType(e.getKey().getTypeDescriptor(), e.getValue()));
        }
        pipeline.apply(inMemoryValues).apply(output.getTransform());
        pipeline.run().waitUntilFinish();
    }

    @Test
    void readCreatedObjectFromBucket() {
        final S3DataSet ds = new S3DataSet();
        ds.setDatastore(dataStore);
        ds.setBucket(bucketName);
        ds.setObject(dataSetName);
        ds.setRegion(S3DataSet.S3Region.DEFAULT);
        ds.setLimit(10);
        ds.setFieldDelimiter(FieldDelimiterType.COMMA);
        ds.setFormat(SimpleFileIOFormat.CSV);
        ds.setRecordDelimiter(RecordDelimiterType.LF);
        final Map<String, String> config = configurationByExample().forInstance(ds).configured().toMap();
        final Pipeline pipeline = Pipeline.create();
        final PTransform<PBegin, PCollection<IndexedRecord>> processor = handler.asManager()
                .createComponent("FileIO", "S3Input", MAPPER, 1, config)
                .map(e -> (PTransform<PBegin, PCollection<IndexedRecord>>) e)
                .orElseThrow(() -> new IllegalArgumentException("No component found"));
        final PCollection<JsonObject> result = pipeline.apply("INPUT", processor).apply("IndexRecordToJsonOBject",
                new IndexedRecordToJson());
        PAssert.that(result).satisfies((SerializableFunction<Iterable<JsonObject>, Void>) input -> {
            List<JsonObject> records = new ArrayList<>();
            input.iterator().forEachRemaining(records::add);
            assertEquals(3, records.size());
            assertEquals("user1,user2,user3", records.stream().map(s -> s.getString("field1")).sorted().collect(joining(",")));
            return null;
        });
        pipeline.run().waitUntilFinish();
    }

    @AfterEach
    void clear() {
        if (client != null) {
            ObjectListing objectListing = client.listObjects(bucketName);
            while (true) {
                for (final S3ObjectSummary s3ObjectSummary : objectListing.getObjectSummaries()) {
                    client.deleteObject(bucketName, s3ObjectSummary.getKey());
                }
                // If the bucket contains many objects, the listObjects() call
                // might not return all of the objects in the first listing. Check to
                // see whether the listing was truncated. If so, retrieve the next page of objects
                // and delete them.
                if (objectListing.isTruncated()) {
                    objectListing = client.listNextBatchOfObjects(objectListing);
                } else {
                    break;
                }
            }
            // After all objects and object versions are deleted, delete the bucket.
            client.deleteBucket(bucketName);
        }
    }

}
