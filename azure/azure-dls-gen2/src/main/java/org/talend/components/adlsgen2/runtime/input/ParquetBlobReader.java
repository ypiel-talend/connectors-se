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
package org.talend.components.adlsgen2.runtime.input;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.talend.components.adlsgen2.common.format.FileFormatRuntimeException;
import org.talend.components.adlsgen2.input.InputConfiguration;
import org.talend.components.adlsgen2.service.AdlsGen2Service;
import org.talend.components.adlsgen2.service.BlobInformations;
import org.talend.components.common.stream.input.parquet.ParquetRecordReader;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParquetBlobReader extends BlobReader {

    public ParquetBlobReader(InputConfiguration configuration, RecordBuilderFactory recordBuilderFactory,
            AdlsGen2Service connectionServices) {
        super(configuration, recordBuilderFactory, connectionServices);
    }

    @Override
    protected Iterator<Record> initRecordIterator(Iterable<BlobInformations> blobItems) {
        return new ParquetRecordIterator(blobItems, recordBuilderFactory);
    }

    private class ParquetRecordIterator extends RecordIterator<Record> {

        private Configuration hadoopConfig;

        private Iterator<Record> reader;

        private Record currentRecord;

        private ParquetRecordIterator(final Iterable<BlobInformations> blobItemsList,
                final RecordBuilderFactory recordBuilderFactory) {
            super(blobItemsList, recordBuilderFactory);
            initConfig();
            peekFirstBlob();
        }

        private void initConfig() {
            hadoopConfig = new Configuration();
            hadoopConfig.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        }

        @Override
        protected Record convertToRecord(Record next) {
            return next;
        }

        @Override
        protected void readBlob() {
            try {
                final File tmp = File.createTempFile("talend-adls-gen2-tmp", ".parquet");
                tmp.deleteOnExit();
                try (InputStream input = service.getBlobInputstream(configuration.getDataSet(), getCurrentBlob())) {
                    Files.copy(input, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                final HadoopInputFile hdpIn = HadoopInputFile.fromPath(new Path(tmp.getPath()), hadoopConfig);

                this.reader = new ParquetRecordReader(this.getRecordBuilderFactory()).read(hdpIn);
                this.currentRecord = reader.next();
            } catch (IOException e) {
                log.error("[ParquetIterator] {}", e.getMessage());
                throw new FileFormatRuntimeException(e.getMessage());
            }
        }

        @Override
        protected boolean hasNextBlobRecord() {
            return currentRecord != null;
        }

        @Override
        protected Record peekNextBlobRecord() {
            Record currentRecord = this.currentRecord;
            if (reader.hasNext()) {
                this.currentRecord = reader.next();
            } else {
                this.currentRecord = null;
            }

            return currentRecord;
        }

        @Override
        protected void complete() {
        }

    }

}
