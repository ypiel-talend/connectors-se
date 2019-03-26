package org.talend.components.mongodb.service;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.talend.components.mongodb.dataset.MongoDBDataset;
import org.talend.components.mongodb.datastore.MongoDBDatastore;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Suggestions;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UIMongoDBService {

    public static final String HEALTH_CHECK = "healthCheck";

    public static final String GET_SCHEMA_FIELDS = "loadFields";

    @Service
    private MongoDBService mongoDbService;

    @HealthCheck(HEALTH_CHECK)
    public HealthCheckStatus testConnection(MongoDBDatastore datastore) {
        MongoClient mongo = mongoDbService.getMongoClient(datastore, new MongoDBService.DefaultClientOptionsFactory(datastore));
        try {
            MongoDatabase db = mongo.getDatabase(datastore.getDatabase());
            MongoIterable<String> collectionsIterable = db.listCollectionNames();
            Iterator<String> collectionsIterator = collectionsIterable.iterator();
            if (collectionsIterator.hasNext()) {
                System.out.println("getting collection name");
                collectionsIterator.next();
            }
            mongo.close();
        } catch (Exception e) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, "Exception");
        }
        return new HealthCheckStatus(HealthCheckStatus.Status.OK, "Everything's ok");
    }

    @Suggestions(GET_SCHEMA_FIELDS)
    public SuggestionValues getFields(MongoDBDataset dataset) {
        if (dataset.getSchema() == null || dataset.getSchema().isEmpty()) {
            return new SuggestionValues(false, Collections.emptyList());
        }
        List<SuggestionValues.Item> items = dataset.getSchema().stream().map(s -> new SuggestionValues.Item(s, s))
                .collect(Collectors.toList());
        return new SuggestionValues(false, items);
    }

}
