package com.xflprflx.paycheck.services;

import com.xflprflx.paycheck.domain.dtos.InvoiceDTO;
import com.xflprflx.paycheck.domain.dtos.TransportDocumentDTO;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    @Override
    public List<TransportDocumentDTO> returnTransportDocumentListFromFile(MultipartFile file) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        InputStream inputStream = file.getInputStream();
        List<TransportDocumentDTO> transportDocuments = new ArrayList<>();
        try(Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            sheet.forEach(row -> {
                if (row.getRowNum() != 0) {
                    List<Cell> cells = new ArrayList<>();
                    row.forEach(cell -> {
                        if (cell != null && cell.getCellType() != CellType.BLANK) {
                            cells.add(cell);
                        }
                    });
                    Integer length = cells.size();

                    String number = length > 0 ? processCell(cells.get(0)) : null;
                    String serie = length > 1 ? processCell(cells.get(1)) : null;
                    LocalDate issueDate = length > 2 ? LocalDate.parse(processCell(cells.get(2)), formatter) : null;
                    String addressShipper = length > 3 ? processCell(cells.get(3)) : null;
                    Double amount = length > 4 ? Double.parseDouble(processCell(cells.get(4))) : null;
                    String invoice = length > 5 ? processCell(cells.get(5)) : null;
                    String[] invoices = invoice != null ? invoice.split(", ") : new String[0];

                    TransportDocumentDTO transportDocument = new TransportDocumentDTO();
                    transportDocument.setNumber(number);
                    transportDocument.setSerie(serie);
                    transportDocument.setIssueDate(issueDate);
                    transportDocument.setAddressShipper(addressShipper);
                    transportDocument.setAmount(amount);
                    for (String x : invoices) {
                        var inv = new InvoiceDTO();
                        inv.setNumber(x);
                        transportDocument.getInvoices().add(inv);
                    }
                    transportDocuments.add(transportDocument);
                }
            });


            return transportDocuments;
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
