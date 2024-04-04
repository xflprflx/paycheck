package com.xflprflx.paycheck.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.xflprflx.paycheck.domain.TransportDocument;
import com.xflprflx.paycheck.domain.dtos.TransportDocumentDTO;
import com.xflprflx.paycheck.services.TransportDocumentService;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/transportDocuments")
@CrossOrigin(origins = "*")
public class TransportDocumentController {

	@Autowired
	private TransportDocumentService transportDocumentService;

	@PostMapping(value = "/list")
	public ResponseEntity<String> postTransportDocumentList(@Valid @RequestBody List<TransportDocumentDTO> transportDocumentDTOS) {
		try {
			transportDocumentService.saveCteWithInvoice(transportDocumentDTOS);
			return ResponseEntity.ok().body("CT-es salvos com sucesso.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erro ao salvar CT-es: " + e.getMessage());
		}
	}
}
