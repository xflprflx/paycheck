package com.xflprflx.paycheck.domain.enums;

public enum PaymentStatus {
	PAID_ON_TIME(0, "Pago no prazo"),
	PAID_LATE(1, "Pago em atraso"),
	PENDING_ON_TIME(2, "Pendente no prazo"),
	PENDING_LATE(3, "Pendente em atraso"),
	SCAN_PENDING(4, "Digitalização pendente");
	
	private Integer code;
	private String 	description;
	
	PaymentStatus(Integer code, String description) {
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
}
