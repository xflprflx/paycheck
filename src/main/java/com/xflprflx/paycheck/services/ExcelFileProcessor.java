package com.xflprflx.paycheck.services;

import com.xflprflx.paycheck.domain.dtos.InvoiceDTO;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ExcelFileProcessor implements FileProcessor{

    @Override
    public List<InvoiceDTO> returnInvoiceListFromFile(MultipartFile file) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        InputStream inputStream = file.getInputStream();
        List<InvoiceDTO> invoices = new ArrayList<>();
        try(Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            List<Row> rows = (List<Row>) toList(sheet.iterator());

            rows.forEach(row -> {
                if(row.getRowNum() != 0) {
                    List<Cell> cells = (List<Cell>) toList(row.cellIterator());
                    Integer length = cells.stream().toArray().length;

                    String number = length > 0 ? processCell(cells.get(0)) : null;
                    LocalDate scannedDate = length > 1 ? LocalDate.parse(processCell(cells.get(1)), formatter) : null;
                    LocalDate paymentApprovalDate = length > 2 ?LocalDate.parse(processCell(cells.get(2)), formatter) : null;

                    InvoiceDTO invoice = new InvoiceDTO();
                    invoice.setNumber(number);
                    invoice.setScannedDate(scannedDate);
                    invoice.setPaymentApprovalDate(paymentApprovalDate);
                    invoices.add(invoice);
                }
            });

            return invoices;
        }
    }

    private List<?> toList(Iterator<?> iterator) {
        return IteratorUtils.toList(iterator);
    }

    private String processCell(Cell cell) {
        String value;
        if(cell.getCellType() == CellType.NUMERIC) {
            if(DateUtil.isCellDateFormatted(cell)) {
                Date dateValue = cell.getDateCellValue();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                value = dateFormat.format(dateValue);
                System.out.println(value);
            } else {
                value = String.valueOf((int) cell.getNumericCellValue());
            }
        } else {
            value = cell.toString();
        }
        return value;
    }
}
