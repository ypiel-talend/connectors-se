/*
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.talend.components.couchbase.source;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import org.talend.components.couchbase.service.CouchbaseService;
import org.talend.components.couchbase.service.I18nMessage;

import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.*;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.Collections.sort;

@Version(1)
@Icon(value = Icon.IconType.CUSTOM, custom = "CouchbaseInput")
@PartitionMapper(name = "Input")
@Documentation("Couchbase input Mapper")
public class CouchbaseInputMapper implements Serializable {

    private final CouchbaseInputConfiguration configuration;

    private final CouchbaseService service;

    private final RecordBuilderFactory recordBuilderFactory;

    private final I18nMessage i18nMessage;

    private long offset;

    private long processors;

    private long estimateSize;

    private long limit;

    public CouchbaseInputMapper(@Option("configuration") final CouchbaseInputConfiguration configuration,
            final CouchbaseService service, final RecordBuilderFactory recordBuilderFactory, final I18nMessage i18nMessage) {
        this.configuration = configuration;
        this.service = service;
        this.recordBuilderFactory = recordBuilderFactory;
        this.i18nMessage = i18nMessage;
    }

    public CouchbaseInputMapper(@Option("configuration") final CouchbaseInputConfiguration configuration, CouchbaseService service,
                                RecordBuilderFactory recordBuilderFactory, I18nMessage i18nMessage, long offset, long limit) {
        this(configuration, service, recordBuilderFactory, i18nMessage);
        this.offset = offset;
        this.limit = limit;
    }

    @Assessor
    public long estimateSize() {
        // this method should return the estimation of the dataset size
        // it is recommended to return a byte value
        // if you don't have the exact size you can use a rough estimation

        try {
            estimateSize = Long.parseLong(configuration.getLimit());
            return estimateSize;
            //TODO: change data type for processors value from string to long
        } catch (NumberFormatException e) {
            // Can't parse processors value or it's empty. Try to get total number of records from bucket
            try {
                Bucket bucket = service.openConnection(configuration.getDataSet());
                estimateSize = service.getTotalNumberOfRecordsInBucket(bucket);
                return estimateSize;
            } catch (Exception e1){
                // If we can't receive
                estimateSize = 100;
                return estimateSize;
            }
        }
    }

    @Split
    public List<CouchbaseInputMapper> split(@PartitionSize final long bundles) {
        // overall idea here is to split the work related to configuration in bundles of size "bundles"
        //
        // for instance if your estimateSize() returned 1000 and you can run on 10 nodes
        // then the environment can decide to run it concurrently (10 * 100).
        // In this case bundles = 100 and we must try to return 10 CouchbaseInputMapper with 1/10 of the overall work each.
        //
        // default implementation returns this which means it doesn't support the work to be split

        processors = estimateSize / bundles;

        List<CouchbaseInputMapper> inputMapperList = new ArrayList<>();

        for (int i=0; i < processors; i++){
            //check if last processor
            if (i == (processors - 1)){
                offset = (i * bundles);
                limit = bundles + (estimateSize % bundles);

            } else {
                offset = i * bundles;
                limit = bundles;
            }
            CouchbaseInputMapper couchbaseInputMapper = new CouchbaseInputMapper(configuration, service, recordBuilderFactory, i18nMessage, offset, limit);
            inputMapperList.add(couchbaseInputMapper);
        }
        return inputMapperList;
    }

    @Emitter
    public CouchbaseInput createWorker() {
        // here we create an actual worker,
        // you are free to rework the configuration etc but our default generated implementation
        // propagates the partition mapper entries.
//        if (processors == 0) {
//            return new CouchbaseInput(configuration, service, recordBuilderFactory, i18nMessage);
//        } else {
        return new CouchbaseInput(configuration, service, recordBuilderFactory, i18nMessage, offset, limit);
//        }
    }
}