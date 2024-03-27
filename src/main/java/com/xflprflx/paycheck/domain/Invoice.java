package com.xflprflx.paycheck.domain;

import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.xflprflx.paycheck.domain.enums.DeliveryStatus;

public class Invoice {

	private Integer id;
	private String number;
	private DeliveryStatus deliveryStatus;
	private LocalDate scannedDate;
	private LocalDate createdAt;
	private LocalDate updatedAt;
	
	public Invoice() {
	}

	public Invoice(
			Integer id, String number, DeliveryStatus deliveryStatus, LocalDate scannedDate, LocalDate createdAt,
			LocalDate updatedAt) {
		this.id = id;
		this.number = number;
		this.deliveryStatus = deliveryStatus;
		this.scannedDate = scannedDate;
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

	public DeliveryStatus getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	public LocalDate getScannedDate() {
		return scannedDate;
	}

	public void setScannedDate(LocalDate scannedDate) {
		this.scannedDate = scannedDate;
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
		Invoice other = (Invoice) obj;
		return Objects.equals(id, other.id);
	}
}
