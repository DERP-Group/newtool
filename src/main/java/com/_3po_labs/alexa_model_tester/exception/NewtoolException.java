package com._3po_labs.alexa_model_tester.exception;

public class NewtoolException extends RuntimeException{

	private static final long serialVersionUID = -2884336297715991794L;
	
	private NewtoolExceptionCode exceptionCode;

	public NewtoolException(String message, NewtoolExceptionCode exceptionCode){
		super(message);
		this.exceptionCode = exceptionCode; 
	}

	public NewtoolExceptionCode getExceptionCode() {
		return exceptionCode;
	}
}
