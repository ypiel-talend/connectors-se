package org.talend.components.azure.table.output;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.azure.common.NameMapping;
import org.talend.components.azure.service.AzureConnectionService;
import org.talend.components.azure.service.AzureTableUtils;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageErrorCodeStrings;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.DynamicTableEntity;
import com.microsoft.azure.storage.table.EntityProperty;
import com.microsoft.azure.storage.table.TableBatchOperation;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableResult;
import com.microsoft.azure.storage.table.TableServiceException;

@Version(1) // default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Icon(value = Icon.IconType.CUSTOM, custom = "outputTable") // you can use a custom one using @Icon(value=CUSTOM,
// custom="filename") and adding icons/filename_icon32.png in
// resources
@Processor(name = "OutputTable")
@Documentation("Azure Output Table Component")
public class OutputTableProcessor implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutputTableProcessor.class);

    private final OutputProperties configuration;

    private final AzureConnectionService service;

    private transient CloudStorageAccount connection;

    private transient List<Record> recordToEnqueue = new ArrayList<>();

    private transient List<TableOperation> batchOperations = new ArrayList<>();

    private transient int batchOperationsCount;

    private transient String latestPartitionKey;

    private static final int MAX_RECORDS_TO_ENQUEUE = 250;

    public OutputTableProcessor(@Option("configuration") final OutputProperties configuration,
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

    @ElementListener
    public void onNext(@Input final Record incomingRecord) throws Exception {
        if (incomingRecord == null) {
            return;
        }
        if (configuration.isProcessInBatch()) {
            DynamicTableEntity entity = createDynamicEntityFromInputRecord(incomingRecord);
            addOperationToBatch(entity, incomingRecord);
        } else {
            recordToEnqueue.add(incomingRecord);
            if (recordToEnqueue.size() >= MAX_RECORDS_TO_ENQUEUE) {
                processParallelRecords();
            }
        }
    }

    @PreDestroy
    public void release() {
        if (batchOperationsCount > 0) {
            LOGGER.debug("Executing last batch");
            processBatch();
        }

        if (recordToEnqueue.size() > 0) {
            processParallelRecords();
        }
    }

    private void processParallelRecords() {
        recordToEnqueue.parallelStream().forEach(record -> {
            try {
                DynamicTableEntity entity = createDynamicEntityFromInputRecord(record);
                TableResult r = executeOperation(getTableOperation(entity));
            } catch (StorageException e) {
                // TODO log message
                e.printStackTrace();
                if (configuration.isDieOnError()) {
                    throw new RuntimeException(e);
                }

            } catch (URISyntaxException e) {
                throw new RuntimeException(e); // connection problem so next operation will also fail, we stop the process
            }
        });
        recordToEnqueue.clear();
    }

    /**
     * This method create a table after it's deletion.<br/>
     * the table deletion take about 40 seconds to be effective on azure CF.
     * https://docs.microsoft.com/en-us/rest/api/storageservices/fileservices/Delete-Table#Remarks <br/>
     * So we try to wait 50 seconds if the first table creation return an
     * {@link StorageErrorCodeStrings.TABLE_BEING_DELETED } exception code
     *
     * @param cloudTable
     * @throws StorageException
     * @throws IOException
     */
    private void createTableAfterDeletion(CloudTable cloudTable) throws StorageException, IOException {
        try {
            cloudTable.create(null, AzureTableUtils.getTalendOperationContext());
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
            cloudTable.create(null, AzureTableUtils.getTalendOperationContext());
            LOGGER.debug("Table {} created.", cloudTable.getName());
        }
    }

    private void handleActionOnTable() throws IOException, StorageException, URISyntaxException {
        CloudTable cloudTable = connection.createCloudTableClient()
                .getTableReference(configuration.getAzureConnection().getTableName());
        switch (configuration.getActionOnTable()) {
        case CREATE:
            cloudTable.create(null, AzureTableUtils.getTalendOperationContext());
            break;
        case CREATE_IF_NOT_EXIST:
            cloudTable.createIfNotExists(null, AzureTableUtils.getTalendOperationContext());
            break;
        case DROP_AND_CREATE:
            cloudTable.delete(null, AzureTableUtils.getTalendOperationContext());
            createTableAfterDeletion(cloudTable);
            break;
        case DROP_IF_EXIST_CREATE:
            cloudTable.deleteIfExists(null, AzureTableUtils.getTalendOperationContext());
            createTableAfterDeletion(cloudTable);
            break;
        case DEFAULT:
        default:
            return;
        }

    }

    private ArrayList<TableResult> executeOperation(TableBatchOperation batchOpe) throws URISyntaxException, StorageException {

        CloudTable cloudTable = connection.createCloudTableClient()
                .getTableReference(configuration.getAzureConnection().getTableName());
        return cloudTable.execute(batchOpe, null, AzureTableUtils.getTalendOperationContext());
    }

    private void processBatch() {
        TableBatchOperation batch = new TableBatchOperation();
        batch.addAll(batchOperations);
        //
        try {
            executeOperation(batch);

        } catch (StorageException e) {
            // TODO logger
            if (configuration.isDieOnError()) {
                throw new RuntimeException(e);
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e); // connection problem so next operation will also fail, we stop the process
        }
        // reset operations, count and marker
        batchOperations.clear();
        batchOperationsCount = 0;
        latestPartitionKey = "";
    }

    private DynamicTableEntity createDynamicEntityFromInputRecord(Record incomingRecord) {
        DynamicTableEntity entity = new DynamicTableEntity();
        HashMap<String, EntityProperty> entityProps = new HashMap<>();
        for (Schema.Entry f : incomingRecord.getSchema().getEntries()) {

            if (incomingRecord.get(Object.class, f.getName()) == null) {
                continue; // record value may be null, No need to set the property in azure in this case
            }

            if (f.getType() == Schema.Type.RECORD) {
                continue; // NOOP needed for nested Records for now
            }
            String sName = f.getName(); // schema name
            String mName = getMappedNameIfNecessary(sName); // mapped name

            if (sName.equals(configuration.getPartitionName())) {
                entity.setPartitionKey(incomingRecord.getString(sName));
            } else if (sName.equals(configuration.getRowKey())) {
                entity.setRowKey(incomingRecord.getString(sName));
            } else if (mName.equals(AzureTableUtils.TABLE_TIMESTAMP)) {
                // nop : managed by server
            } else { // that's some properties !
                if (f.getType().equals(Schema.Type.BOOLEAN)) {
                    entityProps.put(mName, new EntityProperty(incomingRecord.getBoolean(sName)));
                } else if (f.getType().equals(Schema.Type.DOUBLE)) {
                    entityProps.put(mName, new EntityProperty(incomingRecord.getDouble(sName)));
                } else if (f.getType().equals(Schema.Type.INT)) {
                    entityProps.put(mName, new EntityProperty(incomingRecord.getInt(sName)));
                } else if (f.getType().equals(Schema.Type.BYTES)) {
                    entityProps.put(mName, new EntityProperty(incomingRecord.getBytes(sName)));
                } else if (f.getType().equals(Schema.Type.LONG)) {
                    entityProps.put(mName, new EntityProperty(incomingRecord.getLong(sName)));
                    // TODO datetime
                } else { // use string as default type for string and other types...
                    entityProps.put(mName, new EntityProperty(incomingRecord.getString(sName)));

                }
            }
        }
        // Etag is needed for some operations (delete, merge, replace) but we rely only on PK and RK for those ones.
        entity.setEtag("*");
        entity.setProperties(entityProps);
        return entity;
    }

    private String getMappedNameIfNecessary(String sName) {
        // TODO nameMappings should be not nullable - bug of studio integration
        if (configuration.getNameMappings() != null && configuration.getNameMappings().size() > 0) {
            NameMapping usedNameMapping = configuration.getNameMappings().stream()
                    .filter(nameMapping -> sName.equals(nameMapping.getSchemaColumnName())).findFirst().get();
            if (usedNameMapping != null) {
                return usedNameMapping.getEntityPropertyName();
            }
        }

        return sName;
    }

    private void addOperationToBatch(DynamicTableEntity entity, Record record) {
        if (latestPartitionKey == null || latestPartitionKey.isEmpty()) {
            latestPartitionKey = entity.getPartitionKey();
        }
        // we reached the threshold for batch OR changed PartitionKey
        if (batchOperationsCount == 100 || !entity.getPartitionKey().equals(latestPartitionKey)) {
            processBatch();
            latestPartitionKey = entity.getPartitionKey();
        }
        TableOperation to = getTableOperation(entity);
        batchOperations.add(to);
        batchOperationsCount++;
        latestPartitionKey = entity.getPartitionKey();
    }

    private TableResult executeOperation(TableOperation ope) throws URISyntaxException, StorageException {

        CloudTable cloudTable = connection.createCloudTableClient()
                .getTableReference(configuration.getAzureConnection().getTableName());
        return cloudTable.execute(ope, null, AzureTableUtils.getTalendOperationContext());
    }

    private TableOperation getTableOperation(DynamicTableEntity entity) {
        switch (configuration.getActionOnData()) {
        case INSERT:
            return TableOperation.insert(entity);
        case INSERT_OR_MERGE:
            return TableOperation.insertOrMerge(entity);
        case INSERT_OR_REPLACE:
            return TableOperation.insertOrReplace(entity);
        case MERGE:
            return TableOperation.merge(entity);
        case REPLACE:
            return TableOperation.replace(entity);
        case DELETE:
            return TableOperation.delete(entity);
        default:
            LOGGER.error("No specified operation for table");
            return null;
        }
    }
}