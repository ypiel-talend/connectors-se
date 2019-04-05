package org.talend.components.mongodb.output;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;
import org.talend.components.mongodb.output.processor.ValuesProcessor;
import org.talend.components.mongodb.output.processor.ValuesProcessorsFactory;
import org.talend.components.mongodb.service.I18nMessage;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.*;
import org.talend.sdk.component.api.record.Record;

import org.talend.components.mongodb.service.MongoDBService;
import org.talend.sdk.component.api.record.Schema;

@Version(1) // default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Icon(Icon.IconType.STAR) // you can use a custom one using @Icon(value=CUSTOM, custom="filename") and adding
                          // icons/filename_icon32.png in resources
@Processor(name = "MongoDBOutput")
@Documentation("MongoDB output component")
public class MongoDBOutput implements Serializable {

    private final I18nMessage i18n;

    private final MongoDBOutputConfiguration configuration;

    private MongoCollection<Document> collection;

    private Map<String, OutputMapping> mapping;

    private List<String> columnsList = new ArrayList<>();

    private final MongoDBService service;

    private MongoClient client;

    private ValuesProcessor<? extends WriteModel<Document>> valuesProcessor;

    public MongoDBOutput(@Option("configuration") final MongoDBOutputConfiguration configuration, final MongoDBService service,
            final I18nMessage i18n) {
        this.configuration = configuration;
        this.service = service;
        this.i18n = i18n;
    }

    @PostConstruct
    public void init() {
        this.client = service.getMongoClient(configuration.getDataset().getDatastore(),
                new OutputClientOptionsFactory(configuration, i18n));
        collection = service.getCollection(configuration.getDataset(), client);
        // if (configuration.isDropCollectionIfExists()) {
        // collection.drop();
        // }
        List<OutputMapping> outputMappings = configuration.getOutputMappingList();
        if (outputMappings != null) {
            mapping = outputMappings.stream().collect(Collectors.toMap(OutputMapping::getColumn, c -> c));
        } else {
            mapping = Collections.emptyMap();
        }
        if (configuration.getDataset().getSchema() != null) {
            columnsList.addAll(configuration.getDataset().getSchema());
        }
        valuesProcessor = ValuesProcessorsFactory.createProcessor(configuration, collection, i18n);
    }

    @BeforeGroup
    public void beforeGroup() {
    }

    @ElementListener
    public void onNext(@Input final Record defaultInput) {
        if (columnsList.isEmpty()) {
            columnsList.addAll(
                    defaultInput.getSchema().getEntries().stream().map(Schema.Entry::getName).collect(Collectors.toList()));
        }
        final Map<String, Schema.Type> defaultInputTypesMap = defaultInput.getSchema().getEntries().stream()
                .collect(Collectors.toMap(Schema.Entry::getName, Schema.Entry::getType));
        columnsList.stream().forEach(col -> valuesProcessor.processField(mapping.get(col), col,
                getValue(defaultInput, col, defaultInputTypesMap.get(col))));
        valuesProcessor.finalizeRecord();
    }

    @AfterGroup
    public void afterGroup() {
        valuesProcessor.flush();
    }

    @PreDestroy
    public void release() {
        valuesProcessor.close();
        client.close();
    }

    protected Object getValue(Record defaultInput, String col, Schema.Type type) {
        Object value = null;
        if (type == null) {
            value = defaultInput.get(Object.class, col);
        } else {
            switch (type) {
            case INT:
                value = defaultInput.getInt(col);
                break;
            case LONG:
                value = defaultInput.getLong(col);
                break;
            case RECORD:
                break;
            case ARRAY:
                value = defaultInput.getArray(Object.class, col);
                break;
            case STRING:
                value = defaultInput.getString(col);
                break;
            case BYTES:
                value = defaultInput.getBytes(col);
                break;
            case FLOAT:
                value = defaultInput.getFloat(col);
                break;
            case DOUBLE:
                value = defaultInput.getDouble(col);
                break;
            case BOOLEAN:
                value = defaultInput.getBoolean(col);
                break;
            case DATETIME:
                value = defaultInput.getDateTime(col);
                break;
            }
        }
        return value;
    }

}