package org.talend.components.couchbase.source;

import com.couchbase.client.dcp.message.DcpDeletionMessage;
import com.couchbase.client.dcp.message.DcpExpirationMessage;
import com.couchbase.client.dcp.message.DcpMutationMessage;
import com.couchbase.client.deps.io.netty.buffer.ByteBuf;
import com.couchbase.client.deps.io.netty.util.CharsetUtil;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.RawJsonDocument;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.couchbase.service.CouchbaseService;
import org.talend.components.couchbase.service.CouchbaseStreamingConnection;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.talend.sdk.component.api.record.Schema.Type.*;

@Slf4j
@Documentation("TODO fill the documentation for this source")
public class CouchbaseInputSource implements Serializable {

    private transient static final Logger LOG = LoggerFactory.getLogger(CouchbaseInputSource.class);

    private final CouchbaseInputMapperConfiguration configuration;

    private final CouchbaseService service;

    private final RecordBuilderFactory builderFactory;

    private transient Schema schema;

    private volatile BlockingQueue<ByteBuf> resultsQueue;

    private CouchbaseStreamingConnection connection;

    public CouchbaseInputSource(@Option("configuration") final CouchbaseInputMapperConfiguration configuration,
            final CouchbaseService service, final RecordBuilderFactory builderFactory) {
        this.configuration = configuration;
        this.service = service;
        this.builderFactory = builderFactory;
    }

    @PostConstruct
    public void init() {

        String bootStrapNodes = configuration.getDataSet().getDatastore().getBootstrapNodes();
        String bucketName = configuration.getDataSet().getDatastore().getBucket();
        String password = configuration.getDataSet().getDatastore().getPassword();

        connection = new CouchbaseStreamingConnection(bootStrapNodes, bucketName, password);
        connection.connect();
        resultsQueue = new LinkedBlockingQueue<>();
        connection.startStreaming(resultsQueue);
    }

    @Producer
    public Record next() {
        // this is the method allowing you to go through the dataset associated
        // to the component configuration
        //
        // return null means the dataset has no more data to go through
        // you can use the builderFactory to create a new Record.

        while (true) {
            final ByteBuf event;
            final Record record;
            try {
                event = resultsQueue.poll(1, TimeUnit.SECONDS);

                if (event != null) {
                    record = createRecord(event);
                    connection.acknowledge(event);
                    event.release();
                    return record;
                }
            } catch (InterruptedException e) {
                LOG.error("Failed to poll event from the results queue", e);
            }
            if (!connection.isStreaming() && resultsQueue.isEmpty()) {
                break;
            }
        }
        return null;
    }

    @PreDestroy
    public void release() {
        connection.stopStreaming();
        connection.disconnect();

    }

    private Record createRecord(final ByteBuf value) {
        if (schema == null) {
            final Schema.Builder schemaBuilder = builderFactory.newSchemaBuilder(RECORD);
            final Schema.Entry.Builder entryBuilder = builderFactory.newEntryBuilder();

            schemaBuilder.withEntry(entryBuilder.withType(STRING).build());
            schemaBuilder.withEntry(entryBuilder.withType(LONG).build());
            schemaBuilder.withEntry(entryBuilder.withType(STRING).build());
            schemaBuilder.withEntry(entryBuilder.withType(INT).build());
            schemaBuilder.withEntry(entryBuilder.withType(LONG).build());
            schemaBuilder.withEntry(entryBuilder.withType(LONG).build());
            schemaBuilder.withEntry(entryBuilder.withType(INT).build());
            schemaBuilder.withEntry(entryBuilder.withType(INT).build());
            schemaBuilder.withEntry(entryBuilder.withType(INT).build());
            schemaBuilder.withEntry(entryBuilder.withType(BYTES).build());

            schema = schemaBuilder.build();
        }

        final Record.Builder recordBuilder = builderFactory.newRecordBuilder();

        final String key;
        final long seqno;
        final String event;
        final short partition;
        final long cas;
        final long revSeqno;
        final int expiration;
        final int flags;
        final int lockTime;
        final byte[] content;

        if (DcpMutationMessage.is(value)) {
            key = bufToString(DcpMutationMessage.key(value));
            seqno = DcpMutationMessage.bySeqno(value);
            event = "mutation";
            partition = DcpMutationMessage.partition(value);
            cas = DcpMutationMessage.cas(value);
            revSeqno = DcpMutationMessage.revisionSeqno(value);
            expiration = DcpMutationMessage.expiry(value);
            flags = DcpMutationMessage.flags(value);
            lockTime = DcpMutationMessage.lockTime(value);
            content = bufToBytes(DcpMutationMessage.content(value));
        } else if (DcpDeletionMessage.is(value)) {
            key = bufToString(DcpDeletionMessage.key(value));
            seqno = DcpDeletionMessage.bySeqno(value);
            event = "deletion";
            partition = DcpDeletionMessage.partition(value);
            cas = DcpDeletionMessage.cas(value);
            revSeqno = DcpDeletionMessage.revisionSeqno(value);
            expiration = 0;
            flags = 0;
            lockTime = 0;
            content = null;
        } else if (DcpExpirationMessage.is(value)) {
            key = bufToString(DcpExpirationMessage.key(value));
            seqno = DcpExpirationMessage.bySeqno(value);
            event = "expiration";
            partition = DcpExpirationMessage.partition(value);
            cas = DcpExpirationMessage.cas(value);
            revSeqno = DcpExpirationMessage.revisionSeqno(value);
            expiration = 0;
            flags = 0;
            lockTime = 0;
            content = null;
        } else {
            throw new IllegalArgumentException("Unexpected value type: " + value.getByte(1));
        }

        recordBuilder.withString("key", key);
        recordBuilder.withLong("seqno", seqno);
        recordBuilder.withString("event", event);
        recordBuilder.withInt("partition", partition);
        recordBuilder.withLong("cas", cas);
        recordBuilder.withLong("revSeqno", revSeqno);
        recordBuilder.withInt("expiration", expiration);
        recordBuilder.withInt("flags", flags);
        recordBuilder.withInt("lockTime", lockTime);
        recordBuilder.withBytes("content", content);

        return recordBuilder.build();
    }

    private static String bufToString(ByteBuf buf) {
        return new String(bufToBytes(buf), CharsetUtil.UTF_8);
    }

    private static byte[] bufToBytes(ByteBuf buf) {
        byte[] bytes;
        bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        return bytes;
    }
}