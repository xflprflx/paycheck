package com.xflprflx.paycheck.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.xflprflx.paycheck.domain.Invoice;
import com.xflprflx.paycheck.domain.dtos.InvoiceDTO;
import com.xflprflx.paycheck.repositories.InvoiceRepository;
import com.xflprflx.paycheck.services.exceptions.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xflprflx.paycheck.domain.TransportDocument;
import com.xflprflx.paycheck.domain.dtos.TransportDocumentDTO;
import com.xflprflx.paycheck.repositories.TransportDocumentRepository;
import com.xflprflx.paycheck.services.exceptions.ObjectNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransportDocumentService {

	@Autowired
	private TransportDocumentRepository transportDocumentRepository;

	@Autowired
	private InvoiceRepository invoiceRepository;

	public TransportDocument findById(Integer id) {
		Optional<TransportDocument> obj = transportDocumentRepository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Documento não encontrado! Id: " + id));
	}

	public List<TransportDocument> findAll() {
		return transportDocumentRepository.findAll();
	}

	public TransportDocument create(TransportDocumentDTO transportDocumentDTO) {
		transportDocumentDTO.setId(null);
		validByNumber(transportDocumentDTO);
		TransportDocument transportDocument = new TransportDocument(transportDocumentDTO);
		return transportDocumentRepository.save(transportDocument);
	}

	public TransportDocument update(Integer id, TransportDocumentDTO transportDocumentDTO) {
		transportDocumentDTO.setId(id);
		TransportDocument obj = findById(id);
		validByNumber(transportDocumentDTO);
		obj = new TransportDocument(transportDocumentDTO);
		return transportDocumentRepository.save(obj);
	}


	private void validByNumber(TransportDocumentDTO transportDocumentDTO) {
		Optional<TransportDocument> transportDocument = transportDocumentRepository.findByNumber(transportDocumentDTO.getNumber());
		if (transportDocument.isPresent() && transportDocument.get().getId() != transportDocumentDTO.getId()) {
			throw new DataIntegrityViolationException("Documento já cadastrado no sistema");
		}
	}

	@Transactional
    public void saveCteWithInvoice(List<TransportDocumentDTO> transportDocumentDTOS) {
		List<TransportDocument> transportDocuments = new ArrayList<>();
		for (TransportDocumentDTO transportDocumentDTO : transportDocumentDTOS) {
			for (InvoiceDTO invoiceDTO : transportDocumentDTO.getInvoices()) {
				Optional<Invoice> optionalInvoice = invoiceRepository.findByNumber(invoiceDTO.getNumber());
				if(optionalInvoice.isPresent()) {
					var oldInvoice = optionalInvoice.get();
					var invoice = new Invoice(invoiceDTO);
					invoice.setId(oldInvoice.getId());
					invoice = invoiceRepository.save(invoice);
					invoiceDTO.setId(invoice.getId());
				} else {
					var invoice = new Invoice(invoiceDTO);
					invoice = invoiceRepository.save(invoice);
					invoiceDTO.setId(invoice.getId());
				}
			}
			Optional<TransportDocument> optionalTransportDocument = transportDocumentRepository.findByNumber(transportDocumentDTO.getNumber());
			if (optionalTransportDocument.isPresent()) {
				var oldTransportDocument = optionalTransportDocument.get();
				var transportDocument = new TransportDocument(transportDocumentDTO);
				transportDocument.setId(oldTransportDocument.getId());
				transportDocument = transportDocumentRepository.save(transportDocument);
				transportDocumentDTO.setId(transportDocument.getId());
			} else {
				var transportDocument = new TransportDocument(transportDocumentDTO);
				transportDocument = transportDocumentRepository.save(transportDocument);
				transportDocumentDTO.setId(transportDocument.getId());
			}
			transportDocuments.add(new TransportDocument(transportDocumentDTO));
		}
		transportDocumentRepository.saveAll(transportDocuments);
    }
}
