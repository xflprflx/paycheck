package com.xflprflx.paycheck.services;

import com.xflprflx.paycheck.domain.Invoice;
import com.xflprflx.paycheck.domain.Payment;
import com.xflprflx.paycheck.domain.TransportDocument;
import com.xflprflx.paycheck.domain.dtos.InvoiceDTO;
import com.xflprflx.paycheck.domain.dtos.TransportDocumentDTO;
import com.xflprflx.paycheck.domain.enums.PaymentStatus;
import com.xflprflx.paycheck.factory.FileProcessorFactory;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransportDocumentService {

	@Autowired
	private TransportDocumentRepository transportDocumentRepository;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private PaymentForecastCalculatorService paymentForecastCalculatorService;

	@Transactional(readOnly = true)
	public List<TransportDocumentDTO> findAllTransportDocuments() {
		List<TransportDocument> transportDocuments = transportDocumentRepository.findAllWithPayment();
		return transportDocuments.stream()
				.map(x -> new TransportDocumentDTO(x, x.getInvoices()))
				.collect(Collectors.toList());
	}

	@Transactional
	public void saveCteWithInvoice(List<TransportDocumentDTO> transportDocumentDTOS) {
		List<TransportDocument> transportDocuments = new ArrayList<>();

		List<Invoice> allInvoices = invoiceService.findAll();
		List<TransportDocument> allTransportDocuments = transportDocumentRepository.findAll();
		List<Payment> allPayments = paymentService.findAll();

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

	private void associatePayment(TransportDocumentDTO transportDocumentDTO, TransportDocument transportDocument, List<Payment> allPayments) {
		Optional<Payment> payment = allPayments.stream()
				.filter(p -> p.getNumber().equals(transportDocumentDTO.getNumber()))
				.findFirst();
		payment.ifPresent(transportDocument::setPayment);
	}

	private void processInvoices(TransportDocumentDTO transportDocumentDTO, TransportDocument transportDocument, List<Invoice> allInvoices) {
		for (InvoiceDTO invoiceDTO : transportDocumentDTO.getInvoices()) {
			Optional<Invoice> existingInvoice = allInvoices.stream()
					.filter(inv -> inv.getNumber().equals(invoiceDTO.getNumber()))
					.findFirst();

			if (existingInvoice.isPresent()) {
				Invoice invoice = existingInvoice.get();
				transportDocument.getInvoices().add(invoice);
			} else {
				Invoice newInvoice = invoiceService.save(new Invoice(invoiceDTO));
				transportDocument.getInvoices().add(newInvoice);
				allInvoices.add(newInvoice);
			}
		}
	}

	public void updateByPayment(List<Payment> payments) {
		List<TransportDocument> allTransportDocuments = transportDocumentRepository.findAll();
		List<TransportDocument> transportDocumentsToUpdate = new ArrayList<>();
		for (Payment payment : payments) {
			Optional<TransportDocument> transportDocumentOptional = allTransportDocuments.stream()
					.filter(transportDocument -> transportDocument.getNumber().equals(payment.getNumber()) &&
							transportDocument.getSerie().equals(payment.getSerie()) &&
							transportDocument.getAmount().equals(payment.getAmount()))
					.findFirst();
			transportDocumentOptional.ifPresent(existing -> {
				existing.setPayment(payment);
				existing.setPaymentStatus(PaymentStatus.updatePaymentStatus(existing));
				transportDocumentsToUpdate.add(existing);
			});
		}
		transportDocumentRepository.saveAll(transportDocumentsToUpdate);
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

	@Transactional(readOnly = true)
	public TransportDocumentDTO findById(Integer id) {
		Optional<TransportDocument> optionalTransportDocument = transportDocumentRepository.findById(id);
		TransportDocument transportDocument = optionalTransportDocument.orElseThrow(() -> new ObjectNotFoundException("Documento não encontrado."));
		return new TransportDocumentDTO(transportDocument, transportDocument.getInvoices());
	}

	@Transactional
	public TransportDocumentDTO blockPayment(Integer id, String reasonReduction) {
		try {
			TransportDocument transportDocument = transportDocumentRepository.getOne(id);
			transportDocument.setId(id);
			transportDocument.setReasonReduction(reasonReduction);
			transportDocument.setPaymentStatus(PaymentStatus.DEBATE_PAYMENT);
			transportDocument = transportDocumentRepository.save(transportDocument);
			return new TransportDocumentDTO(transportDocument);
		}catch (EntityNotFoundException e) {
			throw new ObjectNotFoundException("Documento não encontrado");
		}
	}

	@Transactional
	public TransportDocumentDTO unlockPayment(Integer id, Integer paymentStatus) {
		try {
			TransportDocument transportDocument = transportDocumentRepository.getOne(id);
			transportDocument.setId(id);
			transportDocument.setReasonReduction("");
			transportDocument.setPaymentStatus(PaymentStatus.toEnum(paymentStatus));
			transportDocument = transportDocumentRepository.save(transportDocument);
			return new TransportDocumentDTO(transportDocument);
		}catch (EntityNotFoundException e) {
			throw new ObjectNotFoundException("Documento não encontrado");
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
    public void deletePaymentCascadeAll(Integer id) {
		if (transportDocumentRepository.existsById(id)) {
			try {
				transportDocumentRepository.deleteById(id);
			} catch (org.springframework.dao.DataIntegrityViolationException e) {
				throw new com.xflprflx.paycheck.services.exceptions.DataIntegrityViolationException("Falha de integridade referencial");
			} catch (EntityNotFoundException e) {
				throw new ObjectNotFoundException("Documento não encontrado");
			}
		}
    }

	@Transactional
	public List<TransportDocumentDTO> findAllFiltered(
			LocalDate issueStart, LocalDate issueEnd, LocalDate scannedStart, LocalDate scannedEnd, LocalDate forecastScStart, LocalDate forecastScEnd,
			LocalDate forecastApprStart, LocalDate forecastApprEnd, LocalDate approvalStart, LocalDate approvalEnd,
			LocalDate paymentStart, LocalDate paymentEnd, List<Integer> paymentStatuses) {

		Specification<TransportDocument> spec = Specification.where(null);
		if (issueStart != null && issueEnd != null) {
			spec = spec.and((root, query, builder) -> builder.between(root.get("issueDate"), issueStart, issueEnd));
		}
		if (scannedStart != null && scannedEnd != null) {
			spec = spec.and((root, query, builder) -> {
				query.distinct(true);
				return builder.between(root.join("invoices").get("scannedDate"), scannedStart, scannedEnd);
			});
		}
		if (forecastScStart != null && forecastScStart != null) {
			spec = spec.and((root, query, builder) -> builder.between(root.get("paymentForecastByScannedDate"), forecastScStart, forecastScEnd));
		}
		if (forecastApprStart != null && forecastApprEnd != null) {
			spec = spec.and((root, query, builder) -> builder.between(root.get("paymentForecastByPaymentApprovalDate"), forecastApprStart, forecastApprEnd));
		}
		if (approvalStart != null && approvalEnd != null) {
			spec = spec.and((root, query, builder) -> {
				query.distinct(true);
				return builder.between(root.join("invoices").get("paymentApprovalDate"), approvalStart, approvalEnd);
			});
		}
		if (paymentStart != null && paymentEnd != null) {
			spec = spec.and((root, query, builder) -> {
				query.distinct(true);
				return builder.between(root.join("payment").get("paymentDate"), paymentStart, paymentEnd);
			});
		}

		if(paymentStatuses != null && !paymentStatuses.isEmpty()) {
			spec = spec.and((root, query, builder) -> root.get("paymentStatus").in(paymentStatusesStringToPaymentStatusList(paymentStatuses)));
		}

		List<TransportDocument>  transportDocuments = transportDocumentRepository.findAll(spec);
		List<TransportDocumentDTO> transportDocumentDTOS = transportDocuments.stream().map(x -> new TransportDocumentDTO(x, x.getInvoices())).collect(Collectors.toList());
		return transportDocumentDTOS;
	}

	public List<TransportDocumentDTO> returnTransportDocumentListFromFile(MultipartFile file) throws IOException {
		String filename = file.getOriginalFilename();
		FileProcessor fileProcessor = FileProcessorFactory.getFileProcessor(filename);
		return fileProcessor.returnTransportDocumentListFromFile(file);
	}

	private List<PaymentStatus> paymentStatusesStringToPaymentStatusList(List<Integer> paymentStatuses) {
		List<PaymentStatus> paymentStatusList = paymentStatuses.stream().map(x -> PaymentStatus.toEnum(x)).collect(Collectors.toList());
		return paymentStatusList;
	}
}
