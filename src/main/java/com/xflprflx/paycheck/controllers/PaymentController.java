package com.xflprflx.paycheck.controllers;

import com.xflprflx.paycheck.domain.Payment;
import com.xflprflx.paycheck.domain.dtos.PaymentDTO;
import com.xflprflx.paycheck.services.ApachePdfService;
import com.xflprflx.paycheck.services.PaymentService;
import com.xflprflx.paycheck.services.PdfService;
import com.xflprflx.paycheck.services.TabulaPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<String> postPaymentList(@RequestParam("file")MultipartFile file) throws IOException {
        //List<Payment> payments = pdfService.tableFromPdfToPaymentObject(file);
        List<Payment> payments = pdfService.pdfToPaymentObject(file);
        paymentService.savePayments(payments);
        return ResponseEntity.ok().body("Pagamentos salvos com sucesso.");
    }
}


