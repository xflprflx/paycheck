package com.xflprflx.paycheck.domain.dtos;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.xflprflx.paycheck.domain.Invoice;
import com.xflprflx.paycheck.domain.enums.DeliveryStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String number;
	private String deliveryStatus;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate scannedDate;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate paymentApprovalDate;
	
	public InvoiceDTO() {
	}

	public InvoiceDTO(Invoice invoice) {
		this.id = invoice.getId();
		this.number = invoice.getNumber();
		this.deliveryStatus = invoice.getDeliveryStatus().getDescription();
		this.scannedDate = invoice.getScannedDate();
		this.paymentApprovalDate = invoice.getPaymentApprovalDate();
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

	public String getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
		this.deliveryStatus = deliveryStatus.getDescription();
	}

	public LocalDate getScannedDate() {
		return scannedDate;
	}

	public void setScannedDate(LocalDate scannedDate) {
		this.scannedDate = scannedDate;
	}

	public void setScannedDate(String scannedDateStr) {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
		this.scannedDate = LocalDate.parse(scannedDateStr, formatter);
	}

	public LocalDate getPaymentApprovalDate() {
		return paymentApprovalDate;
	}

	public void setPaymentApprovalDate(LocalDate paymentApprovalDate) {
		this.paymentApprovalDate = paymentApprovalDate;
	}

	public void setPaymentApprovalDate(String paymentApprovalDateStr) {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
		this.paymentApprovalDate = LocalDate.parse(paymentApprovalDateStr, formatter);
	}

}
