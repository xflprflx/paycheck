package com.xflprflx.paycheck.controllers;

import com.xflprflx.paycheck.domain.dtos.InvoiceDTO;
import com.xflprflx.paycheck.services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/invoices")
@CrossOrigin(origins = "*")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping(value = "/file")
    public ResponseEntity<List<InvoiceDTO>> readInvoiceFile(@RequestParam("file") MultipartFile file) throws IOException {
        List<InvoiceDTO> invoices = invoiceService.returnInvoiceListFromFile(file);
        return ResponseEntity.ok().body(invoices);
    }

    @PostMapping(value = "/list")
    public ResponseEntity<String> postInvoiceList(@Valid @RequestBody List<InvoiceDTO> invoiceDTOS) {
        invoiceService.saveInvoices(invoiceDTOS);
        return ResponseEntity.ok().body("NF-es salvos com sucesso.");
    }
}
