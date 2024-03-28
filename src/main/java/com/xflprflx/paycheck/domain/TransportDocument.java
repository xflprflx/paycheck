package com.xflprflx.paycheck.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate issueDate;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate paymentDate;
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate createdAt;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate updatedAt;
	
    @ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "tb_transport_document_invoice",
		joinColumns = @JoinColumn(name = "transport_document_id"),
		inverseJoinColumns = @JoinColumn(name = "invoice_id"))
    private Set<Invoice> invoices = new HashSet<>();
	
	public TransportDocument() {
	}

	public TransportDocument(
			Integer id, String number, String serie, Double amount, LocalDate issueDate,
			LocalDate paymentDate, PaymentStatus paymentStatus) {
		this.id = id;
		this.number = number;
		this.serie = serie;
		this.amount = amount;
		this.issueDate = issueDate;
		this.paymentDate = paymentDate;
		this.paymentStatus = paymentStatus;
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

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDate getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDate updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public Set<Invoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(Set<Invoice> invoices) {
		this.invoices = invoices;
	}

	@PrePersist
    public void prePersist() {
        createdAt = LocalDate.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDate.now();
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
