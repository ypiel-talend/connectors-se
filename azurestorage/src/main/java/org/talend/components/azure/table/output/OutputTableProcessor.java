package org.talend.components.azure.table.output;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.azure.service.AzureConnectionUtils;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.AfterGroup;
import org.talend.sdk.component.api.processor.BeforeGroup;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Output;
import org.talend.sdk.component.api.processor.OutputEmitter;
import org.talend.sdk.component.api.processor.Processor;

import org.talend.components.azure.service.AzureConnectionService;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageErrorCodeStrings;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.DynamicTableEntity;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableServiceException;

@Version(1) // default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Icon(value = Icon.IconType.CUSTOM, custom = "outputTable") // you can use a custom one using @Icon(value=CUSTOM,
                                                            // custom="filename") and adding icons/filename_icon32.png in
                                                            // resources
@Processor(name = "OutputTable")
@Documentation("Azure Output Table Component")
public class OutputTableProcessor implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(OutputTableProcessor.class);

    private final OutputTableProcessorConfiguration configuration;

    private final AzureConnectionService service;

    private transient CloudStorageAccount connection;

    private transient List<Record> recordToEnqueue = new ArrayList<>();

    public OutputTableProcessor(@Option("configuration") final OutputTableProcessorConfiguration configuration,
            final AzureConnectionService service) {
        this.configuration = configuration;
        this.service = service;
    }

    @PostConstruct
    public void init() throws Exception {
        try {
            connection = service.createStorageAccount(configuration.getAzureConnection().getConnection());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Can't establish connection", e);
        }

        handleActionOnTable();
    }

    public void handleActionOnTable()
            throws IOException, StorageException, InvalidKeyException, URISyntaxException {
        CloudTable cloudTable = connection.createCloudTableClient().getTableReference(configuration.getAzureConnection().getTableName());
        switch (configuration.getActionOnTable()) {
        case CREATE:
            cloudTable.create(null, AzureConnectionUtils.getTalendOperationContext());
            break;
        case CREATE_IF_NOT_EXIST:
            cloudTable.createIfNotExists(null, AzureConnectionUtils.getTalendOperationContext());
            break;
        case DROP_AND_CREATE:
            cloudTable.delete(null, AzureConnectionUtils.getTalendOperationContext());
            createTableAfterDeletion(cloudTable);
            break;
        case DROP_IF_EXIST_CREATE:
            cloudTable.deleteIfExists(null, AzureConnectionUtils.getTalendOperationContext());
            createTableAfterDeletion(cloudTable);
            break;
        case DEFAULT:
        default:
            return;
        }

    }

    @ElementListener
    public void onNext(@Input final Record incomingRecord) throws Exception {
        if (incomingRecord == null) {
            return;
        }
        Schema writeSchema = incomingRecord.getSchema();
        if (configuration.isProcessInBatch()) {
            DynamicTableEntity entity = createDynamicEntityFromInputRecord(incomingRecord, writeSchema);
            addOperationToBatch(entity, incomingRecord);
        } else {
            recordToEnqueue.add(incomingRecord);
           /* if (recordToEnqueue.size() >= MAX_RECORDS_TO_ENQUEUE) {
                processParallelRecords();
            }*/
        }
        System.out.println(incomingRecord.toString());
    }

    private DynamicTableEntity createDynamicEntityFromInputRecord(Record incomingRecord, Schema writeSchema) {
        return null;
    }

    private void addOperationToBatch(DynamicTableEntity entity, Record record) throws IOException {
        /*if (latestPartitionKey == null || latestPartitionKey.isEmpty()) {
            latestPartitionKey = entity.getPartitionKey();
        }
        // we reached the threshold for batch OR changed PartitionKey
        if (batchOperationsCount == 100 || !entity.getPartitionKey().equals(latestPartitionKey)) {
            processBatch();
            latestPartitionKey = entity.getPartitionKey();
        }
        TableOperation to = getTableOperation(entity);
        batchOperations.add(to);
        batchRecords.add(record);
        batchOperationsCount++;
        latestPartitionKey = entity.getPartitionKey();*/
    }

    @PreDestroy
    public void release() {
        // NOOP
    }

    /**
     * This method create a table after it's deletion.<br/>
     * the table deletion take about 40 seconds to be effective on azure CF.
     * https://docs.microsoft.com/en-us/rest/api/storageservices/fileservices/Delete-Table#Remarks <br/>
     * So we try to wait 50 seconds if the first table creation return an
     * {@link StorageErrorCodeStrings.TABLE_BEING_DELETED } exception code
     *
     * @param cloudTable
     *
     * @throws StorageException
     * @throws IOException
     *
     */
    private void createTableAfterDeletion(CloudTable cloudTable) throws StorageException, IOException {
        try {
            cloudTable.create(null, AzureConnectionUtils.getTalendOperationContext());
        } catch (TableServiceException e) {
            if (!e.getErrorCode().equals(StorageErrorCodeStrings.TABLE_BEING_DELETED)) {
                throw e;
            }
            LOGGER.warn("Table '{}' is currently being deleted. We'll retry in a few moments...", cloudTable.getName());
            // wait 50 seconds (min is 40s) before retrying.
            // See https://docs.microsoft.com/en-us/rest/api/storageservices/fileservices/Delete-Table#Remarks
            try {
                Thread.sleep(50000);
            } catch (InterruptedException eint) {
                throw new IOException("Wait process for recreating table interrupted.");
            }
            cloudTable.create(null, AzureConnectionUtils.getTalendOperationContext());
            LOGGER.debug("Table {} created.", cloudTable.getName());
        }
    }
}