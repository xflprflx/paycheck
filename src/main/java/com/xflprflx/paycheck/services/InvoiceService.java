package com.xflprflx.paycheck.services;

import com.xflprflx.paycheck.domain.Invoice;
import com.xflprflx.paycheck.domain.TransportDocument;
import com.xflprflx.paycheck.domain.dtos.InvoiceDTO;
import com.xflprflx.paycheck.domain.enums.DeliveryStatus;
import com.xflprflx.paycheck.repositories.InvoiceRepository;
import com.xflprflx.paycheck.repositories.TransportDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private PaymentForecastCalculatorService paymentForecastCalculatorService;

	@Autowired
	private TransportDocumentRepository transportDocumentRepository;

	@Transactional
	public Optional<Invoice> findByNumber(String number) {
		return invoiceRepository.findByNumber(number);
	}

	@Transactional
	public Invoice save(Invoice invoice) {
		return invoiceRepository.save(invoice);
	}

	@Transactional
	public void saveInvoices(List<InvoiceDTO> invoiceDTOS) {
		List<Invoice> invoices = new ArrayList<>();
		for (InvoiceDTO invoiceDTO : invoiceDTOS) {
			findByNumber(invoiceDTO.getNumber()).ifPresent(oldInvoice -> {
				Invoice invoice = new Invoice(invoiceDTO);
				invoice.setId(oldInvoice.getId());
				oldInvoice.getTransportDocuments().forEach(invoice.getTransportDocuments()::add);
				if (invoiceDTO.getScannedDate() != null) {
					invoice.setDeliveryStatus(DeliveryStatus.DELIVERED);
				}
				invoices.add(invoice);
			});
		}
		invoiceRepository.saveAll(invoices);
		updatePaymentForecastIfNeeded(invoices);
	}

	private void updatePaymentForecastIfNeeded(List<Invoice> invoices) {
		List<TransportDocument> transportDocumentsToUpdate = new ArrayList<>();
		for (Invoice invoice : invoices) {
			invoice.getTransportDocuments().forEach(transportDocumentsToUpdate::add);
		}
		for (TransportDocument transportDocument : transportDocumentsToUpdate) {
			boolean allScanned = transportDocument.getInvoices().stream().allMatch(inv -> inv.getScannedDate() != null);
			boolean allApproved = transportDocument.getInvoices().stream().allMatch(inv -> inv.getPaymentApprovalDate() != null);

			if (allScanned || allApproved) {
				LocalDate newPaymentForecastByScannedDate = allScanned ? paymentForecastCalculatorService.calculateNewPaymentForecastByScannedDate(transportDocument) : transportDocument.getPaymentForecastByScannedDate();
				LocalDate newPaymentForecastByApprovalDate = allApproved ? paymentForecastCalculatorService.calculateNewPaymentForecastByPaymentApprovalDate(transportDocument) : transportDocument.getPaymentForecastByPaymentApprovalDate();

				transportDocument.setPaymentForecastByScannedDate(newPaymentForecastByScannedDate);
				transportDocument.setPaymentForecastByPaymentApprovalDate(newPaymentForecastByApprovalDate);

				transportDocumentRepository.save(transportDocument);
			}
		}
	}
}
