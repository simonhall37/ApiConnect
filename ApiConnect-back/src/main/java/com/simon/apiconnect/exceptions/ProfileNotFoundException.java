package com.simon.apiconnect.exceptions;

import org.slf4j.Logger;

public class ProfileNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -2794835674427538160L;

	public ProfileNotFoundException(String message) {
		super(message);
	}
	
	public ProfileNotFoundException(String type, String field, String value) {
		super("Couldn't find "+ type + " with " + field + " equal to " + value);
	}
	
	public ProfileNotFoundException(Logger log,String type, String field, String value) {
		super("Couldn't find "+ type + " with " + field + " equal to " + value);
		log.warn("Couldn't find "+ type + " with " + field + " equal to " + value);
	}
}
