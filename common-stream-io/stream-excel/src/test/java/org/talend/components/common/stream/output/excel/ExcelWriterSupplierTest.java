package org.talend.components.common.stream.output.excel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.talend.components.common.stream.format.excel.ExcelConfiguration;
import org.talend.components.common.stream.format.excel.ExcelConfiguration.ExcelFormat;

class ExcelWriterSupplierTest {

    @Test
    void unsupportedHtml() {
        ExcelConfiguration config = new ExcelConfiguration();
        config.setExcelFormat(ExcelFormat.HTML);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ExcelWriterSupplier().getWriter(() -> null, config));
    }
}
