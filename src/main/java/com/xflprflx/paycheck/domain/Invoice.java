package com.xflprflx.paycheck.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xflprflx.paycheck.domain.dtos.InvoiceDTO;
import com.xflprflx.paycheck.domain.enums.DeliveryStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tb_invoice")
public class Invoice implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String number;
	@Enumerated(EnumType.STRING)
	private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate scannedDate;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate paymentApprovalDate;

	@ManyToMany(mappedBy = "invoices",fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
	private Set<TransportDocument> transportDocuments = new HashSet<>();
	
	public Invoice() {
	}

	public Invoice(Integer id, String number, DeliveryStatus deliveryStatus, LocalDate scannedDate, LocalDate paymentApprovalDate) {
		this.id = id;
		this.number = number;
		this.deliveryStatus = deliveryStatus;
		this.scannedDate = scannedDate;
		this.paymentApprovalDate = paymentApprovalDate;
	}
	
	public Invoice(InvoiceDTO invoiceDTO) {
		this.id = invoiceDTO.getId();
		this.number = invoiceDTO.getNumber();
		this.deliveryStatus = invoiceDTO.getScannedDate() != null ? DeliveryStatus.DELIVERED : DeliveryStatus.PENDING;
		this.scannedDate = invoiceDTO.getScannedDate();
		this.paymentApprovalDate = invoiceDTO.getPaymentApprovalDate();
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

	public LocalDate getPaymentApprovalDate() {
		return paymentApprovalDate;
	}

	public void setPaymentApprovalDate(LocalDate paymentApprovalDate) {
		this.paymentApprovalDate = paymentApprovalDate;
	}
	
	public Set<TransportDocument> getTransportDocuments() {
		return transportDocuments;
	}

	public void setTransportDocuments(Set<TransportDocument> transportDocuments) {
		this.transportDocuments = transportDocuments;
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

/*	@PreRemove
	private void removeTransportDocumentsFromInvoice() {
		for (TransportDocument transportDocument : transportDocuments) {
			transportDocument.getInvoices().remove(this);
		}
		transportDocuments.clear();
	}*/
}
