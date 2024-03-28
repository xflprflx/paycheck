package com.xflprflx.paycheck.domain.dtos;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xflprflx.paycheck.domain.Invoice;
import com.xflprflx.paycheck.domain.enums.DeliveryStatus;

public class InvoiceDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String number;
	private DeliveryStatus deliveryStatus;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate scannedDate;
	
	public InvoiceDTO() {
	}

	public InvoiceDTO(Invoice invoice) {
		this.id = invoice.getId();
		this.number = invoice.getNumber();
		this.deliveryStatus = invoice.getDeliveryStatus();
		this.scannedDate = invoice.getScannedDate();
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
}
