package com.xflprflx.paycheck.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.xflprflx.paycheck.domain.Invoice;
import com.xflprflx.paycheck.domain.Payment;
import com.xflprflx.paycheck.domain.dtos.InvoiceDTO;
import com.xflprflx.paycheck.repositories.InvoiceRepository;
import com.xflprflx.paycheck.services.exceptions.DataIntegrityViolationException;
import org.springframework.beans.BeanUtils;
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
			var transportDocument = new TransportDocument(transportDocumentDTO);
			if (optionalTransportDocument.isPresent()) {
				var oldTransportDocument = optionalTransportDocument.get();
				transportDocument.setId(oldTransportDocument.getId());
			}
				transportDocument = transportDocumentRepository.save(transportDocument);
				transportDocumentDTO.setId(transportDocument.getId());

//			transportDocuments.add(new TransportDocument(transportDocumentDTO));
			//transportDocuments.add(BeanUtils.copyProperties(transportDocumentDTO, transportDocument));
		}
		//transportDocumentRepository.saveAll(transportDocuments);
    }



	public void updateByPayment(Payment payment) {
		Optional<TransportDocument> transportDocumentOptional = transportDocumentRepository.findByNumber(payment.getNumber());
		if (transportDocumentOptional.isPresent()){
			TransportDocument transportDocument = transportDocumentOptional.get();
			transportDocument.setPayment(payment);
			transportDocumentRepository.save(transportDocument);
		}
	}

	public List<TransportDocument> findAll() {
		return transportDocumentRepository.findAll();
	}
}
