package com.xflprflx.paycheck.services;

import com.xflprflx.paycheck.domain.dtos.InvoiceDTO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CsvFileProcessor implements FileProcessor {

    /*@Override
    public List<InvoiceDTO> returnInvoiceListFromFile(MultipartFile file) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        InputStream inputStream = file.getInputStream();
        List<InvoiceDTO> invoices = new ArrayList<>();
        try(CSVParser parser = new CSVParser(new InputStreamReader(inputStream), CSVFormat.DEFAULT)) {
            for(CSVRecord record : parser) {
                if (record.getRecordNumber() != 1) {
                    String[] fields = record.get(0).split(";");
                    String number = fields.length > 0 ? fields[0] : null;
                    LocalDate scannedDate = fields.length > 1 && !fields[1].isEmpty() ? LocalDate.parse(fields[1].split(" ")[0], formatter) : null;
                    LocalDate paymentApprovalDate = fields.length > 2 && !fields[2].isEmpty() ? LocalDate.parse(fields[2], formatter) : null;

                    InvoiceDTO invoice = new InvoiceDTO();
                    invoice.setNumber(number);
                    invoice.setScannedDate(scannedDate);
                    invoice.setPaymentApprovalDate(paymentApprovalDate);
                    invoices.add(invoice);
                }
            }
        }
        return invoices;
    }*/

    @Override
    public List<InvoiceDTO> returnInvoiceListFromFile(MultipartFile file) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        InputStream inputStream = file.getInputStream();
        List<InvoiceDTO> invoices = new ArrayList<>();
        try (CSVParser parser = new CSVParser(new InputStreamReader(inputStream), CSVFormat.DEFAULT)) {
            for (CSVRecord record : parser) {
                if (record.getRecordNumber() != 1) {
                    String[] fields = record.get(0).split(";");
                    String number = fields.length > 0 ? fields[0] : null;
                    LocalDate scannedDate = null;
                    LocalDate paymentApprovalDate = null;

                    if (fields.length > 1 && !fields[1].isEmpty()) {
                        scannedDate = LocalDate.parse(fields[1].split(" ")[0], formatter);
                    }
                    System.out.println(scannedDate);

                    if (fields.length > 2 && !fields[2].isEmpty()) {
                        paymentApprovalDate = LocalDate.parse(fields[2], formatter);
                    }
                    System.out.println(paymentApprovalDate);

                    InvoiceDTO invoice = new InvoiceDTO();
                    invoice.setNumber(number);
                    invoice.setScannedDate(scannedDate);
                    invoice.setPaymentApprovalDate(paymentApprovalDate);
                    invoices.add(invoice);
                }
            }
        } catch (DateTimeParseException e) {
            // Tratar exceção de formatação inválida de data
            System.err.println("Erro ao formatar data: " + e.getMessage());
        }

        return invoices;
    }

}
