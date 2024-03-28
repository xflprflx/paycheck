package com.xflprflx.paycheck.domain.dtos;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xflprflx.paycheck.domain.Invoice;
import com.xflprflx.paycheck.domain.TransportDocument;
import com.xflprflx.paycheck.domain.enums.PaymentStatus;

public class TransportDocumentDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String number;
	private String serie;
	private Double amount;
	private LocalDate issueDate;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate paymentDate;
	private PaymentStatus paymentStatus;

	private Set<InvoiceDTO> invoices = new HashSet<>();

	public TransportDocumentDTO() {
	}

	public TransportDocumentDTO(TransportDocument transportDocument) {
		this.id = transportDocument.getId();
		this.number = transportDocument.getNumber();
		this.serie = transportDocument.getSerie();
		this.amount = transportDocument.getAmount();
		this.issueDate = transportDocument.getIssueDate();
		this.paymentDate = transportDocument.getPaymentDate();
		this.paymentStatus = transportDocument.getPaymentStatus();
	}
	
	public TransportDocumentDTO(TransportDocument transportDocument, Set<Invoice> invoices) {
		this(transportDocument);
		invoices.forEach(invoice -> this.invoices.add(new InvoiceDTO(invoice)));
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public LocalDate getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(LocalDate issueDate) {
		this.issueDate = issueDate;
	}

	public LocalDate getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDate paymentDate) {
		this.paymentDate = paymentDate;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public Set<InvoiceDTO> getInvoices() {
		return invoices;
	}
}
