package com.simon.apiconnect.controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.hateoas.VndErrors;

import com.simon.apiconnect.exceptions.ProfileAlreadyExistsException;
import com.simon.apiconnect.exceptions.ProfileNotFoundException;

@ControllerAdvice
@RequestMapping(produces = "application/vnd.error")
public class ProfileControllerAdvice {

	@ResponseBody
    @ExceptionHandler(ProfileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    VndErrors bookNotFoundExceptionHandler(ProfileNotFoundException ex) {
        return new VndErrors("error", ex.getMessage());
    }
	
	 @ResponseBody
	    @ExceptionHandler(ProfileAlreadyExistsException.class)
	    @ResponseStatus(HttpStatus.CONFLICT)
	    VndErrors bookIsbnAlreadyExistsExceptionHandler(ProfileAlreadyExistsException ex) {
	        return new VndErrors("error", ex.getMessage());
	    }
	
}
