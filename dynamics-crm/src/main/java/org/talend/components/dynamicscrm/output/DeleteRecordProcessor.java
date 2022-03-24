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
package org.talend.components.dynamicscrm.output;

import javax.naming.ServiceUnavailableException;

import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.talend.components.dynamicscrm.service.DynamicsCrmException;
import org.talend.components.dynamicscrm.service.I18n;
import org.talend.ms.crm.odata.DynamicsCRMClient;
import org.talend.sdk.component.api.record.Record;

public class DeleteRecordProcessor implements RecordProcessor {

    private final DynamicsCRMClient client;

    private final EdmEntitySet entitySet;

    private final I18n i18n;

    public DeleteRecordProcessor(final DynamicsCRMClient client, final EdmEntitySet entitySet, final I18n i18n) {
        this.client = client;
        this.entitySet = entitySet;
        this.i18n = i18n;
    }

    @Override
    public void processRecord(Record record) throws ServiceUnavailableException {
        // There is only one key in Dynamics CRM.
        String keyName = entitySet.getEntityType().getKeyPropertyRefs().get(0).getName();
        client.deleteEntity(record.getString(keyName));
    }
}
