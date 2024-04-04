package com.xflprflx.paycheck.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.xflprflx.paycheck.domain.Invoice;
import com.xflprflx.paycheck.domain.Payment;
import com.xflprflx.paycheck.domain.dtos.InvoiceDTO;
import com.xflprflx.paycheck.domain.dtos.PaymentDTO;
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
	private InvoiceService invoiceService;

	@Autowired
	private PaymentService paymentService;

	@Transactional(readOnly = true)
	public Optional<TransportDocument> findTransportDocumentByNumber(String numberTransportDocument) {
		return transportDocumentRepository.findByNumber(numberTransportDocument);
	}

	@Transactional
	public void saveCteWithInvoice(List<TransportDocumentDTO> transportDocumentDTOS) {
		List<TransportDocument> transportDocuments = new ArrayList<>();
		for (TransportDocumentDTO transportDocumentDTO : transportDocumentDTOS) {
			TransportDocument transportDocument = createOrUpdateTransportDocument(transportDocumentDTO);
			processInvoices(transportDocumentDTO, transportDocument);
			associatePayment(transportDocumentDTO, transportDocument);
			transportDocuments.add(transportDocument);
		}
		transportDocumentRepository.saveAll(transportDocuments);
	}

	private TransportDocument createOrUpdateTransportDocument(TransportDocumentDTO transportDocumentDTO) {
		TransportDocument transportDocument = new TransportDocument(transportDocumentDTO);
		findTransportDocumentByNumber(transportDocumentDTO.getNumber())
				.ifPresent(existingTransportDocument -> transportDocument.setId(existingTransportDocument.getId()));
		return transportDocument;
	}

	private void processInvoices(TransportDocumentDTO transportDocumentDTO, TransportDocument transportDocument) {
		for (InvoiceDTO invoiceDTO : transportDocumentDTO.getInvoices()) {
			Invoice invoice = invoiceService.findByNumber(invoiceDTO.getNumber())
					.orElseGet(() -> invoiceService.save(new Invoice(invoiceDTO)));
			transportDocument.getInvoices().add(invoice);
		}
	}

	private void associatePayment(TransportDocumentDTO transportDocumentDTO, TransportDocument transportDocument) {
		paymentService.findPaymentByNumber(transportDocumentDTO.getNumber())
				.ifPresent(transportDocument::setPayment);
	}

	//todo verificar
	public void updateByPayment(Payment payment) {
		Optional<TransportDocument> transportDocumentOptional = transportDocumentRepository.findByNumber(payment.getNumber());
		if (transportDocumentOptional.isPresent()){
			TransportDocument transportDocument = transportDocumentOptional.get();
			transportDocument.setPayment(payment);
			transportDocumentRepository.save(transportDocument);
		}
	}
}
