package com.xflprflx.paycheck.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xflprflx.paycheck.domain.TransportDocument;
import com.xflprflx.paycheck.domain.dtos.TransportDocumentDTO;
import com.xflprflx.paycheck.services.TransportDocumentService;

@RestController
@RequestMapping(value = "/transportDocuments")
public class TransportDocumentController {

	@Autowired
	private TransportDocumentService transportDocumentService;
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<TransportDocumentDTO> findById(@PathVariable Integer id) {
		TransportDocument obj = transportDocumentService.findById(id);
		return ResponseEntity.ok().body(new TransportDocumentDTO(obj, obj.getInvoices()));
	}
}
