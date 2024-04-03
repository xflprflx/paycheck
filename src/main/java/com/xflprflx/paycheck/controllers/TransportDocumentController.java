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

	/*@GetMapping(value = "/teste")
	public ResponseEntity<String> teste() {
		return ResponseEntity.ok().body("Testeeeee");
	}


	@GetMapping(value = "/{id}")
	public ResponseEntity<TransportDocumentDTO> findById(@PathVariable Integer id) {
		TransportDocument obj = transportDocumentService.findById(id);
		return ResponseEntity.ok().body(new TransportDocumentDTO(obj, obj.getInvoices()));
	}

	@GetMapping
	public ResponseEntity<List<TransportDocumentDTO>> findAll() {
		List<TransportDocument> list = transportDocumentService.findAll();
		List<TransportDocumentDTO> listDto = list.stream().map(obj -> new TransportDocumentDTO(obj, obj.getInvoices()))
				.collect(Collectors.toList());
		return ResponseEntity.ok().body(listDto);
	}

	@PostMapping
	public ResponseEntity<TransportDocumentDTO> create(@Valid @RequestBody TransportDocumentDTO transportDocumentDTO) {
		TransportDocument newObj = transportDocumentService.create(transportDocumentDTO);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newObj.getId()).toUri();
		return ResponseEntity.created(uri).build();
 	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<TransportDocumentDTO> update(@PathVariable Integer id, @Valid @RequestBody TransportDocumentDTO transportDocumentDTO) {
		TransportDocument obj = transportDocumentService.update(id, transportDocumentDTO);
		return ResponseEntity.ok().body(new TransportDocumentDTO(obj));
	}*/

	@PostMapping(value = "/list")
	public ResponseEntity<String> postTransportDocumentList(@Valid @RequestBody List<TransportDocumentDTO> transportDocumentDTOS) {
		try {
			transportDocumentService.saveCteWithInvoice(transportDocumentDTOS);
			return ResponseEntity.ok().body("CT-es salvos com sucesso.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erro ao salvar CT-es: " + e.getMessage());
		}
	}
}
