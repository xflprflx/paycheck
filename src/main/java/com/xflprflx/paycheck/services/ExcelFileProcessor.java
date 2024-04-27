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
        List<TransportDocumentDTO> transportDocuments = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    TransportDocumentDTO transportDocument = new TransportDocumentDTO();
                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        Cell cell = row.getCell(j);
                        if (cell != null) {
                            String cellValue = processCell(cell);
                            switch (j) {
                                case 0:
                                    transportDocument.setNumber(cellValue);
                                    break;
                                case 1:
                                    transportDocument.setSerie(cellValue);
                                    break;
                                case 2:
                                    if(cell.getCellType() != CellType.BLANK)
                                        transportDocument.setIssueDate(LocalDate.parse(cellValue, formatter));
                                    break;
                                case 3:
                                    transportDocument.setAddressShipper(cellValue);
                                    break;
                                case 4:
                                    try {
                                        transportDocument.setAmount(Double.parseDouble(cellValue));
                                    } catch (NumberFormatException e) {
                                        // Handle invalid amount format
                                        transportDocument.setAmount(null);
                                    }
                                    break;
                                case 5:
                                    String[] invoices = cellValue.split(", ");
                                    for (String invoice : invoices) {
                                        var inv = new InvoiceDTO();
                                        inv.setNumber(invoice);
                                        transportDocument.getInvoices().add(inv);
                                    }
                                    break;
                                default:
                                    // Ignore additional cells if any
                                    break;
                            }
                        }
                    }
                    transportDocuments.add(transportDocument);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return transportDocuments;
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
            }
            else {
                value = cell.getNumericCellValue() == Math.floor(cell.getNumericCellValue()) ? String.valueOf((int) cell.getNumericCellValue()) : String.valueOf(cell.getNumericCellValue());
            }
        } else {
            value = cell.toString();
        }
        return value;
    }
}
