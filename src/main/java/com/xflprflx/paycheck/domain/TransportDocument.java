package com.xflprflx.paycheck.domain;

import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.xflprflx.paycheck.domain.enums.PaymentStatus;



public class TransportDocument {

	private Integer id;
	private String number;
	private String serie;
	private Double amount;
	private LocalDate issueDate;
	private LocalDate paymentDate;
	private PaymentStatus paymentStatus;
	private LocalDate createdAt;
	private LocalDate updatedAt;
	
	
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
