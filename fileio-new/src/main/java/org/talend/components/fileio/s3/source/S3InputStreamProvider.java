package org.talend.components.fileio.s3.source;

import java.io.IOException;
import java.io.InputStream;

import org.talend.components.fileio.input.InputStreamProvider;
import org.talend.components.fileio.s3.configuration.S3DataSet;
import org.talend.components.fileio.s3.service.S3Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class S3InputStreamProvider implements InputStreamProvider {

    private AmazonS3 s3Connection;

    private final S3DataSet configuration;

    private InputStream stream;

    public S3InputStreamProvider(final S3DataSet configuration, final S3Service service) {
        this.configuration = configuration;
        this.s3Connection = service.createClientForBucket(configuration);
    }

    @Override
    public void close() throws IOException {
        if (stream != null) {
            stream.close();
        }
    }

    @Override
    public InputStream getInputStream() {
        if (stream == null) {
            stream = createStream(s3Connection, configuration);
        }
        return stream;
    }

    private InputStream createStream(AmazonS3 s3Connection, S3DataSet configuration) {
        S3Object object = s3Connection.getObject(new GetObjectRequest(configuration.getBucket(), configuration.getObject()));
        return object.getObjectContent();
    }

}
