package com.xflprflx.paycheck.controllers;

import com.xflprflx.paycheck.domain.dtos.InvoiceDTO;
import com.xflprflx.paycheck.domain.dtos.TransportDocumentDTO;
import com.xflprflx.paycheck.services.InvoiceService;
import com.xflprflx.paycheck.services.TransportDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/invoices")
@CrossOrigin(origins = "*")
public class InvoiceController {

	@Autowired
	private InvoiceService invoiceService;

	@PostMapping(value = "/list")
	public ResponseEntity<String> postInvoiceList(@Valid @RequestBody List<InvoiceDTO> invoiceDTOS) {
		try {
			invoiceService.saveInvoices(invoiceDTOS);
			return ResponseEntity.ok().body("NF-es salvos com sucesso.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erro ao salvar NF-es: " + e.getMessage());
		}
	}
}
