package com.xflprflx.paycheck.services;

import com.xflprflx.paycheck.domain.dtos.PaymentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfService {

    @Autowired
    private TabulaPdfService tabulaPdfService;

    @Autowired
    private ApachePdfService apachePdfService;

    public List<PaymentDTO> pdfToPaymentObject(MultipartFile file) throws IOException {
        InputStream in = file.getInputStream();
        String date = apachePdfService.extractDate(file);
        List<PaymentDTO> payments = tabulaPdfService.tableFromPdfToPaymentObject(in, date).stream().map(x -> new PaymentDTO(x)).collect(Collectors.toList());
        return payments;
    }
}
