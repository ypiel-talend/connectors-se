package org.talend.components.mongodb.source;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public interface QueryDataFinder<T> {

    MongoCursor<T> findData(MongoCollection<T> collection, MongoDBInputMapperConfiguration configuration);

}
