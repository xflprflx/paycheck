package com.xflprflx.paycheck.services;

import com.xflprflx.paycheck.domain.Invoice;
import com.xflprflx.paycheck.domain.Payment;
import com.xflprflx.paycheck.domain.TransportDocument;
import com.xflprflx.paycheck.domain.dtos.InvoiceDTO;
import com.xflprflx.paycheck.domain.dtos.TransportDocumentDTO;
import com.xflprflx.paycheck.domain.enums.PaymentStatus;
import com.xflprflx.paycheck.factory.FileProcessorFactory;
import com.xflprflx.paycheck.repositories.InvoiceRepository;
import com.xflprflx.paycheck.repositories.PaymentRepository;
import com.xflprflx.paycheck.repositories.TransportDocumentRepository;
import com.xflprflx.paycheck.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransportDocumentServicePOC {

	@Autowired
	private TransportDocumentRepository transportDocumentRepository;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private PaymentForecastCalculatorService paymentForecastCalculatorService;

	@Transactional
	public void saveCteWithInvoice(List<TransportDocumentDTO> transportDocumentDTOS) {
		List<TransportDocument> transportDocuments = new ArrayList<>();

		// Consultar todas as faturas, documentos de transporte e pagamentos de uma vez
		List<Invoice> allInvoices = invoiceRepository.findAll();
		List<TransportDocument> allTransportDocuments = transportDocumentRepository.findAll();
		List<Payment> allPayments = paymentRepository.findAll();

		for (TransportDocumentDTO transportDocumentDTO : transportDocumentDTOS) {
			TransportDocument transportDocument = createOrUpdateTransportDocument(transportDocumentDTO, allTransportDocuments);
			processInvoices(transportDocumentDTO, transportDocument, allInvoices);
			transportDocument = updateTransportDocumentPaymentForecast(transportDocument);
			associatePayment(transportDocumentDTO, transportDocument, allPayments);

			if (!transportDocument.getPaymentStatus().equals(PaymentStatus.DEBATE_PAYMENT)) {
				transportDocument.setPaymentStatus(PaymentStatus.updatePaymentStatus(transportDocument));
			}

			transportDocuments.add(transportDocument);
		}
		transportDocumentRepository.saveAll(transportDocuments);
	}

	// Modificamos o método createOrUpdateTransportDocument para receber todos os documentos de transporte previamente carregados
	private TransportDocument createOrUpdateTransportDocument(TransportDocumentDTO transportDocumentDTO, List<TransportDocument> allTransportDocuments) {
		TransportDocument transportDocument = new TransportDocument(transportDocumentDTO);
		Optional<TransportDocument> existingTransportDocument = allTransportDocuments.stream()
				.filter(td -> td.getNumber().equals(transportDocumentDTO.getNumber()) &&
						td.getSerie().equals(transportDocumentDTO.getSerie()) &&
						td.getIssueDate().equals(transportDocumentDTO.getIssueDate()))
				.findFirst();
		existingTransportDocument.ifPresent(existing -> {
			transportDocument.setId(existing.getId());
			transportDocument.setPaymentStatus(existing.getPaymentStatus());
			transportDocument.setReasonReduction(existing.getReasonReduction());
		});
		return transportDocument;
	}

	// Modificamos o método associatePayment para receber todos os pagamentos previamente carregados
	private void associatePayment(TransportDocumentDTO transportDocumentDTO, TransportDocument transportDocument, List<Payment> allPayments) {
		Optional<Payment> payment = allPayments.stream()
				.filter(p -> p.getNumber().equals(transportDocumentDTO.getNumber()))
				.findFirst();
		payment.ifPresent(transportDocument::setPayment);
	}

	private void processInvoices(TransportDocumentDTO transportDocumentDTO, TransportDocument transportDocument, List<Invoice> allInvoices) {
		for (InvoiceDTO invoiceDTO : transportDocumentDTO.getInvoices()) {
			Invoice invoice = allInvoices.stream()
					.filter(inv -> inv.getNumber().equals(invoiceDTO.getNumber()))
					.findFirst()
					.orElseGet(() -> invoiceService.save(new Invoice(invoiceDTO)));
			transportDocument.getInvoices().add(invoice);
		}
	}

	public TransportDocument updateTransportDocumentPaymentForecast(TransportDocument transportDocument) {
		boolean allScanned = isAllScanned(transportDocument);
		boolean allApproved = isAllApproved(transportDocument);
		if (allScanned || allApproved) {
			LocalDate newPaymentForecastByScannedDate = allScanned ? paymentForecastCalculatorService.calculateNewPaymentForecastByScannedDate(transportDocument) : transportDocument.getPaymentForecastByScannedDate();
			LocalDate newPaymentForecastByApprovalDate = allApproved ? paymentForecastCalculatorService.calculateNewPaymentForecastByPaymentApprovalDate(transportDocument) : transportDocument.getPaymentForecastByPaymentApprovalDate();
			transportDocument.setPaymentForecastByScannedDate(newPaymentForecastByScannedDate);
			transportDocument.setPaymentForecastByPaymentApprovalDate(newPaymentForecastByApprovalDate);
		}
		return transportDocument;
	}

	private boolean isAllScanned(TransportDocument transportDocument) {
		return transportDocument.getInvoices().stream().allMatch(inv -> inv.getScannedDate() != null);
	}

	private boolean isAllApproved(TransportDocument transportDocument) {
		return transportDocument.getInvoices().stream().allMatch(inv -> inv.getPaymentApprovalDate() != null);
	}

}
