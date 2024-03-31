package com.xflprflx.paycheck.domain.dtos;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.xflprflx.paycheck.domain.Invoice;
import com.xflprflx.paycheck.domain.TransportDocument;
import com.xflprflx.paycheck.domain.enums.PaymentStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

public class TransportDocumentDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	@NotNull(message = "O campo número é requerido")
	private String number;
	@NotNull(message = "O campo série é requerido")
	private String serie;
	@NotNull(message = "O campo valor do frete é requerido")
	private Double amount;
	@NotNull(message = "O campo CNPJ é requerido")
	private String cnpjShipper;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate issueDate;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate paymentForecast;
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
		this.cnpjShipper = transportDocument.getCnpjShipper();
		this.issueDate = transportDocument.getIssueDate();
		this.paymentForecast = transportDocument.getPaymentForecast();
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

	public String getCnpjShipper() {
		return cnpjShipper;
	}

	public void setCnpjShipper(String cnpjShipper) {
		this.cnpjShipper = cnpjShipper;
	}

	public LocalDate getPaymentForecast() {
		return paymentForecast;
	}

	public void setPaymentForecast(LocalDate paymentForecast) {
		this.paymentForecast = paymentForecast;
	}
}
