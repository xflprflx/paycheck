package com.xflprflx.paycheck.services;

import com.xflprflx.paycheck.domain.Invoice;
import com.xflprflx.paycheck.domain.Payment;
import com.xflprflx.paycheck.domain.TransportDocument;
import com.xflprflx.paycheck.domain.dtos.InvoiceDTO;
import com.xflprflx.paycheck.domain.dtos.PaymentDTO;
import com.xflprflx.paycheck.domain.dtos.TransportDocumentDTO;
import com.xflprflx.paycheck.domain.enums.PaymentStatus;
import com.xflprflx.paycheck.repositories.InvoiceRepository;
import com.xflprflx.paycheck.repositories.PaymentRepository;
import com.xflprflx.paycheck.repositories.TransportDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private TransportDocumentService transportDocumentService;

	@Transactional
	public List<Payment> savePayments(List<Payment> payments) {
		List<Payment> paymentDTOS = new ArrayList<>();
		for (Payment payment : payments) {
			Optional<Payment> optionalPayment = paymentRepository.findByNumber(payment.getNumber());
			if (optionalPayment.isPresent()) {
				payment.setId(optionalPayment.get().getId());
			}
			payment = paymentRepository.save(payment);
			transportDocumentService.updateByPayment(payment);
			paymentDTOS.add(payment);
		}
		return paymentDTOS;
	}

    public Optional<Payment> findPaymentByNumber(String number) {
		return paymentRepository.findByNumber(number);
    }

	@Transactional
    public List<PaymentDTO> findAll() {
		List<Payment> payments = paymentRepository.findAll();
    	return payments.stream().map(x -> new PaymentDTO(x)).collect(Collectors.toList());
	}

	@Transactional
	public List<PaymentDTO> findAllFiltered(
			LocalDate issueStart, LocalDate issueEnd, LocalDate scannedStart, LocalDate scannedEnd, LocalDate forecastScStart, LocalDate forecastScEnd,
			LocalDate forecastApprStart, LocalDate forecastApprEnd, LocalDate approvalStart, LocalDate approvalEnd,
			LocalDate paymentStart, LocalDate paymentEnd, Integer paymentStatus) {

		Specification<Payment> spec = Specification.where(null);
		if (issueStart != null && issueEnd != null) {
			spec = spec.and((root, query, builder) -> builder.between(root.join("transportDocument").get("issueDate"), issueStart, issueEnd));
		}
		if (scannedStart != null && scannedEnd != null) {
			spec = spec.and((root, query, builder) -> {
				query.distinct(true);
				return builder.between(root.join("transportDocument").joinCollection("invoices").get("scannedDate"), scannedStart, scannedEnd);
			});
		}
		if (forecastScStart != null && forecastScStart != null) {
			spec = spec.and((root, query, builder) -> builder.between(root.join("transportDocument").get("paymentForecastByScannedDate"), forecastScStart, forecastScEnd));
		}
		if (forecastApprStart != null && forecastApprEnd != null) {
			spec = spec.and((root, query, builder) -> builder.between(root.join("transportDocument").get("paymentForecastByPaymentApprovalDate"), forecastApprStart, forecastApprEnd));
		}
		if (approvalStart != null && approvalEnd != null) {
			spec = spec.and((root, query, builder) -> {
				query.distinct(true);
				return builder.between(root.join("transportDocument").joinCollection("invoices").get("paymentApprovalDate"), approvalStart, approvalEnd);
			});
		}
		if (paymentStart != null && paymentEnd != null) {
			spec = spec.and((root, query, builder) -> {
				query.distinct(true);
				return builder.between(root.get("paymentDate"), paymentStart, paymentEnd);
			});
		}
		if(paymentStatus != null) {
			spec = spec.and((root, query, builder) -> builder.equal(root.join("transportDocument").get("paymentStatus"), PaymentStatus.toEnum(paymentStatus)));
		}

		List<Payment>  payments = paymentRepository.findAll(spec);
		List<PaymentDTO> paymentDTOS = payments.stream().map(x -> new PaymentDTO(x)).collect(Collectors.toList());
		return paymentDTOS;
	}

	@Transactional(readOnly = true)
	public List<Payment> findPaymentsWithoutDoc() {
		List<Payment> docsNull = paymentRepository.findWhereDocIsNUll();
		return docsNull;
	}
}
