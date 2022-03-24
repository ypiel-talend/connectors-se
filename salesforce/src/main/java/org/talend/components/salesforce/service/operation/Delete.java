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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sforce.soap.partner.IDeleteResult;
import com.sforce.soap.partner.IError;
import com.sforce.ws.ConnectionException;

import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Delete implements RecordsOperation {

    private static final String ID = "Id";

    private final ConnectionFacade connection;

    @Override
    public List<Result> execute(List<Record> records) throws IOException {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }

        final Record first = records.get(0);

        // Calculate the field position of the Id the first time that it is used. The Id field must be present in the
        // schema to delete rows.
        boolean containsId = false;
        for (Schema.Entry field : first.getSchema().getEntries()) {
            if (ID.equals(field.getName())) {
                containsId = true;
                break;
            }
        }
        if (containsId) {
            final List<Record> recordWithID = records
                    .stream() //
                    .filter((Record r) -> r.getString(ID) != null) //
                    .collect(Collectors.toList());

            return this.doDelete(recordWithID);
        } else {
            throw new RuntimeException("'Id' field not found!");
        }
    }

    @Override
    public String name() {
        return "delete";
    }

    private List<Result> doDelete(List<Record> records) throws IOException {

        // Clean the feedback records at each batch write.

        String[] delIDs = new String[records.size()];
        String[] changedItemKeys = new String[delIDs.length];
        for (int ix = 0; ix < delIDs.length; ++ix) {
            delIDs[ix] = records.get(ix).getString(ID);
            changedItemKeys[ix] = delIDs[ix];
        }
        try {
            final IDeleteResult[] dr = connection.delete(delIDs);
            return Stream.of(dr).map(this::toResult).collect(Collectors.toList());
        } catch (ConnectionException e) {
            throw new IOException(e);
        }
    }

    private Result toResult(IDeleteResult deleteResult) {
        if (deleteResult.isSuccess()) {
            return Result.OK;
        }
        final List<String> errors = Stream
                .of(deleteResult.getErrors()) //
                .map(IError::getMessage) //
                .collect(Collectors.toList());
        return new Result(errors);
    }
}
