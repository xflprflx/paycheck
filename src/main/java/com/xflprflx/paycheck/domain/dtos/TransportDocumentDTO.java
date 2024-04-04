package com.xflprflx.paycheck.domain.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.xflprflx.paycheck.domain.Invoice;
import com.xflprflx.paycheck.domain.Payment;
import com.xflprflx.paycheck.domain.TransportDocument;
import com.xflprflx.paycheck.domain.enums.PaymentStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
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
	private String addressShipper;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate issueDate;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate paymentForecastByScannedDate;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate paymentForecastByPaymentApprovalDate;
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;
	private PaymentDTO paymentDTO;

	private Set<InvoiceDTO> invoices = new HashSet<>();

	public TransportDocumentDTO() {
	}

	public TransportDocumentDTO(TransportDocument transportDocument) {
		this.id = transportDocument.getId();
		this.number = transportDocument.getNumber();
		this.serie = transportDocument.getSerie();
		this.amount = transportDocument.getAmount();
		this.addressShipper = transportDocument.getAddressShipper();
		this.issueDate = transportDocument.getIssueDate();
		this.paymentForecastByScannedDate = transportDocument.getPaymentForecastByScannedDate();
		this.paymentForecastByPaymentApprovalDate = transportDocument.getPaymentForecastByPaymentApprovalDate();
		this.paymentStatus = transportDocument.getPaymentStatus();
		this.paymentDTO = new PaymentDTO(transportDocument.getPayment());
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

	public void setIssueDate(String issueDateStr) {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
		this.issueDate = LocalDate.parse(issueDateStr, formatter);
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

	public String getAddressShipper() {
		return addressShipper;
	}

	public void setAddressShipper(String addressShipper) {
		this.addressShipper = addressShipper;
	}

	public LocalDate getPaymentForecastByScannedDate() {
		return paymentForecastByScannedDate;
	}

	public void setPaymentForecastByScannedDate(LocalDate paymentForecastByScannedDate) {
		this.paymentForecastByScannedDate = paymentForecastByScannedDate;
	}

	public LocalDate getPaymentForecastByPaymentApprovalDate() {
		return paymentForecastByPaymentApprovalDate;
	}

	public void setPaymentForecastByPaymentApprovalDate(LocalDate paymentForecastByPaymentApprovalDate) {
		this.paymentForecastByPaymentApprovalDate = paymentForecastByPaymentApprovalDate;
	}

	public PaymentDTO getPaymentDTO() {
		return paymentDTO;
	}

	public void setPaymentDTO(PaymentDTO paymentDTO) {
		this.paymentDTO = paymentDTO;
	}


}
