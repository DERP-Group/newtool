package com._3po_labs.alexa_model_tester.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import com._3po_labs.alexa_model_tester.messagequeue.AlexaRequestQueue;
import com.amazon.speech.json.SpeechletRequestEnvelope;

@Controller
public class AlexaRequestController {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ApplicationContext appContext;
	
	private AlexaRequestQueue alexaRequestQueue = AlexaRequestQueue.getInstance();
	
    @RequestMapping(value = "/rest/alexaRequest", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody ResponseEntity<String> recordAlexaRequest(@Valid @RequestBody SpeechletRequestEnvelope body) {
 
    	registerAlexaRequest(body);
        return new ResponseEntity<String>( HttpStatus.NO_CONTENT);
    }
    
    public void registerAlexaRequest(SpeechletRequestEnvelope body){
    	String userId = body.getSession().getUser().getUserId();
    	String applicationId = body.getSession().getApplication().getApplicationId(); 
    	LOG.debug("User id: " + userId);
    	LOG.debug("Application id: " + applicationId);
    	
    	alexaRequestQueue.addAlexaResponse(userId, applicationId, body);
    	alexaRequestQueue.notifyAllThreads();
    }
}
