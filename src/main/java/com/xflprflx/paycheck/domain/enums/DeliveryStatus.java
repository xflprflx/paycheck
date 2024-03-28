package com.xflprflx.paycheck.domain.enums;

public enum DeliveryStatus {
	DELIVERED(0, "Entregue"),
	PENDING(1, "Pendente");
	
	private Integer code;
	private String 	description;
	
	DeliveryStatus(Integer code, String description) {
	}

	public Integer getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
	
	public static DeliveryStatus toEnum(String description) {
		if(description == null) {
			return null;
		}
		for(DeliveryStatus x : DeliveryStatus.values()) {
			if(description.equals(x.getDescription())) {
				return x;
			}
		}
		throw new IllegalArgumentException("Status de entrega inválido");
	}
}
