package com._3po_labs.alexa_model_tester.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import com._3po_labs.alexa_model_tester.exception.AlexaModelTesterException;
import com._3po_labs.alexa_model_tester.messagequeue.AlexaRequestQueue;
import com._3po_labs.alexa_model_tester.model.SendAudioRequest;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.threepio_labs.avsclient.client.AVSClient;
import com.threepio_labs.avsclient.model.RecognizeSpeechRequest;

@Controller
@Scope("request")
public class AudioFileRequestController {
	
	@Autowired
	private ApplicationContext appContext;
	
	private AlexaRequestQueue alexaRequestQueue = AlexaRequestQueue.getInstance();
	
    @RequestMapping(value = "/rest/audioFileRequest", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody SpeechletRequestEnvelope sendAudioFile(@RequestBody SendAudioRequest body, @RequestHeader(value="Authorization") String authorizationHeader) throws AlexaModelTesterException {
    	AVSClient client = appContext.getBean("avsClient", AVSClient.class);
    	client.recognizeSpeechAsync(authorizationHeader, new RecognizeSpeechRequest(),body.getAudioFileUrl());
    	//SWEET JESUS MAKE SURE THIS WAS SUCCESSFUL BEFORE DOING THE CALL BELOW
    	SpeechletRequestEnvelope alexaRequest = waitForAlexaRequest(body.getUserId(),body.getApplicationId());
    	if(alexaRequest != null){
    		return alexaRequest;
    	}else{
    		System.out.println("output was null.");
    	}
        throw new AlexaModelTesterException("No response found in the alotted time.");
    }

	private SpeechletRequestEnvelope waitForAlexaRequest(String userId, String applicationId) {

//		alexaRequestQueue.registerThread(this);
		SpeechletRequestEnvelope alexaRequest = alexaRequestQueue.getAlexaResponse(userId, applicationId);
		if(alexaRequest != null){
			return alexaRequest;
		}
		
		Object mutex = alexaRequestQueue.getMutex();
		long maxWait = 5000;
		long endTime = System.currentTimeMillis() + maxWait;
		while(true){
			long currentTime = System.currentTimeMillis();
			if(currentTime >= endTime){
				System.out.println("Past endtime, exiting.");
				return null;
			}
			System.out.println("Waiting.");
			
			try {
				
				synchronized(mutex){
					mutex.wait(endTime - currentTime);
				}
				
				System.out.println("Wait finished, returning null.");
				return null;
			} catch (InterruptedException e) {
				System.out.println("Interrupted");
				alexaRequest = alexaRequestQueue.getAlexaResponse(userId, applicationId);
				if(alexaRequest != null){
					return alexaRequest;
				}
			}
		}
	}
	
	class AsynchronousCall implements Runnable{

		public void run() {
		}
		
	}
}
