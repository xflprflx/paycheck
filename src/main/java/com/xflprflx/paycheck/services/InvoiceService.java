package com.xflprflx.paycheck.services;

import com.xflprflx.paycheck.domain.Invoice;
import com.xflprflx.paycheck.domain.TransportDocument;
import com.xflprflx.paycheck.domain.dtos.InvoiceDTO;
import com.xflprflx.paycheck.domain.enums.DeliveryStatus;
import com.xflprflx.paycheck.domain.enums.PaymentStatus;
import com.xflprflx.paycheck.factory.FileProcessorFactory;
import com.xflprflx.paycheck.repositories.InvoiceRepository;
import com.xflprflx.paycheck.repositories.TransportDocumentRepository;
import com.xflprflx.paycheck.services.exceptions.PaymentTermsUndefinedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
	public Invoice save(Invoice invoice) {
		return invoiceRepository.save(invoice);
	}

	@Transactional
	public List<Invoice> findAll() {
		return invoiceRepository.findAll();
	}

	@Transactional
	public void saveInvoices(List<InvoiceDTO> invoiceDTOS) {
		List<Invoice> allInvoices = invoiceRepository.findAll();
		List<Invoice> invoices = new ArrayList<>();
		for (InvoiceDTO invoiceDTO : invoiceDTOS) {
			Optional<Invoice> invoiceOptional = allInvoices.stream().filter(invoice -> invoice.getNumber().equals(invoiceDTO.getNumber())).findFirst();
			invoiceOptional.ifPresent(existing -> {
				Invoice invoice = new Invoice(invoiceDTO);
				invoice.setId(existing.getId());
				existing.getTransportDocuments().forEach(invoice.getTransportDocuments()::add);
				if (invoiceDTO.getScannedDate() != null) {
					invoice.setDeliveryStatus(DeliveryStatus.DELIVERED);
				}
				invoices.add(invoice);
			});
		}
		invoiceRepository.saveAll(invoices);
		try {
			updatePaymentForecastIfNeeded(invoices);
		} catch (NullPointerException e) {
			throw new PaymentTermsUndefinedException("Condição de pagamento não definida. \nCadastre a condição de pagamento no menu CONFIGURAÇÕES");
		}
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

			}
			if (transportDocument.getPaymentStatus() != null){
				if (!transportDocument.getPaymentStatus().equals(PaymentStatus.DEBATE_PAYMENT)) {
					transportDocument.setPaymentStatus(PaymentStatus.updatePaymentStatus(transportDocument));
				}
			} else {
				transportDocument.setPaymentStatus(PaymentStatus.updatePaymentStatus(transportDocument));
			}
		}
		transportDocumentRepository.saveAll(transportDocumentsToUpdate);
	}

	public List<InvoiceDTO> returnInvoiceListFromFile(MultipartFile file) throws IOException {
		String filename = file.getOriginalFilename();
		FileProcessor fileProcessor = FileProcessorFactory.getFileProcessor(filename);
		return fileProcessor.returnInvoiceListFromFile(file);
	}
}
