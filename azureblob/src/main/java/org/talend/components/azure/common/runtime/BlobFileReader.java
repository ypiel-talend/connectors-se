/*
 * Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

package org.talend.components.azure.common.runtime;

import org.talend.components.azure.dataset.AzureBlobDataset;
import org.talend.components.azure.service.AzureBlobConnectionServices;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

public abstract class BlobFileReader {

    public abstract Record readRecord();

    public static class BlobFileReaderFactory {

        public static BlobFileReader getReader(AzureBlobDataset config, RecordBuilderFactory recordBuilderFactory,
                AzureBlobConnectionServices connectionServices) throws Exception {
            switch (config.getFileFormat()) {
            case CSV:
                return new CSVBlobFileReader(config, recordBuilderFactory, connectionServices);
            case AVRO:
                return new AvroBlobFileReader();
            case EXCEL:
                return new ExcelBlobFileReader();
            case PARQUET:
                return new ParquetBlobFileReader();
            default:
                throw new IllegalArgumentException("Unsupported file format"); // shouldn't be here
            }
        }
    }
}
