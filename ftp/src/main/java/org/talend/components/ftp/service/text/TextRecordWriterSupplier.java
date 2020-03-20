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
import org.talend.components.common.stream.api.output.RecordWriterSupplier;
import org.talend.components.common.stream.api.output.TargetFinder;
import org.talend.components.common.stream.format.ContentFormat;
import org.talend.components.ftp.dataset.TextConfiguration;
import org.talend.sdk.component.api.record.RecordPointerFactory;
import org.talend.sdk.component.api.service.Service;

public class TextRecordWriterSupplier implements RecordWriterSupplier {

    @Service
    private RecordPointerFactory recordPointerFactory;

    @Override
    public RecordWriter getWriter(TargetFinder target, ContentFormat config) {
        if (!(config instanceof TextConfiguration)) {
            throw new IllegalArgumentException("try to get text-writer with other than text config.");
        }

        TextConfiguration textConfig = (TextConfiguration) config;

        return new TextRecordWriter(textConfig, recordPointerFactory, target);
    }
}
