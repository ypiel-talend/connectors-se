package org.talend.components.mongodb.source;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

public class FindQueryDataFinder implements QueryDataFinder<Document> {

    @Override
    public MongoCursor<Document> findData(MongoCollection<Document> collection, MongoDBInputMapperConfiguration configuration) {
        Document myQuery = Document.parse(configuration.getQuery());
        FindIterable<Document> iterable = collection.find(myQuery).noCursorTimeout(configuration.isNoQueryTimeout());
        iterable.limit(configuration.getLimit());
        initSorting(iterable, configuration);
        return iterable.iterator();
    }

    private final void initSorting(final FindIterable<Document> findIterable, MongoDBInputMapperConfiguration configuration) {
        if (configuration.getSort() == null || configuration.getSort().isEmpty()) {
            return;
        }
        BasicDBObject orderBy = new BasicDBObject();
        configuration.getSort().stream()
                .forEach(s -> orderBy.put(s.getColumn(), (s.getOrder() == Sort.SortingOrder.asc) ? 1 : -1));
        findIterable.sort(orderBy);
    }

}
