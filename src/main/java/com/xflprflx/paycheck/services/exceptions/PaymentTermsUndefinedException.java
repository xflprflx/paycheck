package com.xflprflx.paycheck.services.exceptions;

public class PaymentTermsUndefinedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PaymentTermsUndefinedException(String message, Throwable cause) {
		super(message, cause);
	}

	public PaymentTermsUndefinedException(String message) {
		super(message);
	}

	
}
