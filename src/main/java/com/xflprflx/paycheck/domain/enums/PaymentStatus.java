package com.xflprflx.paycheck.domain.enums;

import com.xflprflx.paycheck.domain.TransportDocument;

public enum PaymentStatus {
		PAID_ON_TIME(0, "Pago no prazo"),
		PAID_LATE(1, "Pago em atraso"),
		PENDING_ON_TIME(2, "Pendente no prazo"),
		PENDING_LATE(3, "Pendente em atraso"),
		PENDING_APPROVAL(4, "Aprovação pendente"),
		SCAN_PENDING(5, "Digitalização pendente"),
		DEBATE_PAYMENT(6, "Pagamento abatido");
	
	private Integer code;
	private String 	description;
	
	PaymentStatus(Integer code, String description) {
		this.code = code;
		this.description = description;
	}


	public Integer getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
	
	public static PaymentStatus toEnum(String description) {
		if(description == null) {
			return null;
		}
		for(PaymentStatus x : PaymentStatus.values()) {
			if(description.equals(x.getDescription())) {
				return x;
			}
		}
		throw new IllegalArgumentException("Status de pagamento inválido");
	}

	public static PaymentStatus toEnum(Integer code) {
		if(code == null) {
			return null;
		}
		for(PaymentStatus x : PaymentStatus.values()) {
			if(code.equals(x.getCode())) {
				return x;
			}
		}
		throw new IllegalArgumentException("Status de pagamento inválido");
	}

	public static PaymentStatus updatePaymentStatus(TransportDocument transportDocument){
		if (transportDocument.isPaidLate())
			return PAID_LATE;
		if (transportDocument.isPaidOnTime())
			return PAID_ON_TIME;
		if (transportDocument.isScanPending())
			return SCAN_PENDING;
		if (transportDocument.isPendingApproval())
			return PENDING_APPROVAL;
		if (transportDocument.isPendingApproval())
			return PENDING_APPROVAL;
		if(transportDocument.isPendingOnTime())
			return PENDING_ON_TIME;
		if (transportDocument.isPendingLate())
			return PENDING_LATE;
		return null;
	}
}
