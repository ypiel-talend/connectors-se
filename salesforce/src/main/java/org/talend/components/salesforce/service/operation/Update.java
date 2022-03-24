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
package org.talend.components.salesforce.service.operation;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sforce.soap.partner.IError;
import com.sforce.soap.partner.ISaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

import org.talend.components.salesforce.configuration.OutputConfig.OutputAction;
import org.talend.components.salesforce.service.operation.converters.SObjectConverter;
import org.talend.sdk.component.api.record.Record;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Update implements RecordsOperation {

    private final ConnectionFacade connection;

    private final SObjectConverter converter;

    @Override
    public List<Result> execute(List<Record> records) throws IOException {
        final SObject[] upds = new SObject[records.size()];
        for (int i = 0; i < records.size(); i++) {
            upds[i] = converter.fromRecord(records.get(i), OutputAction.UPDATE);
        }

        String[] changedItemKeys = new String[upds.length];
        for (int ix = 0; ix < upds.length; ++ix) {
            changedItemKeys[ix] = upds[ix].getId();
        }

        try {
            final ISaveResult[] saveResults = connection.update(upds);
            return Stream
                    .of(saveResults) //
                    .map(this::toResult)
                    .collect(Collectors.toList());
        } catch (ConnectionException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String name() {
        return "update";
    }

    private Result toResult(ISaveResult saveResult) {
        if (saveResult.isSuccess()) {
            return Result.OK;
        }
        final List<String> errors = Stream
                .of(saveResult.getErrors()) //
                .map(IError::getMessage) //
                .collect(Collectors.toList());
        return new Result(errors);
    }
}
