/*
 * Copyright (C) 2006-2022 Talend Inc. - www.talend.com
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
package org.talend.components.common.formats.excel;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.function.Function;
import org.talend.components.common.formats.Encoding;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.condition.ActiveIfs;
import org.talend.sdk.component.api.configuration.constraint.Min;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.exception.ComponentException;
import org.talend.sdk.component.api.meta.Documentation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@GridLayout({ @GridLayout.Row("excelFormat"), @GridLayout.Row("sheetName"), @GridLayout.Row("encoding"),
        @GridLayout.Row("customEncoding"), @GridLayout.Row("useHeader"), @GridLayout.Row("header"),
        @GridLayout.Row("useFooter"),
        @GridLayout.Row("footer") })
@Data
public class ExcelFormatOptions implements Serializable {

    @Option
    @Documentation("Excel format")
    private ExcelFormat excelFormat = ExcelFormat.EXCEL2007;

    @Option
    @ActiveIf(target = "excelFormat", value = { "EXCEL2007", "EXCEL97" })
    // @Required
    // FIXME: Required doesn't work simultaneously with @ActiveIf
    @Documentation("")
    private String sheetName = "Sheet1";

    @Option
    @ActiveIf(target = "excelFormat", value = "HTML")
    @Documentation("")
    private Encoding encoding = Encoding.UTF8;

    @Option
    @ActiveIfs({ @ActiveIf(target = "excelFormat", value = "HTML"), @ActiveIf(target = "encoding", value = "OTHER") })
    @Documentation("Encoding ")
    private String customEncoding;

    @Option
    @ActiveIf(target = "excelFormat", value = { "EXCEL2007", "EXCEL97" })
    @Documentation("")
    private boolean useHeader;

    @Option
    @ActiveIfs(operator = ActiveIfs.Operator.AND, value = { @ActiveIf(target = "useHeader", value = "true"),
            @ActiveIf(target = "excelFormat", value = { "EXCEL2007", "EXCEL97" }) })
    @Documentation("")
    @Min(0)
    private int header = 1;

    @Option
    @ActiveIf(target = "excelFormat", value = { "EXCEL2007", "EXCEL97" })
    @Documentation("")
    private boolean useFooter;

    @Option
    @ActiveIfs(operator = ActiveIfs.Operator.AND, value = { @ActiveIf(target = "useFooter", value = "true"),
            @ActiveIf(target = "excelFormat", value = { "EXCEL2007", "EXCEL97" }) })
    @Documentation("")
    @Min(0)
    private int footer = 1;

    public String effectiveHTMLFileEncoding(final Function<String, String> errorMsgBuilder) {
        if (excelFormat != ExcelFormat.HTML) {
            throw new IllegalStateException("Only HTML format supports encoding configuration"); // should not be here
        }
        if (Encoding.OTHER == getEncoding()) {
            try {
                Charset.forName(customEncoding);
                return getCustomEncoding();
            } catch (Exception e) {
                log.error("[effectiveFileEncoding] {}", e.getMessage());
                throw new ComponentException(errorMsgBuilder.apply(getCustomEncoding()));
            }
        } else {
            return getEncoding().getEncodingCharsetValue();
        }
    }
}
