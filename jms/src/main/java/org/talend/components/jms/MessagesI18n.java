package org.talend.components.jms;

import org.talend.sdk.component.api.internationalization.Internationalized;

@Internationalized
public interface MessagesI18n {

    String connectionSuccessful();

    String connectionFailed(String cause);

    String connectionCantCloseGracefully(String cause);
}
