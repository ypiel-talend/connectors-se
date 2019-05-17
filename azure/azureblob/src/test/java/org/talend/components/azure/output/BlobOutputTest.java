package org.talend.components.azure.output;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.talend.components.azure.common.FileFormat;
import org.talend.components.azure.common.excel.ExcelFormat;
import org.talend.components.azure.common.excel.ExcelFormatOptions;
import org.talend.components.azure.common.exception.BlobRuntimeException;
import org.talend.components.azure.dataset.AzureBlobDataset;
import org.talend.components.azure.service.MessageService;

public class BlobOutputTest {

    @Test
    public void testHTMLOutputNotSupported() {
        ExcelFormatOptions excelFormatOptions = new ExcelFormatOptions();
        excelFormatOptions.setExcelFormat(ExcelFormat.HTML);

        AzureBlobDataset dataset = new AzureBlobDataset();
        dataset.setFileFormat(FileFormat.EXCEL);
        dataset.setExcelOptions(excelFormatOptions);
        BlobOutputConfiguration outputConfiguration = new BlobOutputConfiguration();
        outputConfiguration.setDataset(dataset);

         BlobOutput output = new BlobOutput(outputConfiguration, null, Mockito.mock(MessageService.class));
         Assertions.assertThrows(BlobRuntimeException.class, output::init);
    }
}