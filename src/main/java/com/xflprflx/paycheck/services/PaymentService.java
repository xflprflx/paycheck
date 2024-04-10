package com.xflprflx.paycheck.services;

import com.xflprflx.paycheck.domain.Invoice;
import com.xflprflx.paycheck.domain.Payment;
import com.xflprflx.paycheck.domain.TransportDocument;
import com.xflprflx.paycheck.domain.dtos.InvoiceDTO;
import com.xflprflx.paycheck.domain.dtos.PaymentDTO;
import com.xflprflx.paycheck.domain.dtos.TransportDocumentDTO;
import com.xflprflx.paycheck.repositories.InvoiceRepository;
import com.xflprflx.paycheck.repositories.PaymentRepository;
import com.xflprflx.paycheck.repositories.TransportDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	public List<PaymentDTO> savePayments(List<Payment> payments) {
		List<PaymentDTO> paymentDTOS = new ArrayList<>();
		for (Payment payment : payments) {
			Optional<Payment> optionalPayment = paymentRepository.findByNumber(payment.getNumber());
			if (optionalPayment.isPresent()) {
				payment.setId(optionalPayment.get().getId());
			}
			payment = paymentRepository.save(payment);
			transportDocumentService.updateByPayment(payment);
			paymentDTOS.add(new PaymentDTO(payment));
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
}
