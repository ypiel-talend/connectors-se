package org.talend.components.common;

public class ConnectorException extends RuntimeException {

    private static final long serialVersionUID = 7443958636958326012L;

    public ConnectorException(String message) {
        super(message);
    }

    public ConnectorException(String message, Throwable cause) {
        super(message, cause);
    }
}