package org.talend.components.couchbase.service;

import lombok.extern.slf4j.Slf4j;
import org.talend.components.couchbase.datastore.CouchbaseDataStore;
import org.talend.sdk.component.api.service.Service;

@Slf4j
@Service
public class CouchbaseService {

    @Service
    private CouchbaseDataStore couchBaseConnection;

    // you can put logic here you can reuse in components

}