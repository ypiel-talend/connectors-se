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
package org.talend.components.ftp.source;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.talend.components.ftp.service.FTPService;
import org.talend.components.ftp.service.I18nMessage;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.input.Assessor;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.input.Split;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Version(1)
@Icon(value = Icon.IconType.CUSTOM, custom = "ftp")
@PartitionMapper(name = "FTPInput")
@Documentation("This component reads a FTP folder.")
@Slf4j
@RequiredArgsConstructor
public class FTPPartitionMapper implements Serializable {

    protected final FTPInputConfiguration configuration;

    protected final FTPService ftpService;

    protected final I18nMessage i18n;

    @Assessor
    public long estimateSize() {
        return 1l;
    }

    @Split
    public List<FTPPartitionMapper> split() {
        return Collections.singletonList(this);
    }

    @Emitter
    public FTPInput createSource() {
        return new FTPInput(configuration, ftpService, i18n);
    }

}
