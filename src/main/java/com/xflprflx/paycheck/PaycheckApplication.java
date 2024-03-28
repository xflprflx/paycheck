package com.xflprflx.paycheck;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.xflprflx.paycheck.domain.Invoice;
import com.xflprflx.paycheck.domain.TransportDocument;
import com.xflprflx.paycheck.domain.enums.PaymentStatus;
import com.xflprflx.paycheck.repositories.InvoiceRepository;
import com.xflprflx.paycheck.repositories.TransportDocumentRepository;

@SpringBootApplication
public class PaycheckApplication implements CommandLineRunner {

	@Autowired
	private TransportDocumentRepository transportDocumentRepository;
	
	@Autowired
	private InvoiceRepository invoiceRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(PaycheckApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Invoice invoice = new Invoice(null, "55", null, LocalDate.now());
		List<Invoice> invoices = invoiceRepository.saveAll(Arrays.asList(invoice));
		
		TransportDocument transportDocument = new TransportDocument(null, "151", "11", 200.0, null, null, PaymentStatus.SCAN_PENDING);
		invoices.forEach(x -> transportDocument.getInvoices().add(x));
//		transportDocument.getInvoices().add(invoice);
		transportDocumentRepository.saveAll(Arrays.asList(transportDocument));
	
	}

}
