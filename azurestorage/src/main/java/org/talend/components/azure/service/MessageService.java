package org.talend.components.azure.service;

import org.talend.sdk.component.api.internationalization.Internationalized;

@Internationalized
public interface MessageService {

    String connected();

    String connectionError();

    String errorRetrieveTables();

    String errorRetrieveSchema();
}
