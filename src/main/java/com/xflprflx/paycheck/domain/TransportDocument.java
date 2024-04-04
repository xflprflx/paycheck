package com.xflprflx.paycheck.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.xflprflx.paycheck.domain.dtos.TransportDocumentDTO;
import com.xflprflx.paycheck.domain.enums.PaymentStatus;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tb_transport_document")
public class TransportDocument implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(unique = true)
	private String number;
	private String serie;
	private Double amount;
	private String addressShipper;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate issueDate;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate paymentForecast;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate paymentDate;
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;
	
    @ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "tb_transport_document_invoice",
		joinColumns = @JoinColumn(name = "transport_document_id"),
		inverseJoinColumns = @JoinColumn(name = "invoice_id"))
    private Set<Invoice> invoices = new HashSet<>();

	@ManyToOne
	@JoinColumn(name = "payment_id")
	private Payment payment;

	public TransportDocument() {
	}

	public TransportDocument(Integer id, String number, String serie, Double amount, String addressShipper, LocalDate issueDate, LocalDate paymentForecast, LocalDate paymentDate, PaymentStatus paymentStatus, Payment payment) {
		this.id = id;
		this.number = number;
		this.serie = serie;
		this.amount = amount;
		this.addressShipper = addressShipper;
		this.issueDate = issueDate;
		this.paymentForecast = paymentForecast;
		this.paymentDate = paymentDate;
		this.paymentStatus = paymentStatus;
		this.payment = payment;
	}

	public TransportDocument(TransportDocumentDTO transportDocumentDTO) {
		this.id = transportDocumentDTO.getId();
		this.number = transportDocumentDTO.getNumber();
		this.serie = transportDocumentDTO.getSerie();
		this.amount = transportDocumentDTO.getAmount();
		this.addressShipper = transportDocumentDTO.getAddressShipper();
		this.issueDate = transportDocumentDTO.getIssueDate();
		this.paymentForecast = transportDocumentDTO.getPaymentForecast();
		this.paymentDate = transportDocumentDTO.getPaymentDate();
		this.paymentStatus = transportDocumentDTO.getPaymentStatus();
		transportDocumentDTO.getInvoices().forEach(invoiceDto -> this.invoices.add(new Invoice(invoiceDto)));
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
	
	public Set<Invoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(Set<Invoice> invoices) {
		this.invoices = invoices;
	}

	public String getAddressShipper() {
		return addressShipper;
	}

	public void setAddressShipper(String addressShipper) {
		this.addressShipper = addressShipper;
	}

	public LocalDate getPaymentForecast() {
		return paymentForecast;
	}

	public void setPaymentForecast(LocalDate paymentForecast) {
		this.paymentForecast = paymentForecast;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransportDocument other = (TransportDocument) obj;
		return Objects.equals(id, other.id);
	}
}
