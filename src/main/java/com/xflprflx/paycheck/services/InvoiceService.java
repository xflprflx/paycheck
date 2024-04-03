package com.xflprflx.paycheck.services;

import com.xflprflx.paycheck.domain.Invoice;
import com.xflprflx.paycheck.domain.TransportDocument;
import com.xflprflx.paycheck.domain.dtos.InvoiceDTO;
import com.xflprflx.paycheck.domain.dtos.TransportDocumentDTO;
import com.xflprflx.paycheck.repositories.InvoiceRepository;
import com.xflprflx.paycheck.services.exceptions.DataIntegrityViolationException;
import com.xflprflx.paycheck.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {



	@Autowired
	private InvoiceRepository invoiceRepository;

	public Optional<Invoice> findByNumber(String number) {
		return invoiceRepository.findByNumber(number);
	}

	@Transactional
    public void saveInvoices(List<InvoiceDTO> invoiceDTOS) {
		List<Invoice> invoices = new ArrayList<>();
		for (InvoiceDTO invoiceDTO : invoiceDTOS) {
			Optional<Invoice> optionalInvoice = findByNumber(invoiceDTO.getNumber());
			if(optionalInvoice.isPresent()) {
				var oldInvoice = optionalInvoice.get();
				var invoice = new Invoice(invoiceDTO);
				invoice.setId(oldInvoice.getId());
				invoices.add(invoice);
				invoiceDTO.setId(invoice.getId());
			}
			invoiceRepository.saveAll(invoices);
		}
    }
}
