package com._3po_labs.alexa_model_tester.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com._3po_labs.alexa_model_tester.exception.NewtoolException;
import com._3po_labs.alexa_model_tester.exception.NewtoolExceptionInformation;

@ControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(NewtoolException.class)
    public ResponseEntity<NewtoolExceptionInformation> rulesForCustomerNotFound(HttpServletRequest req, NewtoolException e) 
    {
    	HttpStatus status = null;
    	switch(e.getExceptionCode()){
    	case GENERAL:
    	case INVALID:
    		status = HttpStatus.BAD_REQUEST;
    		break;
    	case UNAUTHORIZED:
    		status = HttpStatus.UNAUTHORIZED;
    		break;
    	case ROUNDTRIP_TIMEOUT:
    		status = HttpStatus.NOT_FOUND;
    		break;
    		default:
    			status = HttpStatus.INTERNAL_SERVER_ERROR;
    			break;
    	}
	    NewtoolExceptionInformation exceptionInformation = new NewtoolExceptionInformation(e.getMessage());
	    return new ResponseEntity<NewtoolExceptionInformation>(exceptionInformation, status);
    }
}
