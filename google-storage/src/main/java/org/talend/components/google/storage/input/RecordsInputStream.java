/*
 * Copyright (C) 2006-2022 Talend Inc. - www.talend.com
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
package org.talend.components.google.storage.input;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Supplier;

import org.talend.components.common.collections.IteratorComposer;
import org.talend.components.common.stream.api.input.RecordReader;
import org.talend.sdk.component.api.record.Record;

public class RecordsInputStream implements AutoCloseable {

    private final RecordReader recordReader;

    private final InputStream inputStream;

    public RecordsInputStream(RecordReader recordReader, Supplier<InputStream> inputStreamGetter) {
        this.recordReader = recordReader;
        if (inputStreamGetter != null) {
            this.inputStream = inputStreamGetter.get();
        } else {
            this.inputStream = null;
        }
    }

    public Iterator<Record> records() {
        if (this.inputStream == null) {
            return Collections.emptyIterator();
        }
        return IteratorComposer.of(recordReader.read(inputStream)).closeable(this::close).build();
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }
}
