package org.talend.components.ftp.output;

import lombok.extern.slf4j.Slf4j;
import org.talend.components.ftp.jupiter.FtpFile;
import org.talend.sdk.component.junit.environment.Environment;
import org.talend.sdk.component.junit.environment.EnvironmentConfiguration;
import org.talend.sdk.component.junit.environment.builtin.ContextualEnvironment;
import org.talend.sdk.component.junit5.WithComponents;

@Slf4j
@Environment(ContextualEnvironment.class)
@EnvironmentConfiguration(environment = "Contextual", systemProperties = {})
@WithComponents(value = "org.talend.components.ftp")
@FtpFile(base = "fakeFTP/")
public class FTPOutputTest {
}
