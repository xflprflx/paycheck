package com.xflprflx.paycheck.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xflprflx.paycheck.domain.TransportDocument;
import com.xflprflx.paycheck.repositories.TransportDocumentRepository;
import com.xflprflx.paycheck.services.exceptions.ObjectNotFoundException;

@Service
public class TransportDocumentService {

	@Autowired
	private TransportDocumentRepository transportDocumentRepository;
	
	public TransportDocument findById(Integer id) {
		Optional<TransportDocument> obj = transportDocumentRepository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Documento não encontrado! Id: " + id));
	}

	public List<TransportDocument> findAll() {
		return transportDocumentRepository.findAll();
	}
}
