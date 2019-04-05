package org.talend.components.mongodb.service;

import org.talend.sdk.component.api.internationalization.Internationalized;

@Internationalized
public interface I18nMessage {

    String connectionSuccessful();

    String connectionFailed(String error);

    String emptyRecord();

    String noKeysFound(String object);

    String cannotDeleteObjectWithoutKeys();

    String createdRecord(String record);

    String modelCreated(String valueOf);

    String addingField(String col, Object value);

    String authMechanismNotSupported(String mechanism);

    String factoryClass(String className);

    String retrievingCollection(String collection);
}
