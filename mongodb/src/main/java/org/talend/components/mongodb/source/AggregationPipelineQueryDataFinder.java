package org.talend.components.mongodb.source;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class AggregationPipelineQueryDataFinder implements QueryDataFinder<Document> {

    @Override
    public MongoCursor<Document> findData(MongoCollection<Document> collection, MongoDBInputMapperConfiguration configuration) {
        List<Document> aggregationStages = new ArrayList<>();
        if (configuration.getAggregationStages() != null) {
            configuration.getAggregationStages().stream()
                    .forEach(stage -> aggregationStages.add(Document.parse(stage.getStage())));
        }
        MongoCursor<Document> cursor_tMongoDBInput_1 = collection.aggregate(aggregationStages)
                .allowDiskUse(configuration.isExternalSort()).iterator();
        return cursor_tMongoDBInput_1;
    }
}
