/*
 * Copyright (C) 2006-2020 Talend Inc. - www.talend.com
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
package org.talend.components.ftp.datastore;

import lombok.Data;
import org.talend.components.ftp.service.FTPService;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@DataStore("FTPDataStore")
@Data
@Icon(value = Icon.IconType.CUSTOM, custom = "ftp")
@Checkable(FTPService.ACTION_HEALTH_CHECK)
@GridLayout(names = GridLayout.FormType.MAIN, value = { @GridLayout.Row({ "host", "port", "implicit" }),
        @GridLayout.Row("useCredentials"), @GridLayout.Row({ "username", "password" }),
        @GridLayout.Row({ "secure", "trustType", "protocol" }), @GridLayout.Row("active") })
@GridLayout(names = GridLayout.FormType.ADVANCED, value = { @GridLayout.Row("fileSystemSeparator"),
        @GridLayout.Row("keepAliveTimeout"), @GridLayout.Row("keepAliveReplyTimeout"), @GridLayout.Row("dateFormat"),
        @GridLayout.Row("recentDateFormat") })
@Documentation("FTP connection Properties")
public class FTPDataStore implements Serializable {

    @Option
    @Required
    @Documentation("FTP host.")
    private String host;

    @Option
    @Documentation("FTP port.")
    private int port = 21;

    @Option
    @Documentation("Does FTP requires credentials.")
    private boolean useCredentials;

    @Option
    @ActiveIf(target = "useCredentials", value = "true")
    @Documentation("FTP username.")
    private String username;

    @Option
    @ActiveIf(target = "useCredentials", value = "true")
    @Documentation("FTP password.")
    @Credential
    private String password;

    @Option
    @Documentation("Should the connection use FTPS.")
    private boolean secure;

    @Option
    @Documentation("How to trust server certificates.")
    @ActiveIf(target = "secure", value = "true")
    private TrustType trustType = TrustType.VALID;

    @Option
    @Documentation("Is the connection implicit.")
    private boolean implicit;

    @Option
    @Documentation("FTPS protocol.")
    @ActiveIf(target = "secure", value = "true")
    private String protocol = "TLS";

    @Option
    @Documentation("Activate active mode, if false passive mode is used.")
    private boolean active;

    @Option
    @Documentation("The file system separator.")
    @DefaultValue("/")
    private String fileSystemSeparator = "/";

    @Option
    @Documentation("How long to wait before sending another control keep-alive message.")
    @DefaultValue("5000")
    private int keepAliveTimeout = 5000;

    @Option
    @Documentation("How long to wait (ms) for keepalive message replies before continuing.")
    @DefaultValue("1000")
    private int keepAliveReplyTimeout = 1000;

    @Option
    @Documentation("Date format.")
    private String dateFormat;

    @Option
    @Documentation("Recent date format.")
    private String recentDateFormat;

    public enum TrustType {
        ALL,
        VALID,
        NONE
    }
}
