package org.talend.components.fileio.hdfs;

import static org.talend.sdk.component.api.component.Icon.IconType.FILE_HDFS_O;

import java.io.Serializable;

import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.OptionsOrder;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@Icon(FILE_HDFS_O)
@DataStore("SimpleFileIODataStore")
@Documentation("Datastore of a HDFS source.")
@OptionsOrder({ "useKerberos", "kerberosPrincipal", "kerberosKeytab", "username" })
public class SimpleFileIODataStore implements Serializable {

    @Option
    @ActiveIf(target = "../useKerberos", value = "false")
    @Documentation("The datastore username to use")
    private String username;

    @Option
    @Documentation("Should Kerberos authentication be used.")
    private boolean useKerberos;

    @Option
    @ActiveIf(target = "../useKerberos", value = "true")
    @Documentation("If `useKerberos` is true, the principal to use.")
    private String kerberosPrincipal = "username@EXAMPLE.COM";

    @Option
    @ActiveIf(target = "../useKerberos", value = "true")
    @Documentation("If `useKerberos` is true, the keytab to use.")
    private String kerberosKeytab = "/home/username/username.keytab";
}
