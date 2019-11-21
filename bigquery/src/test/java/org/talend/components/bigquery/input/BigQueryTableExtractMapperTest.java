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
package org.talend.components.bigquery.input;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import org.mockito.Mockito;

import java.util.Iterator;
import java.util.function.Consumer;

public class BigQueryTableExtractMapperTest {

    // Page blobs = Mockito.mock(Page.class);
    // Mockito.when(storage.list(Mockito.anyString(), Mockito.any())).thenReturn(blobs);
    // Iterable<Blob> blobsIterable = Mockito.mock(Iterable.class);
    // Mockito.when(blobs.iterateAll()).thenReturn(blobsIterable);
    // Mockito.doCallRealMethod().when(blobsIterable).forEach(Mockito.any(Consumer.class));
    // blobsIterator = Mockito.mock(Iterator .class);
    // Mockito.when(blobsIterable.iterator()).thenReturn(blobsIterator);
}
