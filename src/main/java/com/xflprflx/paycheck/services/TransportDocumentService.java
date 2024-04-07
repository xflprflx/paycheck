package com.xflprflx.paycheck.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.xflprflx.paycheck.domain.Invoice;
import com.xflprflx.paycheck.domain.Payment;
import com.xflprflx.paycheck.domain.dtos.InvoiceDTO;
import com.xflprflx.paycheck.domain.dtos.PaymentDTO;
import com.xflprflx.paycheck.domain.enums.PaymentStatus;
import com.xflprflx.paycheck.repositories.InvoiceRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.xflprflx.paycheck.domain.TransportDocument;
import com.xflprflx.paycheck.domain.dtos.TransportDocumentDTO;
import com.xflprflx.paycheck.repositories.TransportDocumentRepository;
import com.xflprflx.paycheck.services.exceptions.ObjectNotFoundException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityNotFoundException;

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
	public Optional<TransportDocument> findTransportDocumentByNumber(String numberTransportDocument) {
		return transportDocumentRepository.findByNumber(numberTransportDocument);
	}

	@Transactional(readOnly = true)
	public List<TransportDocumentDTO> findAllTransportDocuments() {
		List<TransportDocument> transportDocuments = this.transportDocumentRepository.findAll();
		return transportDocuments.stream().map(x -> new TransportDocumentDTO(x, x.getInvoices())).collect(Collectors.toList());
	}

	@Transactional
	public void saveCteWithInvoice(List<TransportDocumentDTO> transportDocumentDTOS) {
		List<TransportDocument> transportDocuments = new ArrayList<>();
		for (TransportDocumentDTO transportDocumentDTO : transportDocumentDTOS) {
			TransportDocument transportDocument = createOrUpdateTransportDocument(transportDocumentDTO);
			processInvoices(transportDocumentDTO, transportDocument);
			transportDocument = updateTransportDocumentPaymentForecast(transportDocument);
			associatePayment(transportDocumentDTO, transportDocument);
			if (transportDocument.getPaymentStatus() != null){
				if (!transportDocument.getPaymentStatus().equals(PaymentStatus.DEBATE_PAYMENT)) {
					transportDocument.setPaymentStatus(PaymentStatus.updatePaymentStatus(transportDocument));
				}
			} else {
				transportDocument.setPaymentStatus(PaymentStatus.updatePaymentStatus(transportDocument));
			}
			transportDocuments.add(transportDocument);
		}
		transportDocumentRepository.saveAll(transportDocuments);
	}

	private TransportDocument createOrUpdateTransportDocument(TransportDocumentDTO transportDocumentDTO) {
		TransportDocument transportDocument = new TransportDocument(transportDocumentDTO);
		findTransportDocumentByNumber(transportDocumentDTO.getNumber())
				.ifPresent(existingTransportDocument -> {
					transportDocument.setId(existingTransportDocument.getId());
					transportDocument.setPaymentStatus(existingTransportDocument.getPaymentStatus());
					transportDocument.setReasonReduction(existingTransportDocument.getReasonReduction());
				});
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
			transportDocument.setPaymentStatus(PaymentStatus.updatePaymentStatus(transportDocument));
			transportDocumentRepository.save(transportDocument);
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
			LocalDate paymentStart, LocalDate paymentEnd, Integer paymentStatus) {

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
		if(paymentStatus != null) {
			spec = spec.and((root, query, builder) -> builder.equal(root.get("paymentStatus"), PaymentStatus.toEnum(paymentStatus)));
		}

		List<TransportDocument>  transportDocuments = transportDocumentRepository.findAll(spec);
		List<TransportDocumentDTO> transportDocumentDTOS = transportDocuments.stream().map(x -> new TransportDocumentDTO(x, x.getInvoices())).collect(Collectors.toList());
		return transportDocumentDTOS;
	}
}
