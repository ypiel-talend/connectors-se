package org.talend.components.fileio.s3.configuration;

import static org.talend.sdk.component.api.component.Icon.IconType.FILE_S3_O;

import java.io.Serializable;

import org.talend.components.fileio.configuration.FileFormatConfiguration;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.OptionsOrder;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@Icon(FILE_S3_O)
@DataSet("S3DataSet")
@Documentation("Dataset of a S3 source.")
@OptionsOrder({ "datastore", "bucket", "object", "encryptDataAtRest", "kmsForDataAtRest", "fileFormatConfiguration", "limit" })
public class S3DataSet implements Serializable {

    @Option
    @Documentation("The S3 datastore")
    private S3DataStore datastore;

    @Option
    @Required
    @Suggestable(value = "S3FindBuckets", parameters = { "datastore" })
    @Documentation("The dataset bucket.")
    private String bucket;

    @Option
    @Required
    @Documentation("The dataset object.")
    private String object;

    @Option
    @Documentation("Should data at rest be encrypted.")
    private boolean encryptDataAtRest;

    @Option
    @ActiveIf(target = "encryptDataAtRest", value = "true")
    @Documentation("KMS to use for data at rest encryption.")
    private String kmsForDataAtRest;

    @Option
    @Required
    @Documentation("Data format to be parsed.")
    private FileFormatConfiguration fileFormatConfiguration;

    @Option
    @ActiveIf(target = ".", value = "-2147483648")
    @Documentation("Maximum number of data to handle if positive.")
    private int limit = -1;
}
