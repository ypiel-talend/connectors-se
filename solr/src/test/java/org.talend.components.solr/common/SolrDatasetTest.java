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
package org.talend.components.solr.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.talend.sdk.component.junit5.WithComponents;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@WithComponents("org.talend.components.solr")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SolrDatasetTest {

    @Test
    public void testNoSlashSuffix() {
        SolrDataset solrDataset = prepareSolrConnectionConfiguration("http://localhost:8983/solr", "testCore");
        assertEquals("http://localhost:8983/solr/testCore", solrDataset.getFullUrl());
    }

    @Test
    public void testSlashSuffix() {
        SolrDataset solrDataset = prepareSolrConnectionConfiguration("http://localhost:8983/solr/", "testCore");
        assertEquals("http://localhost:8983/solr/testCore", solrDataset.getFullUrl());
    }

    @Test
    public void testInverseSlashSuffix() {
        SolrDataset solrDataset = prepareSolrConnectionConfiguration("http://localhost:8983/solr\\", "testCore");
        assertEquals("http://localhost:8983/solr\\testCore", solrDataset.getFullUrl());
    }

    private SolrDataset prepareSolrConnectionConfiguration(String url, String core) {
        SolrDataset solrDataset = new SolrDataset();
        SolrDataStore solrDataStore = new SolrDataStore();
        solrDataStore.setUrl(url);
        solrDataset.setDataStore(solrDataStore);
        solrDataset.setCore(core);
        return solrDataset;
    }

}
