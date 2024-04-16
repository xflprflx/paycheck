package com.xflprflx.paycheck.factory;

import com.xflprflx.paycheck.services.CsvFileProcessor;
import com.xflprflx.paycheck.services.ExcelFileProcessor;
import com.xflprflx.paycheck.services.FileProcessor;

public class FileProcessorFactory {

    public static FileProcessor getFileProcessor(String filename) {
        if(filename != null && (filename.endsWith(".xlsx") || filename.endsWith(".xls"))) {
            return new ExcelFileProcessor();
        } else if (filename != null && filename.endsWith(".csv"))  {
            return new CsvFileProcessor();
        } else {
            throw new IllegalArgumentException("Tipo de arquivo não suportado.");
        }
    }
}
