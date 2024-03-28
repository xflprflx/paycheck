package com.xflprflx.paycheck.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xflprflx.paycheck.domain.TransportDocument;
import com.xflprflx.paycheck.repositories.TransportDocumentRepository;

@Service
public class TransportDocumentService {

	@Autowired
	private TransportDocumentRepository transportDocumentRepository;
	
	public TransportDocument findById(Integer id) {
		Optional<TransportDocument> obj = transportDocumentRepository.findById(id);
		return obj.orElse(null);
	}
}
