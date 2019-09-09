/*
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
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
package org.talend.components.jdbc.service;

import org.talend.sdk.component.api.internationalization.Internationalized;

@Internationalized
public interface I18nMessage {

    String errorDriverDeregister(String type);

    String warnDriverClose(String type);

    String errorDriverLoad(String driverId, String missingJars);

    String errorEmptyJdbcURL();

    String errorCantLoadDriver(String dbType);

    String errorUnsupportedSubProtocol();

    String errorInvalidConnection();

    String errorDriverNotFound(String dbType);

    String errorSQL(int errorCode, String message);

    String errorDriverInstantiation(String message);

    String successConnection();

    String errorUnauthorizedQuery();

    String errorEmptyQuery();

    String warnResultSetCantBeClosed();

    String warnStatementCantBeClosed();

    String warnConnectionCantBeClosed();

    String warnReadOnlyOptimisationFailure();

    String errorNoKeyForDeleteQuery();

    String errorNoKeyForUpdateQuery();

    String errorNoFieldForQueryParam(String field);

    String errorNoUpdatableColumnWasDefined();

    String errorUnsupportedDatabaseAction();

    String errorCantClearPreparedStatement();

    String errorCantClosePreparedStatement();

    String errorCantCloseJdbcConnectionProperly();

    String errorCantLoadTableSuggestions();

    String errorTaberDoesNotExists(String tableName);

    String errorRedshiftUnsupportedBytes(String field);

    String errorUnsupportedDatabase(String dbType);

    String errorUnsupportedType(String type, String field);

    //
    String actionOnDataInsert();

    String actionOnDataUpdate();

    String actionOnDataUpsert();

    String actionOnDataDelete();

    String actionOnDataBulkLoad();

    String errorSingleSortKeyInvalid();

    String errorVacantAccountKey();

    String errorNoRecordReceived();
}
