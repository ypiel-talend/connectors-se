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
package org.talend.components.adlsgen2.migration;

import java.util.HashMap;
import java.util.Map;

import org.talend.sdk.component.api.component.MigrationHandler;

public class AdlsDataSetMigrationHandler implements MigrationHandler {

    private static final String DEFAULT_HEADER_SIZE = "1";

    @Override
    public Map<String, String> migrate(int incomingVersion, Map<String, String> incomingData) {
        Map<String, String> migratedConfiguration = new HashMap<>(incomingData);
        if (incomingVersion < 2) {
            putIfNotNull(migratedConfiguration, "csvConfiguration.recordSeparator",
                    "csvConfiguration.csvFormatOptions.recordDelimiter");
            putIfNotNull(migratedConfiguration, "csvConfiguration.customRecordSeparator",
                    "csvConfiguration.csvFormatOptions.customRecordDelimiter");
            putIfNotNull(migratedConfiguration, "csvConfiguration.header",
                    "csvConfiguration.csvFormatOptions.useHeader");
            migratedConfiguration.put("csvConfiguration.csvFormatOptions.header", DEFAULT_HEADER_SIZE);
            putIfNotNull(migratedConfiguration, "csvConfiguration.fileEncoding",
                    "csvConfiguration.csvFormatOptions.encoding");
            putIfNotNull(migratedConfiguration, "csvConfiguration.customFileEncoding",
                    "csvConfiguration.csvFormatOptions.customEncoding");
            putIfNotNull(migratedConfiguration, "csvConfiguration.fieldDelimiter",
                    "csvConfiguration.csvFormatOptions.fieldDelimiter");
            putIfNotNull(migratedConfiguration, "csvConfiguration.customFieldDelimiter",
                    "csvConfiguration.csvFormatOptions.customFieldDelimiter");
            putIfNotNull(migratedConfiguration, "csvConfiguration.textEnclosureCharacter",
                    "csvConfiguration.csvFormatOptions.textEnclosureCharacter");
            putIfNotNull(migratedConfiguration, "csvConfiguration.escapeCharacter",
                    "csvConfiguration.csvFormatOptions.escapeCharacter");
        }
        return migratedConfiguration;
    }

    private static void putIfNotNull(Map<String, String> configMap, String from, String to) {
        if (configMap.containsKey(from) && configMap.get(from) != null) {
            configMap.put(to, configMap.remove(from));
        }
    }
}
