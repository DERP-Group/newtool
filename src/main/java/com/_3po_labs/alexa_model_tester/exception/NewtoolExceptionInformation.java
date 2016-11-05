package com._3po_labs.alexa_model_tester.exception;

import java.util.UUID;

public class NewtoolExceptionInformation {

	private String message;
	private UUID id;
	
	public NewtoolExceptionInformation(String message){
		id = UUID.randomUUID();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public UUID getId() {
		return id;
	}
}
