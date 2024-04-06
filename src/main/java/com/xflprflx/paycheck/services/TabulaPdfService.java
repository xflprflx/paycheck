package com.xflprflx.paycheck.services;

import com.xflprflx.paycheck.domain.Payment;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class TabulaPdfService {

    public List<Payment> tableFromPdfToPaymentObject(InputStream in, String date) throws IOException {
        try (PDDocument document = PDDocument.load(in)) {
            List<Payment> payments = new ArrayList<>();
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
            PageIterator pi = new ObjectExtractor(document).extract();
            while (pi.hasNext()) {
                // iterate over the pages of the document
                Page page = pi.next();
                List<Table> table = sea.extract(page);
                // iterate over the tables of the page
                for (Table tables : table) {
                    List<List<RectangularTextContainer>> rows = tables.getRows();
                    boolean isHeaderRow = true;
                    // iterate over the rows of the table
                    for (List<RectangularTextContainer> cells : rows) {
                        if (isHeaderRow) {
                            // Pular a primeira linha se for o cabeçalho
                            isHeaderRow = false;
                            continue;
                        }

                        if (cells.get(0).getText().contains("Subtotal")){
                            continue;
                        }

                        if (cells.get(0).getText().contains("Total Geral")){
                            continue;
                        }

                        var payment = cellsToPayment(cells, date);
                        payments.add(payment);
                        // print all column-cells of the row plus linefeed
/*                        for (RectangularTextContainer content : cells) {
                            // Note: Cell.getText() uses \r to concat text chunks
                            String text = content.getText().replace("\r", " ");
                            System.out.println("------------");
                            System.out.println(text);
                            System.out.println("------------");
                            //System.out.print(text + "|");
                        }
                        System.out.println();*/
                    }
                }

            }
            return payments;
        }
    }

    private Payment cellsToPayment(List<RectangularTextContainer> cells, String date) {
        Payment payment = new Payment();
        if (cells.get(0).getText().toString().contains("-")){
            payment.setNumber(cells.get(0).getText().toString().split("-")[0]);
            payment.setSerie(cells.get(0).getText().toString().split("-")[1]);
        } else {
            payment.setNumber(cells.get(0).getText().toString());
            payment.setSerie("-");
        }
        payment.setInvoiceSG(cells.get(1).getText().toString());
        payment.setMyInvoice(cells.get(2).getText().toString());
        payment.setDocCompensation(cells.get(3).getText().toString());
        payment.setAmount(Double.valueOf(cells.get(4).getText().toString().replaceAll("\\.", "").replace(",", ".")));
        payment.setText(cells.get(5).getText().toString());
        payment.setPaymentDate(date);
        System.out.println(payment);
        return payment;
    }
}