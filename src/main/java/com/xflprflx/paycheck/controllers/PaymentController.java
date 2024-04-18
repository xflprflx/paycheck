package com.xflprflx.paycheck.controllers;

import com.xflprflx.paycheck.domain.Payment;
import com.xflprflx.paycheck.domain.dtos.PaymentDTO;
import com.xflprflx.paycheck.domain.dtos.TransportDocumentDTO;
import com.xflprflx.paycheck.services.ApachePdfService;
import com.xflprflx.paycheck.services.PaymentService;
import com.xflprflx.paycheck.services.PdfService;
import com.xflprflx.paycheck.services.TabulaPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping(value = "/file")
    public ResponseEntity<List<PaymentDTO>>  readPaymentFile(@RequestParam("file")MultipartFile file) throws IOException {
        List<PaymentDTO> payments = pdfService.pdfToPaymentObject(file);
        return ResponseEntity.ok().body(payments);
    }

    @PostMapping(value = "/list")
    public ResponseEntity<String> postPaymentList(@RequestBody List<Payment> payments) throws IOException {
        paymentService.savePayments(payments);
        return ResponseEntity.ok().body("Pagamentos salvos com sucesso.");
    }

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> findAll() {
        List<PaymentDTO> paymentDTOS = paymentService.findAll();
        return ResponseEntity.ok().body(paymentDTOS);
    }

    @GetMapping(value = "/filtered")
    public ResponseEntity<List<PaymentDTO>> findAllFiltered(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate  issueEnd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scannedStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate  scannedEnd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate forecastScStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate  forecastScEnd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate forecastApprStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate forecastApprEnd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate approvalStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate approvalEnd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentEnd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Integer paymentStatus) {
        List<PaymentDTO> paymentDTOS =
                paymentService.findAllFiltered(issueStart, issueEnd,
                        scannedStart, scannedEnd, forecastScStart, forecastScEnd,
                        forecastApprStart, forecastApprEnd, approvalStart, approvalEnd,
                        paymentStart, paymentEnd, paymentStatus);
        return ResponseEntity.ok().body(paymentDTOS);
    }

    @GetMapping(value = "/paymentsWithoutDoc")
    public ResponseEntity<List<Payment>> findPaymentsWithoutDoc() {
        List<Payment> docs = paymentService.findPaymentsWithoutDoc();
        return ResponseEntity.ok().body(docs);
    }
}


