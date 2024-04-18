package com.xflprflx.paycheck.services;

import com.xflprflx.paycheck.domain.dtos.InvoiceDTO;
import com.xflprflx.paycheck.domain.dtos.TransportDocumentDTO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CsvFileProcessor implements FileProcessor {

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


    @Override
    public List<TransportDocumentDTO> returnTransportDocumentListFromFile(MultipartFile file) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        InputStream inputStream = file.getInputStream();
        List<TransportDocumentDTO> transportDocuments = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             CSVParser parser = new CSVParser(bufferedReader,
                     CSVFormat.DEFAULT.withDelimiter(';'))) {

            List<String> lines = bufferedReader.lines().collect(Collectors.toList());
            List<String> processedLines = new ArrayList<>();

            // Processar as linhas para substituir vírgulas por pontos em valores numéricos
            for (String line : lines) {
                processedLines.add(line.replaceAll("(?<=\\d),(?=\\d{2}(?:\\.)?\\d+)", "."));
            }


            // Recriar o BufferedReader com as linhas processadas
            try (BufferedReader processedReader = new BufferedReader(new StringReader(String.join(System.lineSeparator(), processedLines)))) {
                CSVParser parser2 = new CSVParser(processedReader,
                        CSVFormat.DEFAULT.withDelimiter(';'));

                for (CSVRecord record : parser2) {
                    if (record.getRecordNumber() != 1) {


                        System.out.println(record);
                        // Processar cada registro e criar objetos TransportDocumentDTO
                        String number = record.get(0);
                        String serie = record.get(1);
                        LocalDate issueDate = record.get(2).length() > 0 ? LocalDate.parse(record.get(2).split(" ")[0], formatter) : null;
                        String addressShipper = record.get(3);

                        System.out.println(record.get(4));
//                    Double amount = Double.parseDouble(record.get(4).replace(".", ""));
                        Double amount = record.get(4).length() > 0 ? (Double.valueOf(record.get(4).toString().replaceAll("\\.", "").replace(",", "."))) : null;
                        System.out.println(amount);
                        String[] invoices = record.get(4).length() > 0 ? record.get(5).split(", ") : new String[0];

                        TransportDocumentDTO transportDocument = new TransportDocumentDTO();
                        transportDocument.setNumber(number);
                        transportDocument.setSerie(serie);
                        transportDocument.setIssueDate(issueDate);
                        transportDocument.setAddressShipper(addressShipper);
                        transportDocument.setAmount(amount);
                        System.out.println(transportDocument.getAmount());
                        System.out.println("notas "+invoices.length);
                        Arrays.stream(invoices).forEach(x -> {
                            var inv = new InvoiceDTO();
                            inv.setNumber(x);
                            transportDocument.getInvoices().add(inv);
                        });
                        transportDocuments.add(transportDocument);
                    }
                }
            }
        } catch (IOException | DateTimeParseException e) {
            e.printStackTrace();
        }

        return transportDocuments;
    }

/*    // Método para converter uma string que representa um valor numérico em double
    private Double parseAmountString(String amountStr) {
        // Substituir vírgulas que estão entre dígitos numéricos por pontos
        amountStr = amountStr.replaceAll("(?<=\\d),(?=\\d)", ".");

        // Converter a string para double
        return Double.parseDouble(amountStr);
    }*/

}


