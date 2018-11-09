package org.talend.components.fileio.configuration;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.ui.OptionsOrder;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@Documentation("File format configuration properties.")
@OptionsOrder({ "format", "csvConfiguration" })
public class FileFormatConfiguration implements Serializable {

    // This will be required later, when we implement other types of files
    @Option
    @Required
    @Documentation("Data format to be parsed.")
    @ActiveIf(target = ".", value = "Stub")
    private SimpleFileIOFormat format = SimpleFileIOFormat.CSV;

    @Option
    @Documentation("CSV configuration.")
    @ActiveIf(target = "format", value = "CSV")
    private CsvConfiguration csvConfiguration;

}
