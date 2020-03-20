/*
 * Copyright (C) 2006-2020 Talend Inc. - www.talend.com
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
package org.talend.components.ftp.service.text;

import org.talend.components.common.stream.api.output.RecordWriter;
import org.talend.components.common.stream.api.output.TargetFinder;
import org.talend.components.ftp.dataset.TextConfiguration;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.RecordPointer;
import org.talend.sdk.component.api.record.RecordPointerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class TextRecordWriter implements RecordWriter {

    private final TargetFinder out;

    private final RecordPointer recordPointer;

    private final Charset charset;

    public TextRecordWriter(TextConfiguration textConfiguration, RecordPointerFactory recordPointerFactory, TargetFinder out) {
        this.out = out;
        recordPointer = recordPointerFactory.apply(textConfiguration.getPathToContent());
        charset = Charset.forName(textConfiguration.getEncoding());
    }

    @Override
    public void add(Record record) throws IOException {
        String text = recordPointer.getValue(record, String.class);
        out.find().write(text.getBytes(charset));
    }

    @Override
    public void flush() throws IOException {
        out.find().flush();
    }

    @Override
    public void close() throws IOException {
        out.find().close();
    }
}
