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

package org.talend.components.azure.common.runtime.output;

import java.net.URISyntaxException;

import org.talend.components.azure.output.BlobOutputConfiguration;
import org.talend.components.azure.service.AzureBlobConnectionServices;

import com.microsoft.azure.storage.StorageException;

public class AvroBlobFileWriter extends BlobFileWriter {

    public AvroBlobFileWriter(BlobOutputConfiguration config, AzureBlobConnectionServices connectionServices) throws Exception {
        super(config, connectionServices);
    }

    @Override
    public void flush() {

    }

    @Override
    public void generateFile() throws URISyntaxException, StorageException {

    }

    @Override
    public void complete() {

    }
}
