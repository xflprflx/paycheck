package com.xflprflx.paycheck.services;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xflprflx.paycheck.domain.Invoice;
import com.xflprflx.paycheck.domain.TransportDocument;
import com.xflprflx.paycheck.domain.enums.DeliveryStatus;
import com.xflprflx.paycheck.domain.enums.PaymentStatus;
import com.xflprflx.paycheck.repositories.InvoiceRepository;
import com.xflprflx.paycheck.repositories.TransportDocumentRepository;

@Service
public class DBService {


	@Autowired
	private TransportDocumentRepository transportDocumentRepository;
	
	@Autowired
	private InvoiceRepository invoiceRepository;
	
	public void instanceDB() {
		Invoice invoice1 = new Invoice(null, "55", DeliveryStatus.DELIVERED, LocalDate.now());
		Invoice invoice2 = new Invoice(null, "56", DeliveryStatus.DELIVERED, LocalDate.now());
		List<Invoice> invoices = invoiceRepository.saveAll(Arrays.asList(invoice1, invoice2));
		
		TransportDocument transportDocument = new TransportDocument(null, "151", "11", 200.0, "10280765000690", LocalDate.now(), LocalDate.now(), LocalDate.now(), PaymentStatus.SCAN_PENDING);
		invoices.forEach(x -> transportDocument.getInvoices().add(x));
//		transportDocument.getInvoices().add(invoice);
		transportDocumentRepository.saveAll(Arrays.asList(transportDocument));
	}
}
