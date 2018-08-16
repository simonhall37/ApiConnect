package com.simon.apiconnect.exceptions;

import org.slf4j.Logger;

public class ProfileAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = -8836122811902091038L;
	
	public ProfileAlreadyExistsException(Logger log,String name) {
		super("A profile with name " + name + " already exists");
		log.warn("A profile with name " + name + " already exists");
	}
	
}
