package com.xflprflx.paycheck.services;

import com.xflprflx.paycheck.domain.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class PdfService {

    @Autowired
    private TabulaPdfService tabulaPdfService;

    @Autowired
    private ApachePdfService apachePdfService;

    public List<Payment> pdfToPaymentObject(MultipartFile file) throws IOException {
        InputStream in = file.getInputStream();
        String date = apachePdfService.extractDate(file);
        return tabulaPdfService.tableFromPdfToPaymentObject(in, date);
    }
}
