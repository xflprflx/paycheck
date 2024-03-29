package com.xflprflx.paycheck.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

	@GetMapping
	public ResponseEntity<List<TransportDocumentDTO>> findAll() {
		List<TransportDocument> list = transportDocumentService.findAll();
		List<TransportDocumentDTO> listDto = list.stream().map(obj -> new TransportDocumentDTO(obj, obj.getInvoices()))
				.collect(Collectors.toList());
		return ResponseEntity.ok().body(listDto);
	}

	@PostMapping
	public ResponseEntity<TransportDocumentDTO> create(@RequestBody TransportDocumentDTO transportDocumentDTO) {
		TransportDocument newObj = transportDocumentService.create(transportDocumentDTO);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newObj.getId()).toUri();
		return ResponseEntity.created(uri).build();
 	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
