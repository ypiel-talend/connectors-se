/*
 * Copyright (C) 2006-2021 Talend Inc. - www.talend.com
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
package org.talend.components.adlsgen2.service;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.azurebfs.extensions.SASTokenProvider;
import org.apache.hadoop.security.AccessControlException;
import org.talend.components.adlsgen2.datastore.Constants;

import java.io.IOException;

public class TalendSASTokenProvider implements SASTokenProvider {

    Configuration configuration;

    @Override
    public void initialize(Configuration configuration, String accountName) throws IOException {
        this.configuration = configuration;
    }

    @Override
    public String getSASToken(String accountName, String filesystem, String path, String operation)
            throws IOException, AccessControlException {
        return configuration.get(Constants.STATIC_SAS_TOKEN_KEY);
    }
}
