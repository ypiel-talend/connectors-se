package org.talend.components.common.stream.output.csv;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.talend.components.common.stream.format.ContentFormat;

class CSVWriterSupplierTest {

    @Test
    void getWriterWithWrongConfig() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new CSVWriterSupplier().getWriter(null, new ContentFormat() {
                }));
    }
}
