package com.xflprflx.paycheck.controllers;

import com.xflprflx.paycheck.domain.Payment;
import com.xflprflx.paycheck.domain.dtos.PaymentDTO;
import com.xflprflx.paycheck.services.PaymentService;
import com.xflprflx.paycheck.services.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
    public ResponseEntity<List<PaymentDTO>> postPaymentList(@RequestParam("file")MultipartFile file) {
        List<Payment> payments = pdfService.tableFromPdfToPaymentObject(file);
        List<PaymentDTO> paymentDTOS = paymentService.savePayments(payments);
        return ResponseEntity.ok().body(paymentDTOS);
    }
}


