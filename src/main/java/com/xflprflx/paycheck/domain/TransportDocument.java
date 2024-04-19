package com.xflprflx.paycheck.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.xflprflx.paycheck.domain.dtos.TransportDocumentDTO;
import com.xflprflx.paycheck.domain.enums.PaymentStatus;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tb_transport_document")
public class TransportDocument implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(unique = false)
	private String number;
	private String serie;
	private Double amount;
	private String addressShipper;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate issueDate;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate paymentForecastByScannedDate;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate paymentForecastByPaymentApprovalDate;
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus = PaymentStatus.PENDING_ON_TIME;
	@Column(columnDefinition = "TEXT")
	private String reasonReduction = "";

	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE})
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name = "tb_transport_document_invoice",
		joinColumns = @JoinColumn(name = "transport_document_id"),
		inverseJoinColumns = @JoinColumn(name = "invoice_id"))
    private Set<Invoice> invoices = new HashSet<>();

	@ManyToOne(cascade = {CascadeType.REMOVE})
	@JoinColumn(name = "payment_id")
	private Payment payment;

	public TransportDocument() {
	}

	public TransportDocument(Integer id, String number, String serie, Double amount, String addressShipper, LocalDate issueDate, LocalDate paymentForecastByScannedDate, LocalDate paymentForecastByPaymentApprovalDate, PaymentStatus paymentStatus, Payment payment) {
		this.id = id;
		this.number = number;
		this.serie = serie;
		this.amount = amount;
		this.addressShipper = addressShipper;
		this.issueDate = issueDate;
		this.paymentForecastByScannedDate = paymentForecastByScannedDate;
		this.paymentForecastByPaymentApprovalDate = paymentForecastByPaymentApprovalDate;
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
		this.paymentForecastByScannedDate = transportDocumentDTO.getPaymentForecastByScannedDate();
		this.paymentForecastByPaymentApprovalDate = transportDocumentDTO.getPaymentForecastByPaymentApprovalDate();
		this.paymentStatus = transportDocumentDTO.getPaymentStatus() != null ? PaymentStatus.toEnum(transportDocumentDTO.getPaymentStatus()) : PaymentStatus.updatePaymentStatus(this);
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

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public String getReasonReduction() {
		return reasonReduction;
	}

	public void setReasonReduction(String reasonReduction) {
		this.reasonReduction = reasonReduction;
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

	public boolean isPaidOnTime() {
		if (this.payment != null) {
			if (this.paymentForecastByScannedDate != null) {
				return this.paymentForecastByScannedDate.isAfter(this.payment.getPaymentDate()) ||
						this.paymentForecastByScannedDate.equals(this.payment.getPaymentDate());
			}
			return true;
		}
		return false;
	}

	public boolean isPaidLate() {
		if (this.payment != null) {
			if (this.paymentForecastByScannedDate != null) {
				return this.paymentForecastByScannedDate.isBefore(this.payment.getPaymentDate());
			}
		}
		return false;
	}


	public boolean isPendingOnTime() {
		if (this.payment == null) {
			if (this.paymentForecastByScannedDate != null) {
				return this.paymentForecastByScannedDate.isAfter(LocalDate.now());
			}
			return false;
		}
		return false;
	}

	public boolean isPendingLate() {
		if (this.payment == null) {
			if (this.paymentForecastByScannedDate != null) {
				return this.paymentForecastByScannedDate.isBefore(LocalDate.now());
			}
			return false;
		}
		return false;
	}

	public boolean isPendingApproval() {
		if(this.isAllScanned(this) && !this.isAllApproved(this)){
			return true;
		}
		return false;
	}

	public boolean isScanPending() {
		if (!this.isAllScanned(this)) {
			return true;
		}
		return false;
	}

	private boolean isAllScanned(TransportDocument transportDocument) {
		if (transportDocument.getInvoices().isEmpty()){
			return false;
		}
		return transportDocument.getInvoices().stream().allMatch(inv -> inv.getScannedDate() != null);
	}

	private boolean isAllApproved(TransportDocument transportDocument) {
		if (transportDocument.getInvoices().isEmpty()){
			return false;
		}
		return transportDocument.getInvoices().stream().allMatch(inv -> inv.getPaymentApprovalDate() != null);
	}


}
