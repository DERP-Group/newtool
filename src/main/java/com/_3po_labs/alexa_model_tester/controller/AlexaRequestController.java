package com._3po_labs.alexa_model_tester.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import com._3po_labs.alexa_model_tester.messagequeue.AlexaRequestQueue;
import com._3po_labs.alexa_model_tester.model.SendAudioRequest;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.threepio_labs.avsclient.client.AVSClient;
import com.threepio_labs.avsclient.model.RecognizeSpeechRequest;

@Controller
@Scope("request")
public class AlexaRequestController {
	
	@Autowired
	private ApplicationContext appContext;
	
	private AlexaRequestQueue alexaRequestQueue = AlexaRequestQueue.getInstance();
	
    @RequestMapping(value = "/rest/alexaRequest", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody ResponseEntity<String> recordAlexaRequest(@RequestBody SpeechletRequestEnvelope body) {
 
    	registerAlexaRequest(body);
        return new ResponseEntity<String>( HttpStatus.NO_CONTENT);
    }
    
    public void registerAlexaRequest(SpeechletRequestEnvelope body){
    	String userId = body.getSession().getUser().getUserId();
    	String applicationId = body.getSession().getApplication().getApplicationId(); 
    	System.out.println("User id: " + userId);
    	System.out.println("Application id: " + applicationId);
    	
    	alexaRequestQueue.addAlexaResponse(userId, applicationId, body);
    	System.out.println("Trying to notify.");
    	alexaRequestQueue.notifyAllThreads();
    }
}
