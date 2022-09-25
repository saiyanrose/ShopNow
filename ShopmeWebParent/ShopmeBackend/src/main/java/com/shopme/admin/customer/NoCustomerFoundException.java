package com.shopme.admin.customer;

public class NoCustomerFoundException extends RuntimeException {
	public NoCustomerFoundException(String message) {
		super(message);
	}
}
